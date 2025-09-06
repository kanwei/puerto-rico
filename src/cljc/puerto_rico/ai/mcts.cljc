(ns puerto-rico.ai.mcts
  "Monte Carlo Tree Search implementation for Puerto Rico AI players"
  (:require [puerto-rico.game.state :as state]
            [puerto-rico.game.rules :as rules]))

;; Forward declarations
(declare get-possible-moves)

;; MCTS Node structure
(defrecord MCTSNode [game-state
                     move ; Move that led to this state
                     parent ; Parent node
                     children ; Child nodes
                     visits ; Number of times visited
                     wins ; Number of wins from this node
                     untried-moves]) ; Available moves not yet tried

(defn new-mcts-node
  ([game-state] (new-mcts-node game-state nil nil))
  ([game-state move parent]
   (->MCTSNode game-state move parent {} 0 0.0 (get-possible-moves game-state))))

;; Game-specific move generation
(defn get-possible-moves [game-state]
  "Generate all possible moves for the current game state"
  (case (:phase game-state)
    :role-selection
    (mapv (fn [role] {:type :select-role
                      :role role
                      :player-id (:id (state/current-player game-state))})
          (:available-roles game-state))

    :role-execution
    (let [role (:selected-role game-state)
          current-player (state/current-player game-state)]
      (case role
        :settler
        (mapv (fn [plantation] {:type :role-action
                                :role :settler
                                :player-id (:id current-player)
                                :args [plantation]})
              (keys (filter #(pos? (val %)) (:plantation-supply game-state))))

        :builder
        (mapv (fn [[building info]] {:type :role-action
                                     :role :builder
                                     :player-id (:id current-player)
                                     :args [building]})
              (filter (fn [[building info]]
                        (rules/can-build-building? current-player building info))
                      state/buildings))

        :trader
        (mapv (fn [good] {:type :role-action
                          :role :trader
                          :player-id (:id current-player)
                          :args [good]})
              (filter #(rules/can-trade-good? game-state current-player %)
                      state/goods))

        :captain
        (mapv (fn [good] {:type :role-action
                          :role :captain
                          :player-id (:id current-player)
                          :args [good]})
              (filter #(pos? (get-in current-player [:goods %] 0))
                      state/goods))

        ;; For roles with no choices, return single action
        [{:type :role-action
          :role role
          :player-id (:id current-player)
          :args []}]))

    [])) ; No moves available

;; UCB1 selection formula
(defn ucb1-value [node parent-visits c]
  "Calculate UCB1 value for node selection"
  (if (zero? (:visits node))
    Double/POSITIVE_INFINITY
    (+ (/ (:wins node) (:visits node))
       (* c (Math/sqrt (/ (Math/log parent-visits) (:visits node)))))))

;; MCTS algorithm phases

(defn select-node [node]
  "Selection phase: traverse tree using UCB1 until we reach a leaf"
  (if (or (empty? (:children node))
          (not-empty (:untried-moves node)))
    node
    (let [c 1.414 ; UCB1 exploration parameter
          best-child (apply max-key
                            #(ucb1-value % (:visits node) c)
                            (vals (:children node)))]
      (recur best-child))))

(defn expand-node [node]
  "Expansion phase: add a new child node for an untried move"
  (if (empty? (:untried-moves node))
    node
    (let [move (first (:untried-moves node))
          new-game-state (rules/apply-move (:game-state node) move)
          child-node (new-mcts-node new-game-state move node)]
      (-> node
          (assoc-in [:children move] child-node)
          (update :untried-moves rest)))))

(defn random-playout [game-state max-depth]
  "Simulation phase: random playout to estimate value"
  (loop [state game-state depth 0]
    (if (or (>= depth max-depth)
            (:game-over state)
            (state/check-victory-conditions state))
      state
      (let [possible-moves (get-possible-moves state)]
        (if (empty? possible-moves)
          state
          (let [random-move (rand-nth possible-moves)
                new-state (rules/apply-move state random-move)]
            (recur new-state (inc depth))))))))

(defn evaluate-terminal-state [game-state player-id]
  "Evaluate the final game state from perspective of player-id"
  (if (state/check-victory-conditions game-state)
    (let [final-scores (map state/calculate-victory-points (:players game-state))
          player-idx (->> (:players game-state)
                          (map-indexed vector)
                          (filter #(= (:id (second %)) player-id))
                          first
                          first)
          player-score (nth final-scores player-idx)
          max-score (apply max final-scores)]
      (if (= player-score max-score) 1.0 0.0))
    0.5)) ; Neutral evaluation for non-terminal states

(defn backpropagate [node player-id result]
  "Backpropagation phase: update node statistics"
  (when node
    (let [updated-node (-> node
                           (update :visits inc)
                           (update :wins + result))]
      (recur (:parent updated-node) player-id result))))

;; Main MCTS algorithm
(defn mcts-search [game-state player-id iterations]
  "Run MCTS search for specified number of iterations"
  (let [root (new-mcts-node game-state)]
    (dotimes [_ iterations]
      (let [selected (select-node root)
            expanded (expand-node selected)
            simulation-result (-> (:game-state expanded)
                                  (random-playout 50)
                                  (evaluate-terminal-state player-id))]
        (backpropagate expanded player-id simulation-result)))
    root))

(defn best-move [mcts-root]
  "Select the best move from MCTS results (most visited child)"
  (when (not-empty (:children mcts-root))
    (->> (:children mcts-root)
         (sort-by #(:visits (val %)))
         last
         key)))

;; AI player interface
(defn ai-select-move [game-state player-id difficulty]
  "AI player selects a move using MCTS"
  (let [iterations (case difficulty
                     :easy 100
                     :medium 500
                     :hard 1000
                     500)
        mcts-root (mcts-search game-state player-id iterations)
        selected-move (best-move mcts-root)]
    (println (str "AI Player " player-id " selected move: " selected-move
                  " (visited " (:visits (get (:children mcts-root) selected-move)) " times)"))
    selected-move))

;; AI thinking and decision making utilities
(defn evaluate-position [game-state player-id]
  "Quick evaluation of current position without full MCTS search"
  (let [player (->> (:players game-state)
                    (filter #(= (:id %) player-id))
                    first)
        current-score (state/calculate-victory-points player)
        opponent-scores (map state/calculate-victory-points
                             (filter #(not= (:id %) player-id) (:players game-state)))]
    (- current-score (/ (reduce + opponent-scores) (count opponent-scores)))))

(defn ai-think [game-state player-id]
  "AI thinks about current position and available moves"
  (let [possible-moves (get-possible-moves game-state)
        position-eval (evaluate-position game-state player-id)]
    {:possible-moves (count possible-moves)
     :position-evaluation position-eval
     :phase (:phase game-state)
     :round (:round game-state)}))
