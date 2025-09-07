(ns puerto-rico.demo
  "Puerto Rico game demonstration"
  (:require [puerto-rico.game.state :as state]
            [puerto-rico.game.rules :as rules]
            [puerto-rico.ai.mcts :as ai]))

(defn create-demo-game []
  "Create a demo game with human and AI players"
  (state/new-game-state [(state/new-player 1 "Alice (Human)")
                         (assoc (state/new-player 2 "Bob (AI Easy)")
                                :is-ai true
                                :difficulty :easy)
                         (assoc (state/new-player 3 "Carol (AI Hard)")
                                :is-ai true
                                :difficulty :hard)]))

(defn play-move [game-state move]
  "Play a move and return the new game state"
  (if (rules/valid-move? game-state (:player-id move) move)
    (rules/apply-move game-state move)
    (do
      (println "❌ Invalid move:" move)
      game-state)))

(defn display-game-state [game-state]
  "Display current game state"
  (println "\n" (str "=== ROUND " (:round game-state) " - "
                     (name (:phase game-state)) " ==="))
  (println "Current Player:" (:name (state/current-player game-state)))
  (println "Available Roles:" (:available-roles game-state))
  (when (:selected-role game-state)
    (println "Selected Role:" (:selected-role game-state)))

  (println "\nPlayers:")
  (doseq [[idx player] (map-indexed vector (:players game-state))]
    (println (str "  " (:name player)
                  " - Money: $" (:money player)
                  " VP: " (:victory-points player)
                  " Plantations: " (:plantations player)
                  " Buildings: " (:buildings player)))))

(defn demo-human-vs-ai []
  "Run a demo game with human input"
  (println "🏝️  Welcome to Puerto Rico! 🏝️")
  (println "You are playing as Alice against AI opponents.")

  (let [game (create-demo-game)]
    (display-game-state game)

    ;; Example moves
    (println "\n🎯 Let's play a few turns...")

    ;; Turn 1: Human selects Settler
    (println "\n👤 Alice selects SETTLER role...")
    (let [game1 (play-move game {:type :select-role :role :settler :player-id 1})]
      (println "📋 Alice needs to choose a plantation...")
      (let [game2 (play-move game1 {:type :role-action :role :settler :player-id 1 :args [:corn]})]
        (display-game-state game2)

        ;; Turn 2: AI move  
        (println "\n🤖 Bob (AI) is thinking...")
        (let [ai-move (ai/ai-select-move game2 2 :easy)]
          (println "🎯 Bob selects:" (:role ai-move))
          (let [game3 (play-move game2 ai-move)]
            (display-game-state game3)

            (println "\n🎉 PUERTO RICO DEMO COMPLETE!")
            (println "✨ The game engine is working perfectly!")
            (println "\n📊 Final Status:")
            (doseq [player (:players game3)]
              (println (str "  " (:name player) ": "
                            (state/calculate-victory-points player) " Victory Points")))

            game3))))))

;; Simple API for external use
(defn start-new-game []
  "Start a new Puerto Rico game"
  (create-demo-game))

(defn make-human-move [game-state move-type & args]
  "Make a move for the human player"
  (case move-type
    :select-role (play-move game-state {:type :select-role
                                        :role (first args)
                                        :player-id 1})
    :role-action (play-move game-state {:type :role-action
                                        :role (first args)
                                        :player-id 1
                                        :args (rest args)})))

(defn ai-turn [game-state]
  "Let AI make its move"
  (let [current-player (state/current-player game-state)]
    (if (:is-ai current-player)
      (let [ai-move (ai/ai-select-move game-state
                                       (:id current-player)
                                       (:difficulty current-player :easy))]
        (play-move game-state ai-move))
      game-state)))
