(ns puerto-rico.ai.selfplay
  "Self-play training-data generation and evaluation arena (JVM only).

   Games run in parallel across all CPU cores; MCTS uses a per-thread RNG so
   the workers don't contend on a shared random generator.

   Usage:
     clj -M:selfplay generate --games 50 --sims 200 --out data/selfplay.jsonl
     clj -M:selfplay arena    --games 20 --sims 100

   Each JSONL line is one decision:
     {\"s\": [floats]     ; encoded state, egocentric (seat 0 = the actor)
      \"p\": [98 floats]  ; MCTS visit distribution over the action space
      \"v\": [n floats]   ; final win/loss outcome, rotated so index 0 = actor
      \"sm\": [n floats]} ; final SCORE MARGINS (score - table average),
                         ; rotated so index 0 = actor; sums to 0

   The margin target (sm) feeds the auxiliary score-margin head: predicting how
   far ahead/behind the pack each player ends is a far richer, zero-centered
   signal than win/loss alone. It also drives the MCTS utility blend so the AI
   keeps maximizing its point lead even when the win is already decided."
  (:require [cheshire.core :as json]
            [clj-async-profiler.core :as prof]
            [clojure.java.io :as io]
            [clojure.set :as set]
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
;; Evaluators: a model spec is nil / "rollout" (random playouts) or a path to
;; an ONNX model. Loaded models are cached so a champion shared across seats
;; and games opens exactly one ONNX session.
;; --------------------------------------------------------------------------

(def ^:private model-cache (atom {}))

(defn evaluator-for
  "MCTS evaluator for a model spec (nil/'rollout' -> random rollouts)"
  [spec]
  (if (or (nil? spec) (= spec "rollout"))
    (mcts/rollout-evaluator)
    (nn/evaluator (or (get @model-cache spec)
                      (get (swap! model-cache assoc spec (nn/load-model spec)) spec)))))

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
            winner-idx (first (keep-indexed #(when (= 1.0 %2) %1) outcome))]
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

(defn generate!
  "Play games in parallel and append training examples to a JSONL file.
   With :model set, self-play is guided by that ONNX network; otherwise it
   uses random rollouts (generation 0)."
  [{:keys [games out threads model] :or {threads n-cores} :as opts}]
  (io/make-parents out)
  (let [opts (assoc opts :evaluate (evaluator-for model))
        _ (println (format "Self-play with %s" (if model (str "model " model) "random rollouts")))
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
        total-ex (reduce + (map #(count (:examples %)) results))]
    (println (format "Running %d games on %d threads..." games threads))
    (with-open [w (io/writer out :append true)]
      (doseq [r results, ex (:examples r)]
        (.write w (json/generate-string ex))
        (.write w "\n")))
    (let [secs (/ (- (System/currentTimeMillis) t0) 1000.0)]
      (println (format "\nWrote %d examples from %d games to %s in %.1fs (%.0f examples/s)"
                       total-ex games out secs (/ total-ex secs))))))

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
  [{:keys [challenger champion games players simulations threads]
    :or {games 30 players 3 simulations 100 threads n-cores}}]
  (println (format "Versus: challenger=%s  champion=%s  (%d games, %d sims, %d threads)"
                   challenger (or champion "rollout") games simulations threads))
  (let [chal-ev (evaluator-for challenger)
        champ-ev (evaluator-for champion)
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
  [{:keys [games players threads] :or {games 20 players 3 threads n-cores} :as opts}]
  (println (format "Running %d arena games on %d threads..." games threads))
  (let [outcomes (par-map threads
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

(defn- parse-args [args]
  (into {} (map (fn [[k v]]
                  [(keyword (subs k 2))
                   (if (re-matches #"-?\d+" v) (parse-long v) v)])
                (partition 2 args))))

(defn -main [& [cmd & args]]
  (let [opts (set/rename-keys (parse-args args) {:sims :simulations})]
    (case cmd
      "generate" (prof/profile (generate! (merge {:games 10 :simulations 200 :out "data/selfplay.jsonl"} opts)))
      "arena"    (arena (merge {:games 20 :simulations 100} opts))
      "versus"   (versus (merge {:games 100 :simulations 400} opts))
      (println (str "usage:\n"
                    "  clj -M:selfplay generate --games N --sims N [--model M.onnx] [--threads N] --out FILE\n"
                    "  clj -M:selfplay arena    --games N --sims N [--threads N]        (MCTS vs heuristic)\n"
                    "  clj -M:selfplay versus   --challenger A.onnx [--champion B.onnx] --games N --sims N"))))
  (shutdown-agents))
