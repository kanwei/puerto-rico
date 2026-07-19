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
      \"z\": [n floats]}  ; final normalized scores, rotated so index 0 = actor

   The score target (z) is the auxiliary 'score margin' head: predicting how
   many points everyone ends with is a far richer signal than win/loss alone
   and speeds up learning."
  (:require [cheshire.core :as json]
            [clojure.java.io :as io]
            [clojure.set :as set]
            [puerto-rico.game.state :as state]
            [puerto-rico.game.rules :as rules]
            [puerto-rico.ai.actions :as actions]
            [puerto-rico.ai.encoder :as encoder]
            [puerto-rico.ai.mcts :as mcts]
            [puerto-rico.ai.heuristic :as heuristic]))

(def score-norm
  "Divisor mapping a final VP total to roughly [0,1] for the score head"
  60.0)

(defn- mk-game [n]
  (state/new-game-state (mapv #(state/new-player (inc %) (str "P" (inc %))) (range n))))

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

(defn- score-vector [gs]
  (mapv #(min 1.0 (/ (double (state/calculate-victory-points %)) score-norm))
        (:players gs)))

(defn play-training-game
  "Play one all-MCTS game; returns {:examples [...] :winner-idx int :rounds int}"
  [{:keys [players simulations temp-moves evaluate max-decisions]
    :or {players 3 simulations 200 temp-moves 20 max-decisions 2000}}]
  (loop [gs (mk-game players), examples [], decision 0]
    (cond
      (:game-over gs)
      (let [outcome (mcts/outcome-vector gs)
            scores (score-vector gs)
            winner-idx (first (keep-indexed #(when (= 1.0 %2) %1) outcome))]
        {:examples (mapv (fn [{:keys [state policy seat]}]
                           {:s state :p policy
                            :v (rotate outcome seat)
                            :z (rotate scores seat)})
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
  "Play games in parallel and append training examples to a JSONL file"
  [{:keys [games out threads] :or {threads n-cores} :as opts}]
  (io/make-parents out)
  (let [t0 (System/currentTimeMillis)
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
  (let [opts (parse-args args)]
    (case cmd
      "generate" (generate! (merge {:games 10 :simulations 200 :out "data/selfplay.jsonl"}
                                   (set/rename-keys opts {:sims :simulations})))
      "arena" (arena (merge {:games 20 :simulations 100}
                            (set/rename-keys opts {:sims :simulations})))
      (println "usage: clj -M:selfplay generate --games N --sims N [--threads N] --out FILE\n"
               "      clj -M:selfplay arena --games N --sims N [--threads N]")))
  (shutdown-agents))
