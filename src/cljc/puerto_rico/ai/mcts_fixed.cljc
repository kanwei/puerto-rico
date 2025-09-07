(ns puerto-rico.ai.mcts-fixed
  "Fixed Monte Carlo Tree Search implementation for Puerto Rico AI players"
  (:require [puerto-rico.game.state :as state]
            [puerto-rico.game.rules :as rules]))

;; Simplified MCTS with mutable statistics
(defn get-possible-moves [game-state]
  "Generate all possible moves for the current game state"
  (case (:phase game-state)
    :role-selection
    (vec (:available-roles game-state))

    :role-execution
    (let [role (:selected-role game-state)
          current-player (state/current-player game-state)]
      (case role
        :settler
        (vec (keys (filter #(pos? (val %)) (:plantation-supply game-state))))

        :builder
        (vec (keys (filter (fn [[building info]]
                             (rules/can-build-building? current-player building info))
                           state/buildings)))

        :trader
        (vec (filter #(rules/can-trade-good? game-state current-player %)
                     state/goods))

        :captain
        (vec (filter #(pos? (get-in current-player [:goods %] 0))
                     state/goods))

        ;; For roles with no choices
        [:execute]))

    []))

(defn apply-move-to-game [game-state move]
  "Apply a move to the game state"
  (case (:phase game-state)
    :role-selection
    (rules/select-role game-state (:id (state/current-player game-state)) move)

    :role-execution
    (let [role (:selected-role game-state)
          player-id (:id (state/current-player game-state))]
      (case role
        (:mayor :craftsman :prospector)
        (-> (rules/execute-role game-state role player-id)
            (rules/end-role-execution))

        (-> (rules/execute-role game-state role player-id move)
            (rules/end-role-execution))))

    game-state))

(defn random-playout [game-state max-depth]
  "Simple random playout"
  (loop [state game-state depth 0]
    (if (or (>= depth max-depth)
            (:game-over state)
            (state/check-victory-conditions state))
      state
      (let [possible-moves (get-possible-moves state)]
        (if (empty? possible-moves)
          state
          (let [random-move (rand-nth possible-moves)
                new-state (apply-move-to-game state random-move)]
            (recur new-state (inc depth))))))))

(defn evaluate-game-state [game-state player-id]
  "Evaluate final game state from player's perspective"
  (let [players (:players game-state)
        player-scores (mapv state/calculate-victory-points players)
        player-idx (->> players
                        (map-indexed vector)
                        (filter #(= (:id (second %)) player-id))
                        first
                        first)
        player-score (nth player-scores player-idx)
        max-score (apply max player-scores)
        avg-opponent-score (/ (- (reduce + player-scores) player-score)
                              (dec (count player-scores)))]
    (cond
      (= player-score max-score) 1.0
      (>= player-score avg-opponent-score) 0.6
      :else 0.3)))

(defn simple-mcts [game-state player-id iterations]
  "Simplified MCTS that tracks statistics for each possible move"
  (let [possible-moves (get-possible-moves game-state)
        move-stats (atom (zipmap possible-moves
                                 (repeat {:wins 0 :visits 0})))]

    (when (seq possible-moves)
      (dotimes [_ iterations]
        (let [selected-move (if (< (rand) 0.3) ; 30% exploration
                              (rand-nth possible-moves)
                             ;; Select best UCB1 move
                              (let [total-visits (reduce + (map :visits (vals @move-stats)))]
                                (if (zero? total-visits)
                                  (rand-nth possible-moves)
                                  (->> possible-moves
                                       (map (fn [move]
                                              (let [stats (get @move-stats move)
                                                    visits (:visits stats)
                                                    wins (:wins stats)]
                                                [move (if (zero? visits)
                                                        Double/POSITIVE_INFINITY
                                                        (+ (/ wins visits)
                                                           (* 1.414 (Math/sqrt (/ (Math/log total-visits) visits)))))])))
                                       (sort-by second >)
                                       first
                                       first))))

              new-state (apply-move-to-game game-state selected-move)
              simulation-result (-> new-state
                                    (random-playout 30)
                                    (evaluate-game-state player-id))]

          (swap! move-stats update selected-move
                 (fn [stats]
                   (-> stats
                       (update :visits inc)
                       (update :wins + simulation-result))))))

      ;; Return move with highest win rate
      (when-let [best-move-entry (->> @move-stats
                                      (filter #(pos? (:visits (val %))))
                                      (sort-by #(/ (:wins (val %)) (:visits (val %))))
                                      last)]
        (let [[move stats] best-move-entry]
          (println (str "AI selected " move " (wins: " (:wins stats)
                        " visits: " (:visits stats)
                        " win-rate: " (float (/ (:wins stats) (:visits stats))) ")"))
          move)))))

;; Main AI interface
(defn ai-select-move [game-state player-id difficulty]
  "AI selects a move using simplified MCTS"
  (let [iterations (case difficulty
                     :easy 5 ; Reduced for demo
                     :medium 10
                     :hard 20
                     10)
        move (simple-mcts game-state player-id iterations)]
    (println (str "AI Player " player-id " thinking... Selected: " move))
    (when move
      ;; Convert move back to the format expected by the game engine
      (case (:phase game-state)
        :role-selection {:type :select-role :role move :player-id player-id}
        :role-execution {:type :role-action
                         :role (:selected-role game-state)
                         :player-id player-id
                         :args (if (= move :execute) [] [move])}
        nil))))
