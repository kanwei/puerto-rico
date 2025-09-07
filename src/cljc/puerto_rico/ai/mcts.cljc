(ns puerto-rico.ai.mcts
  "Fixed Monte Carlo Tree Search implementation for Puerto Rico AI players"
  (:require [puerto-rico.game.state :as state]
            [puerto-rico.game.rules :as rules]))

(defn evaluate-role-utility
  "Calculate utility score for each role based on player's current situation"
  [game-state player-id role]
  (let [player (state/player-by-id game-state player-id)
        total-goods (apply + (vals (:goods player)))
        money (:money player)
        buildings (:buildings player)
        plantations (:plantations player)
        empty-buildings (count (filter #(zero? (:colonists %)) buildings))
        empty-plantations (count (filter #(zero? (:colonists %)) plantations))
        total-empty-spots (+ empty-buildings empty-plantations)
        colonist-ship (:colonist-ship game-state)
        trading-house (:trading-house game-state)
        available-ships (->> (:ships game-state)
                             (filter #(< (:amount %) (:capacity %)))
                             count)]
    (case role
      :captain
      (if (not (rules/can-ship-goods? game-state player))
        -200 ; Heavily penalize captain if no goods can be shipped
        (+ 20 (* total-goods 10))) ; Higher score with more goods

      :trader
      (if (not (rules/can-trade-any-goods? game-state player))
        -150 ; Heavily penalize trader if no goods can be traded
        (+ 15 (* total-goods 8)))

      :builder
      (if (< money 2)
        -80 ; Avoid builder if very poor
        (+ 10 (* money 3) ; Score based on money available
           (if (>= (count buildings) 10) 15 0))) ; Bonus if close to 12 buildings

      :mayor
      (if (zero? total-empty-spots)
        -60 ; Don't pick mayor if no empty spots
        (+ 12 (* total-empty-spots 5) ; More empty spots = higher score
           (max 0 (- colonist-ship 2)))) ; Bonus if many colonists available

      :craftsman
      (if (not (rules/can-produce-goods? game-state player))
        -120 ; Heavily penalize craftsman if can't produce anything
        (let [production-potential (->> plantations
                                        (filter #(pos? (:colonists %)))
                                        count)]
          (+ 8 (* production-potential 6))))

      :settler
      (if (>= (count plantations) 12)
        -90 ; Avoid if island is full
        (+ 5 (if (< (count plantations) 8) 8 2))) ; Bonus early game

      :prospector
      (if (< money 5)
        8 ; Good when poor
        2) ; Less valuable when rich

      0))) ; Default score

(defn smart-role-selection
  "Filter available roles using heuristics before MCTS"
  [game-state player-id available-roles]
  (let [role-scores (map (fn [role]
                           [role (evaluate-role-utility game-state player-id role)])
                         available-roles)
        ;; Sort by score and take top 60% of roles
        sorted-roles (sort-by second > role-scores)
        num-to-keep (max 1 (int (* 0.6 (count available-roles))))
        good-roles (take num-to-keep sorted-roles)]
    (map first good-roles)))

;; Simplified MCTS with mutable statistics
(defn get-possible-moves [game-state]
  "Generate all possible moves for the current game state, with smart filtering for role selection"
  (case (:phase game-state)
    :role-selection
    (let [available-roles (vec (:available-roles game-state))
          current-player-id (:current-player-idx game-state)]
      ;; Use heuristics to filter roles for AI players
      (if (and current-player-id
               (get-in game-state [:players current-player-id :is-ai]))
        (smart-role-selection game-state current-player-id available-roles)
        available-roles))

    :role-execution
    (let [role (:selected-role game-state)
          current-player (state/current-player game-state)]
      (case role
        :settler
        (let [available-plantations (concat (:face-up-plantations game-state)
                                            (if (pos? (:quarry-supply game-state)) [:quarry] []))]
          (vec (distinct available-plantations)))

        :builder
        (vec (keys (filter (fn [[building-key count]]
                             (and (pos? count)
                                  (let [building-info (get state/buildings building-key)]
                                    (rules/can-build-building? current-player building-key building-info))))
                           (:building-supply game-state))))

        :trader
        (vec (filter #(rules/can-trade-good? game-state current-player %)
                     (keys (:goods-supply game-state))))

        :captain
        (vec (filter #(pos? (get-in current-player [:goods %] 0))
                     (keys (:goods-supply game-state))))

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
