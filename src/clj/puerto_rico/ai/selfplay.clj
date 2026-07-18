(ns puerto-rico.ai.selfplay
  "Self-play training-data generation and evaluation arena (JVM only).

   Usage:
     clj -M:selfplay generate --games 50 --sims 200 --out data/selfplay.jsonl
     clj -M:selfplay arena    --games 20 --sims 100

   Each JSONL line is one decision:
     {\"s\": [306 floats]   ; encoded state, egocentric
      \"p\": [54 floats]    ; MCTS visit distribution over the action space
      \"v\": [n floats]}    ; final outcome, rotated so index 0 = the actor"
  (:require [cheshire.core :as json]
            [clojure.java.io :as io]
            [clojure.set :as set]
            [puerto-rico.game.state :as state]
            [puerto-rico.game.rules :as rules]
            [puerto-rico.ai.actions :as actions]
            [puerto-rico.ai.encoder :as encoder]
            [puerto-rico.ai.mcts :as mcts]
            [puerto-rico.ai.heuristic :as heuristic]))

(defn- mk-game [n]
  (state/new-game-state (mapv #(state/new-player (inc %) (str "P" (inc %))) (range n))))

(defn- dense-policy [visits]
  (let [total (double (max 1 (reduce + (vals visits))))]
    (mapv (fn [id] (/ (double (get visits id 0)) total))
          (range actions/num-actions))))

(defn- one-hot-policy [id]
  (assoc (vec (repeat actions/num-actions 0.0)) id 1.0))

(defn- rotate [outcome seat]
  (let [n (count outcome)]
    (mapv #(nth outcome (mod (+ seat %) n)) (range n))))

(defn play-training-game
  "Play one all-MCTS game; returns {:examples [...] :winner-idx int :rounds int}"
  [{:keys [players simulations temp-moves evaluate max-decisions]
    :or {players 3 simulations 200 temp-moves 20 max-decisions 1500}}]
  (loop [gs (mk-game players), examples [], decision 0]
    (cond
      (:game-over gs)
      (let [outcome (mcts/outcome-vector gs)
            winner-idx (first (keep-indexed #(when (= 1.0 %2) %1) outcome))]
        {:examples (mapv (fn [{:keys [state policy seat]}]
                           {:s state :p policy :v (rotate outcome seat)})
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

(defn generate!
  "Play games and append training examples to a JSONL file"
  [{:keys [games out] :as opts}]
  (io/make-parents out)
  (with-open [w (io/writer out :append true)]
    (dotimes [i games]
      (let [t0 (System/currentTimeMillis)
            {:keys [examples winner-idx rounds]} (play-training-game opts)]
        (doseq [ex examples]
          (.write w (json/generate-string ex))
          (.write w "\n"))
        (println (format "game %d/%d: %d examples, winner seat %d, %d rounds, %.1fs"
                         (inc i) games (count examples) winner-idx rounds
                         (/ (- (System/currentTimeMillis) t0) 1000.0)))))))

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
    :or {players 3 simulations 100 mcts-seat 0 max-decisions 1500}}]
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
  "Play games with the MCTS seat rotating; report the MCTS win rate.
   With 3 players, a bot no better than its opponents wins ~33%."
  [{:keys [games players] :or {games 20 players 3} :as opts}]
  (let [wins (reduce (fn [wins i]
                       (let [seat (mod i players)
                             win? (play-arena-game (assoc opts :mcts-seat seat))]
                         (println (format "game %d/%d: MCTS seat %d %s"
                                          (inc i) games seat (if win? "WIN" "loss")))
                         (if win? (inc wins) wins)))
                     0
                     (range games))]
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
      (println "usage: clj -M:selfplay generate --games N --sims N --out FILE\n"
               "      clj -M:selfplay arena --games N --sims N")))
  (shutdown-agents))
