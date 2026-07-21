(ns puerto-rico.ai.selfplay
  "Self-play training-data generation and evaluation arena (JVM only).

   Games run in parallel across all CPU cores; MCTS uses a per-thread RNG so
   the workers don't contend on a shared random generator.

   Usage:
     clj -M:selfplay generate --games 50 --sims 200 --out data/p3-gen1.bin
     clj -M:selfplay arena    --games 20 --sims 100

   Output is a raw little-endian float32 matrix in `p<N>-<gen>-<S>-<A>.bin`, one
   row per decision laid out as:
     [ state (S floats)  ; encoded state, egocentric (seat 0 = the actor)
       policy (A floats)  ; MCTS visit distribution over the action space
       value  (N floats)  ; final win/loss outcome, rotated so index 0 = actor
       sm     (N floats) ] ; final SCORE MARGINS (score - table average),
                           ; rotated so index 0 = actor; sums to 0
   S/A are in the filename and N (players) is the `p<N>-` prefix, so a reader has
   the full column layout and windows combine by plain byte concatenation.

   The margin target (sm) feeds the auxiliary score-margin head: predicting how
   far ahead/behind the pack each player ends is a far richer, zero-centered
   signal than win/loss alone. It also drives the MCTS utility blend so the AI
   keeps maximizing its point lead even when the win is already decided."
  (:require [cheshire.core :as json]
            [clojure.java.io :as io]
            [clojure.set :as set]
            [clojure.string :as str]
            [puerto-rico.game.state :as state]
            [puerto-rico.game.rules :as rules]
            [puerto-rico.ai.actions :as actions]
            [puerto-rico.ai.encoder :as encoder]
            [puerto-rico.ai.mcts :as mcts]
            [puerto-rico.ai.nn :as nn]
            [puerto-rico.ai.heuristic :as heuristic]))

(defn- mk-game [n]
  ;; every seat is AI-driven in self-play / arena / eval, so mark them :is-ai -
  ;; this selects the engine's clear-and-refill mayor path (what the AI trains on)
  (state/new-game-state (mapv #(assoc (state/new-player (inc %) (str "P" (inc %))) :is-ai true)
                              (range n))))

;; --------------------------------------------------------------------------
;; Evaluators: a model spec is nil / "rollout" (playout MCTS) or a path to an
;; ONNX model. Loaded models are cached so a champion shared across seats and
;; games opens exactly one ONNX session.
;; --------------------------------------------------------------------------

(def ^:private model-cache (atom {}))

(defn- weighted-choice
  "Pick a key from a {key weight} map with probability proportional to weight,
   using the per-thread RNG."
  [m]
  (let [entries (seq m)
        total (reduce + 0.0 (map second entries))
        r (* (mcts/drand) total)]
    (loop [acc 0.0, [[k w] & more] entries]
      (if (or (nil? more) (>= (+ acc w) r)) k (recur (+ acc w) more)))))

