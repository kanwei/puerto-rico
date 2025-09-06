(ns puerto-rico.game.engine
  "Game engine that manages game flow and AI players"
  (:require [puerto-rico.game.state :as state]
            [puerto-rico.game.rules :as rules]
            [puerto-rico.ai.mcts-fixed :as ai]))

(defn ai-player? [player]
  (:is-ai player))

(defn process-ai-turn [game-state]
  "Process a turn for an AI player"
  (let [current-player (state/current-player game-state)]
    (if (ai-player? current-player)
      (let [ai-move (ai/ai-select-move game-state
                                       (:id current-player)
                                       (:difficulty current-player :medium))]
        (if ai-move
          (do
            (println (str "AI Player " (:name current-player) " plays: " ai-move))
            (rules/apply-move game-state ai-move))
          (do
            (println (str "AI Player " (:name current-player) " has no valid moves"))
            game-state)))
      game-state)))

(defn run-ai-players-until-human-turn [game-state]
  "Keep processing AI turns until it's a human player's turn or game ends"
  (loop [current-state game-state iterations 0]
    (if (or (> iterations 50) ; Safety valve
            (:game-over current-state)
            (state/check-victory-conditions current-state)
            (not (ai-player? (state/current-player current-state))))
      current-state
      (recur (process-ai-turn current-state) (inc iterations)))))

(defn play-game-step [game-state player-move]
  "Play one step of the game with optional player move"
  (let [updated-state (if player-move
                        (if (rules/valid-move? game-state (:player-id player-move) player-move)
                          (rules/apply-move game-state player-move)
                          game-state)
                        game-state)]
    (run-ai-players-until-human-turn updated-state)))

;; Demo game runner
(defn demo-game []
  "Run a demo game with AI players"
  (let [players [(state/new-player 1 "Human Player")
                 (assoc (state/new-player 2 "AI Easy") :is-ai true :difficulty :easy)
                 (assoc (state/new-player 3 "AI Hard") :is-ai true :difficulty :hard)]
        initial-state (state/new-game-state players)]

    (println "=== PUERTO RICO GAME DEMO ===")
    (println "Players:" (mapv :name players))
    (println)

    (loop [game-state initial-state turns 0]
      (if (or (> turns 20) ; Limit demo length
              (:game-over game-state)
              (state/check-victory-conditions game-state))
        (do
          (println "\n=== GAME ENDED ===")
          (println "Final scores:")
          (doseq [player (:players game-state)]
            (println (str (:name player) ": " (state/calculate-victory-points player) " VP")))
          game-state)
        (do
          (println (str "\n--- Turn " (inc turns) " - Round " (:round game-state) " ---"))
          (println (str "Phase: " (:phase game-state)))
          (println (str "Current player: " (:name (state/current-player game-state))))

          (let [new-state (run-ai-players-until-human-turn game-state)]
            (Thread/sleep 1000) ; Pause for readability
            (recur new-state (inc turns))))))))
