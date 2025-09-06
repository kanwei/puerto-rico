(ns puerto-rico.core
  (:require [reagent.core :as reagent]
            [reagent.dom :as rdom]
            [puerto-rico.game.state :as state]
            [puerto-rico.game.rules :as rules]))

;; Simple game state atom for demo
(defonce game-state (reagent/atom {:game-state nil
                                   :game-id nil
                                   :loading false
                                   :error nil}))

;; Initialize a real game with players
(defn create-new-game []
  (let [players [(state/new-player 1 "Alice (Human)")
                 (state/new-player 2 "Bob (AI)")
                 (state/new-player 3 "Carol (AI)")]]
    (state/new-game-state players)))

;; Helper functions
(defn current-player [game-data]
  (state/current-player game-data))

;; Game logic handlers
(defn handle-role-selection [role]
  (let [current-game (:game-state @game-state)
        current-player-data (current-player current-game)
        player-id (:id current-player-data)]
    (when current-game
      (let [new-game-state (rules/select-role current-game player-id role)]
        (swap! game-state assoc :game-state new-game-state)
        (js/console.log "Role selected:" role "New game state:" new-game-state)))))

;; Components
(defn role-card [role available? on-select]
  [:div.role-card {:class (when-not available? "disabled")
                   :on-click (when available? #(on-select role))}
   [:h3 (name role)]
   [:p (case role
         :settler "Take a plantation"
         :mayor "Get colonists"
         :builder "Build buildings"
         :craftsman "Produce goods"
         :trader "Sell to trading house"
         :captain "Ship goods for VP"
         :prospector "Get money"
         "Choose this role")]])

(defn player-board [player current?]
  [:div.player-board {:class (when current? "current-player")}
   [:h3 (str (:name player) (when current? " ⭐"))]
   [:div.player-stats
    [:p "💰 Money: $" (:money player)]
    [:p "🏆 Victory Points: " (:victory-points player)]
    [:div.goods
     [:h4 "📦 Goods:"]
     (if (seq (:goods player))
       (for [[good amount] (:goods player)]
         ^{:key good} [:span.good (str (name good) ": " amount " ")])
       [:span.empty "None"])]
    [:div.buildings
     [:h4 "🏢 Buildings:"]
     (if (seq (:buildings player))
       (for [building (:buildings player)]
         ^{:key building} [:span.building (str (name building) " ")])
       [:span.empty "None"])]
    [:div.plantations
     [:h4 "🌱 Plantations:"]
     (if (seq (:plantations player))
       (for [plantation (:plantations player)]
         ^{:key plantation} [:span.plantation (str (name plantation) " ")])
       [:span.empty "None"])]]])

(defn game-board []
  (let [game-data (:game-state @game-state)]
    (if game-data
      (let [current-player-data (current-player game-data)]
        [:div.game-board
         [:h1 "🏝️ Puerto Rico"]
         [:div.game-info
          [:p "📅 Round: " (:round game-data)]
          [:p "⚡ Phase: " (name (:phase game-data))]
          [:p "👤 Current Player: " (:name current-player-data)]]

         [:div.roles-section
          [:h2 "🎭 Available Roles"]
          [:div.roles-grid
           (for [role (:available-roles game-data)]
             ^{:key role} [role-card role true handle-role-selection])]]

         [:div.players-section
          [:h2 "👥 Players"]
          [:div.players-grid
           (for [[idx player] (map-indexed vector (:players game-data))]
             ^{:key (:id player)}
             [player-board player (= idx (:current-player-idx game-data))])]]])
      [:div.no-game
       [:h1 "🏝️ Puerto Rico"]
       [:p "Welcome to the Puerto Rico board game!"]
       [:button {:on-click #(swap! game-state assoc :game-state (create-new-game))}
        "🎮 Start New Game"]])))

(defn main-panel []
  [game-board])

(defn init
  "Initialize the application"
  []
  (js/console.log "Initializing Puerto Rico application...")
  (rdom/render [main-panel]
               (.getElementById js/document "app")))

(defn test-nrepl-connection
  "Test function to verify Shadow-CLJS nREPL is working"
  []
  (js/alert "🎉 Shadow-CLJS nREPL Connection Test Successful! 🎉")
  (js/console.log "Shadow-CLJS nREPL is connected and working properly")
  (js/console.log "Current timestamp:" (js/Date.))
  :success)

;; Initialize immediately
(init)