(defn heuristic-playout-policy
  "Playout move-selector that samples proportionally to the heuristic's action
   scores where it has an opinion (settler takes, builder builds, mayor
   placements - exactly the decisions whose long-horizon value a random playout
   can't see), and falls back to a uniform random legal move elsewhere. Uses
   `actions/heuristic-action-scores` (id-keyed, allocation-light, and - unlike
   the full heuristic move-selectors - it does NOT print, so it is safe in the
   hot playout loop)."
  [game-state legal-ids]
  (if-let [scores (actions/heuristic-action-scores game-state)]
    (weighted-choice scores)
    (mcts/drand-nth legal-ids)))

(defn- rollout-spec? [spec]
  (or (nil? spec) (#{"rollout" "random" "heuristic"} spec)))

(defn evaluator-for
  "MCTS evaluator for a model spec. Rollout specs (nil / \"rollout\" / \"random\"
   / \"heuristic\") give a playout evaluator; anything else is an ONNX model path.

   opts:
     :utility-c  score-margin weight for NN search (U = win-prob + c*margin); a
                 larger c lets the dense, reliable margin head drive the search.
                 nil = nn's own default (0.05). Ignored for rollouts.
     :rollout    :random (default) or :heuristic - the playout policy for rollout
                 evaluators. Heuristic playouts give a far better value estimate
                 for this game (a random playout can't tell that taking a
                 plantation / building actually helps). spec \"heuristic\"/\"random\"
                 force the kind regardless of :rollout."
  ([spec] (evaluator-for spec nil))
  ([spec {:keys [utility-c rollout] :or {rollout :random}}]
   (if (rollout-spec? spec)
     (let [kind (case spec "heuristic" :heuristic, "random" :random, rollout)
           policy (if (= kind :heuristic)
                    heuristic-playout-policy
                    mcts/random-playout-policy)]
       (mcts/blend-heuristic-priors
        (mcts/rollout-evaluator :playout-policy policy) {}))
     (nn/evaluator (or (get @model-cache spec)
                       (get (swap! model-cache assoc spec (nn/load-model spec)) spec))
                   (when utility-c {:utility-c utility-c})))))

(defn- dense-policy [visits]
  (let [total (double (max 1 (reduce + (vals visits))))]
    (mapv (fn [id] (/ (double (get visits id 0)) total))
          (range actions/num-actions))))

(defn- one-hot-policy [id]
  (assoc (vec (repeat actions/num-actions 0.0)) id 1.0))

(defn- rotate
  "Rotate a per-seat vector so index 0 is the given seat's own value"
  [v seat]
  (let [n (count v)]
    (mapv #(nth v (mod (+ seat %) n)) (range n))))

(defn- score-margin-vector
  "Each player's final score minus the table average (in points). Zero-centered
   and sums to 0 - a stable target regardless of whether the game ran high or
   low scoring."
  [gs]
  (let [scores (mapv #(double (state/calculate-victory-points %)) (:players gs))
        avg (/ (reduce + scores) (count scores))]
    (mapv #(- % avg) scores)))

(defn play-training-game
  "Play one all-MCTS game; returns {:examples [...] :winner-idx int :rounds int}"
  [{:keys [players simulations temp-moves evaluate max-decisions]
    :or {players 3 simulations 200 temp-moves 20 max-decisions 2000}}]
  (loop [gs (mk-game players), examples [], decision 0]
    (cond
      (:game-over gs)
      (let [outcome (mcts/outcome-vector gs)
            margins (score-margin-vector gs)
            ;; take the winner straight from the engine, not by scanning the
            ;; outcome vector for a 1.0 (that broke when the reward was blended)
            winner-idx (state/player-index gs (get-in gs [:winner :id]))]
        {:examples (mapv (fn [{:keys [state policy seat]}]
                           {:s state :p policy
                            :v (rotate outcome seat)
                            :sm (rotate margins seat)})
                         examples)
         :winner-idx winner-idx
         :rounds (:round gs)})

      (> decision max-decisions)
      (throw (ex-info "self-play game did not terminate" {:decisions decision}))

      :else
      (let [seat (actions/actor-index gs)
            legal (actions/legal-action-ids gs)
            encoded (encoder/encode-state gs)
            [policy chosen]
            (if (= 1 (count legal))
              ;; forced move: no search needed, policy is one-hot
              [(one-hot-policy (first legal)) (first legal)]
              (let [{:keys [visits]} (mcts/mcts-search
                                      gs {:simulations simulations
                                          :dirichlet-frac 0.25
                                          :evaluate evaluate})
                    temp (if (< decision temp-moves) 1.0 0.0)]
                [(dense-policy visits) (mcts/sample-action visits temp)]))]
        (recur (actions/apply-action gs chosen)
               (conj examples {:state encoded :policy policy :seat seat})
               (inc decision))))))

;; --------------------------------------------------------------------------
;; Parallel driver: realize (pmap f xs) with a fixed pool sized to the machine
;; --------------------------------------------------------------------------

(def n-cores (.availableProcessors (Runtime/getRuntime)))

(defn- par-map
  "Like pmap but with a bounded pool of `threads` workers (CPU-bound work).
   Returns a lazy seq of results in input order."
  [threads f coll]
  (let [pool (java.util.concurrent.Executors/newFixedThreadPool threads)]
    (try
      (->> coll
           (mapv (fn [x] (.submit pool ^Callable (fn [] (f x)))))
           (mapv (fn [^java.util.concurrent.Future fut] (.get fut))))
      (finally (.shutdown pool)))))

(defn- write-examples-bin!
  "Write examples as a raw little-endian float32 matrix, one row per example laid
   out as [state | policy | value | score-margin]. Returns [final-path stride].

   The out path's extension is replaced with a `-<S>-<A>.bin` suffix so the file
   is self-describing: S = state floats, A = policy/action floats, and the player
   count (which fixes the value + margin widths, N each) is already in the
   `p<N>-` filename prefix. Every generation for a given player count therefore
   shares one row stride, so training windows are combined by plain byte concat."
  [out examples]
  (let [ex0    (first examples)
        s-dim  (count (:s ex0))
        a-dim  (count (:p ex0))
        n      (count (:v ex0))
        stride (+ s-dim a-dim n n)
        base   (str/replace out #"\.(bin|jsonl)$" "")
        final  (str base "-" s-dim "-" a-dim ".bin")
        bb     (doto (java.nio.ByteBuffer/allocate (* 4 stride))
                 (.order java.nio.ByteOrder/LITTLE_ENDIAN))]
    (io/make-parents final)
    (with-open [os (java.io.BufferedOutputStream. (java.io.FileOutputStream. final))]
      (doseq [{:keys [s p v sm]} examples]
        (.clear bb)
        (doseq [x s]  (.putFloat bb (float x)))
        (doseq [x p]  (.putFloat bb (float x)))
        (doseq [x v]  (.putFloat bb (float x)))
        (doseq [x sm] (.putFloat bb (float x)))
        (.write os (.array bb) 0 (.position bb))))
    [final stride]))

(defn generate!
  "Play games in parallel and write training examples to a raw float32 .bin file
   (see `write-examples-bin!` for the layout). With :model set, self-play is
   guided by that ONNX network; otherwise it uses playout MCTS (generation 0),
   with :rollout selecting the playout policy (:random or :heuristic)."
  [{:keys [games out threads model utility-c rollout]
    :or {threads n-cores out "data/selfplay.bin" rollout "random"} :as opts}]
  (let [rollout-kw (keyword rollout)
        opts (assoc opts :evaluate (evaluator-for model {:utility-c utility-c :rollout rollout-kw}))
        _ (println (format "Self-play with %s%s"
                           (if model (str "model " model)
                               (str (name rollout-kw) " rollouts"))
                           (if (and model utility-c) (format " (utility-c=%.2f)" (double utility-c)) "")))
        t0 (System/currentTimeMillis)
        done (atom 0)
        results (par-map threads
                         (fn [_]
                           (let [r (play-training-game opts)]
                             (println (format "  game %d/%d done: %d examples, winner seat %d, %d rounds"
                                              (swap! done inc) games
                                              (count (:examples r)) (:winner-idx r) (:rounds r)))
                             r))
                         (range games))
        examples (into [] (mapcat :examples) results)
        total-ex (count examples)]
    (println (format "Running %d games on %d threads..." games threads))
    (if (zero? total-ex)
      (println "No examples generated - nothing written.")
      (let [[final stride] (write-examples-bin! out examples)
            secs (/ (- (System/currentTimeMillis) t0) 1000.0)]
        (println (format "\nWrote %d examples (%d floats/row) from %d games to %s in %.1fs (%.0f examples/s)"
                         total-ex stride games final secs (/ total-ex secs)))))))

;; --------------------------------------------------------------------------
;; Per-seat model play: each seat can use a different network. This is how the
;; engine "picks a model per player" and how a new generation is measured
;; against the previous one.
;; --------------------------------------------------------------------------

(defn play-eval-game
  "Play one game where seat i is driven by evaluators[i]. Returns winning seat."
  [{:keys [players simulations evaluators max-decisions]
    :or {players 3 simulations 100 max-decisions 2000}}]
  (loop [gs (mk-game players), decision 0]
    (cond
      (:game-over gs)
      (state/player-index gs (get-in gs [:winner :id]))

      (> decision max-decisions)
      (throw (ex-info "eval game did not terminate" {:decisions decision}))

      :else
      (let [seat (actions/actor-index gs)
            pid (:id (actions/actor-player gs))
            move (mcts/ai-select-move gs pid {:simulations simulations
                                              :evaluate (nth evaluators seat)})]
        (recur (rules/apply-move gs move) (inc decision))))))

(defn versus
  "Head-to-head evaluation: the challenger occupies one seat and the champion
   the rest, with the challenger's seat rotating for fairness. Reports the
   challenger's win rate (a challenger no stronger than the champion wins the
   1/players baseline). champion nil/'rollout' pits the challenger vs rollouts.
   Prints a machine-readable RESULT line for the training loop."
  [{:keys [challenger champion games players simulations threads utility-c rollout]
    :or {games 30 players 3 simulations 100 threads n-cores rollout "random"}}]
  (println (format "Versus: challenger=%s  champion=%s  (%d games, %d sims, %d threads%s)"
                   challenger (or champion (str (name (keyword rollout)) "-rollout"))
                   games simulations threads
                   (if utility-c (format ", utility-c=%.2f" (double utility-c)) "")))
  (let [ev-opts {:utility-c utility-c :rollout (keyword rollout)}
        chal-ev (evaluator-for challenger ev-opts)
        champ-ev (evaluator-for champion ev-opts)
        outcomes (par-map threads
                          (fn [i]
                            (let [chal-seat (mod i players)
                                  evs (mapv #(if (= % chal-seat) chal-ev champ-ev)
                                            (range players))
                                  winner (play-eval-game {:players players
                                                          :simulations simulations
                                                          :evaluators evs})]
                              (= winner chal-seat)))
                          (range games))
        wins (count (filter true? outcomes))
        winrate (double (/ wins games))]
    (println (format "\nChallenger won %d/%d (%.1f%%) - baseline for equal strength is %.1f%%"
                     wins games (* 100.0 winrate) (/ 100.0 players)))
    (println (str "RESULT " (json/generate-string
                             {:challenger challenger :champion (or champion "rollout")
                              :wins wins :games games :winrate winrate
                              :baseline (/ 1.0 players)})))
    {:wins wins :games games :winrate winrate}))

;; --------------------------------------------------------------------------
;; Arena: MCTS vs the heuristic AI
;; --------------------------------------------------------------------------

(defn- ai-move [gs mcts-seat simulations evaluate]
  (let [seat (actions/actor-index gs)
        pid (:id (actions/actor-player gs))]
    (if (= seat mcts-seat)
      (mcts/ai-select-move gs pid {:simulations simulations :evaluate evaluate})
      (heuristic/ai-select-move gs pid))))

(defn play-arena-game
  "One game: MCTS in the given seat, heuristic AI elsewhere.
   Returns true when the MCTS player wins."
  [{:keys [players simulations mcts-seat evaluate max-decisions]
    :or {players 3 simulations 100 mcts-seat 0 max-decisions 2000}}]
  (loop [gs (mk-game players), decision 0]
    (cond
      (:game-over gs)
      (= mcts-seat (state/player-index gs (get-in gs [:winner :id])))

      (> decision max-decisions)
      (throw (ex-info "arena game did not terminate" {:decisions decision}))

      :else
      (recur (rules/apply-move gs (ai-move gs mcts-seat simulations evaluate))
             (inc decision)))))

(defn arena
  "Play games in parallel with the MCTS seat rotating; report the win rate.
   With 3 players, a bot no better than its opponents wins ~33%."
  [{:keys [games players threads model utility-c rollout]
    :or {games 20 players 3 threads n-cores rollout "random"} :as opts}]
  (println (format "Running %d arena games on %d threads (MCTS=%s)..."
                   games threads (if model (str "model " model) (str (name (keyword rollout)) " rollouts"))))
  (let [opts (assoc opts :evaluate
                    (evaluator-for model {:utility-c utility-c :rollout (keyword rollout)}))
        outcomes (par-map threads
                          (fn [i]
                            (let [seat (mod i players)
                                  win? (play-arena-game (assoc opts :mcts-seat seat))]
                              (println (format "  game %d/%d: MCTS seat %d %s"
                                               (inc i) games seat (if win? "WIN" "loss")))
                              win?))
                          (range games))
        wins (count (filter true? outcomes))]
    (println (format "\nMCTS won %d/%d (%.0f%%) - baseline for equal strength is %.0f%%"
                     wins games (* 100.0 (/ wins games)) (/ 100.0 players)))
    {:wins wins :games games}))

;; --------------------------------------------------------------------------
;; CLI
;; --------------------------------------------------------------------------

(defn- parse-arg-val [v]
  (cond
    (re-matches #"-?\d+" v) (parse-long v)
    (re-matches #"-?\d+\.\d+" v) (parse-double v)   ;; e.g. --utility-c 0.5
    :else v))

(defn- parse-args [args]
  (into {} (map (fn [[k v]] [(keyword (subs k 2)) (parse-arg-val v)])
                (partition 2 args))))

(defn -main [& [cmd & args]]
  (let [opts (set/rename-keys (parse-args args) {:sims :simulations})]
    (case cmd
      "generate" (generate! (merge {:games 10 :simulations 200 :out "data/selfplay.bin"} opts))
      "arena"    (arena (merge {:games 20 :simulations 100} opts))
      "versus"   (versus (merge {:games 100 :simulations 400} opts))
      (println (str "usage (--utility-c C weights the score-margin head in NN search;\n"
                    "       --rollout heuristic|random picks the gen-0 playout policy):\n"
                    "  clj -M:selfplay generate --games N --sims N [--model M.onnx] [--utility-c C] [--rollout K] [--threads N] --out FILE\n"
                    "  clj -M:selfplay arena    --games N --sims N [--model M.onnx] [--utility-c C] [--rollout K] [--threads N]   (MCTS vs heuristic)\n"
                    "  clj -M:selfplay versus   --challenger A.onnx [--champion B.onnx] [--utility-c C] [--rollout K] --games N --sims N"))))
  (shutdown-agents))
