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

;; Game log state
(defonce game-log (reagent/atom []))

(defn add-log-entry [message & [player-name]]
  (let [timestamp (.toLocaleTimeString (js/Date.))
        entry {:timestamp timestamp
               :message message
               :player player-name}]
    (swap! game-log conj entry)
    ;; Keep only last 20 entries to prevent memory issues
    (when (> (count @game-log) 20)
      (swap! game-log #(vec (take-last 20 %))))))

(defn clear-log []
  (reset! game-log []))

;; Initialize a real game with players
(defn create-new-game []
  (clear-log)
  (add-log-entry "🎮 New game started with 3 players")
  (let [players [(state/new-player 1 "Alice (Human)")
                 (assoc (state/new-player 2 "Bob (AI)") :is-ai true :difficulty :medium)
                 (assoc (state/new-player 3 "Carol (AI)") :is-ai true :difficulty :hard)]]
    (state/new-game-state players)))

;; Helper functions
(defn current-player [game-data]
  (state/current-player game-data))

(defn current-role-executor [game-data]
  "Get the player who is currently executing the role"
  (when-let [executor-idx (:role-execution-current-idx game-data)]
    (nth (:players game-data) executor-idx)))

;; Forward declaration for function used before definition
(declare handle-automatic-role-execution)

;; Game logic handlers

;; Game logic handlers
(defn handle-role-selection [role]
  (let [current-game (:game-state @game-state)
        current-player-data (current-player current-game)
        player-id (:id current-player-data)]
    (when current-game
      (let [new-game-state (rules/select-role current-game player-id role)]
        (add-log-entry (str "🎭 Selected " (name role) " role") (:name current-player-data))
        (when (= role :prospector)
          (add-log-entry (str "💰 Gained 1 doubloon from Prospector") (:name current-player-data)))
        (swap! game-state assoc :game-state new-game-state)

        ;; Auto-execute roles that don't require player choices
        (when (contains? #{:mayor :craftsman} role)
          (handle-automatic-role-execution new-game-state))

        (js/console.log "Role selected:" role "New game state:" new-game-state)))))

;; Role execution handlers
(defn handle-plantation-choice [plantation-type]
  (let [current-game (:game-state @game-state)]
    (when current-game
      (let [executor-player (current-role-executor current-game)
            player-id (:id executor-player)]
        (when (and executor-player player-id)
          (let [new-game-state (-> current-game
                                   (rules/execute-role :settler player-id plantation-type)
                                   (rules/advance-role-execution))]
            (add-log-entry (str "🌱 Took " (name plantation-type) " plantation") (:name executor-player))
            (swap! game-state assoc :game-state new-game-state)
            (js/console.log "Plantation chosen:" plantation-type "for player:" (:name executor-player) "New game state:" new-game-state)))))))

(defn handle-building-choice [building-key]
  (let [current-game (:game-state @game-state)]
    (when current-game
      (let [executor-player (current-role-executor current-game)
            player-id (:id executor-player)]
        (when (and executor-player player-id)
          (let [building-info (get state/buildings building-key)
                new-game-state (-> current-game
                                   (rules/execute-role :builder player-id building-key)
                                   (rules/advance-role-execution))]
            (add-log-entry (str "🏗️ Built " (name building-key) " for $" (:cost building-info)) (:name executor-player))
            (swap! game-state assoc :game-state new-game-state)
            (js/console.log "Building chosen:" building-key "for player:" (:name executor-player) "New game state:" new-game-state)))))))

(defn handle-good-choice [good-type role]
  (let [current-game (:game-state @game-state)]
    (when current-game
      (let [executor-player (current-role-executor current-game)
            player-id (:id executor-player)
            executor-idx (:role-execution-current-idx current-game)
            ;; Capture money before trade (for trader role)
            money-before (when (= role :trader) (:money executor-player))]
        (when (and executor-player player-id)
          (let [new-game-state (-> current-game
                                   (rules/execute-role role player-id good-type)
                                   (rules/advance-role-execution))
                ;; Calculate money gained for trader role (use executor index before advancement)
                executor-after (when (and (= role :trader) executor-idx)
                                 (nth (:players new-game-state) executor-idx))
                money-after (when executor-after (:money executor-after))
                money-gained (when (and money-before money-after) (- money-after money-before))
                action-text (case role
                              :trader (str "💰 Sold " (name good-type)
                                           (when money-gained
                                             (str " for " money-gained " doubloons")))
                              :captain (str "🚢 Shipped " (name good-type))
                              (str "Used " (name good-type)))]
            (add-log-entry action-text (:name executor-player))
            (swap! game-state assoc :game-state new-game-state)
            (js/console.log "Good chosen:" good-type "for role:" role "for player:" (:name executor-player) "New game state:" new-game-state)))))))

(defn handle-skip-role [role]
  (let [current-game (:game-state @game-state)]
    (when current-game
      (let [executor-player (current-role-executor current-game)
            player-id (:id executor-player)]
        (when (and executor-player player-id)
          (let [new-game-state (rules/advance-role-execution current-game)
                action-text (case role
                              :trader "💼 Skipped trading (no goods)"
                              :captain "⛵ Skipped shipping (no goods)"
                              :builder "🔨 Skipped building (can't afford)"
                              :settler "🚫 Skipped settler (no plantations)"
                              "⏭️ Skipped")]
            (add-log-entry action-text (:name executor-player))
            (swap! game-state assoc :game-state new-game-state)
            (js/console.log "Player skipped role:" role "for player:" (:name executor-player))))))))

;; AI turn handling
(defn handle-ai-turn [game-data]
  (let [ai-player (if (= (:phase game-data) :role-execution)
                    (current-role-executor game-data)
                    (when (:is-ai (current-player game-data)) (current-player game-data)))]
    (when ai-player
      (let [player-id (:id ai-player)
            difficulty (:difficulty ai-player :medium)]
        ;; Execute AI decision immediately without delay
        (let [current-game (:game-state @game-state)]
          (when (and current-game
                     (= (:current-player-idx current-game)
                        (:current-player-idx game-data)))
            ;; Import AI function dynamically
            (case (:phase current-game)
              :role-selection
              (let [available-roles (:available-roles current-game)
                    ;; Simple AI: pick a random available role
                    role (rand-nth (vec available-roles))]
                (handle-role-selection role))

              :role-execution
              (let [selected-role (:selected-role current-game)]
                (case selected-role
                  :settler
                  (let [face-up-plantations (:face-up-plantations current-game)
                        quarry-supply (:quarry-supply current-game)
                        executor-player (current-role-executor current-game)
                        role-selector-idx (:role-selector-idx current-game)
                        current-player-idx (:role-execution-current-idx current-game)
                        is-role-selector (= current-player-idx role-selector-idx)
                        has-construction-hut (some #(and (= (:type %) :construction-hut)
                                                         (:colonists %)
                                                         (pos? (:colonists %)))
                                                   (:buildings executor-player))
                        can-take-quarry (and (pos? quarry-supply) (or is-role-selector has-construction-hut))
                        available-choices (concat face-up-plantations
                                                  (when can-take-quarry [:quarry]))]
                    (when (seq available-choices)
                      (let [choice (rand-nth available-choices)]
                        (handle-plantation-choice choice))))

                  :builder
                  (let [executor-player (current-role-executor current-game)
                        affordable-buildings (filter (fn [[building-key building-info]]
                                                       (and (>= (:money executor-player) (:cost building-info))
                                                            (pos? (get (:building-supply current-game) building-key 0))))
                                                     state/buildings)]
                    (if (seq affordable-buildings)
                      (let [[building-key _] (rand-nth affordable-buildings)]
                        (handle-building-choice building-key))
                      (handle-skip-role :builder)))

                  (:trader :captain)
                  (let [executor-player (current-role-executor current-game)
                        available-goods (filter #(pos? (get-in executor-player [:goods %] 0))
                                                [:corn :indigo :sugar :tobacco :coffee])]
                    (if (seq available-goods)
                      (let [good (rand-nth available-goods)]
                        (handle-good-choice good selected-role))
                      (handle-skip-role selected-role)))

                  ;; For automatic roles, trigger automatic execution  
                  (:mayor :craftsman)
                  (handle-automatic-role-execution current-game)

                  ;; For other roles, do nothing
                  nil)))))))))

(defn handle-automatic-role-execution [game-data]
  "Handle roles that don't require player choices (Mayor, Craftsman)"
  (let [selected-role (:selected-role game-data)
        executor-player (current-role-executor game-data)]
    (when (and executor-player
               (contains? #{:mayor :craftsman} selected-role))
      ;; Auto-execute for this player and advance
      (let [current-game (:game-state @game-state)
            player-id (:id executor-player)
;; Execute the global role for entire table
            game-after-execution (rules/execute-role current-game selected-role player-id)
            ;; End the entire role execution phase (don't advance to next player)
            new-game-state (rules/end-role-execution game-after-execution)]
        (let [action-text (case selected-role
                            :mayor "👥 Colonists distributed to all players"
                            :craftsman "⚙️ All players produced goods"
                            "Executed for all players")]
          (add-log-entry action-text (:name executor-player)))
        (swap! game-state assoc :game-state new-game-state)
        (js/console.log "Auto-executed global role" selected-role "for entire table")))))

;; Game state watcher for AI turns
(defn game-state-watcher []
  (let [game-data (:game-state @game-state)]
    (when game-data
      (handle-ai-turn game-data))
    game-data))

;; Components
(defn role-card [role available? gold-amount on-select]
  [:div.role-card {:class (when-not available? "disabled")
                   :on-click (when available? #(on-select role))}
   [:h3 (name role)]
   (when (and gold-amount (> gold-amount 0))
     [:div.gold-coins
      [:span.gold-icon "💰"]
      [:span.gold-amount gold-amount]])
   [:p (case role
         :settler "Take a plantation"
         :mayor "Get colonists"
         :builder "Build buildings"
         :craftsman "Produce goods"
         :trader "Sell to trading house"
         :captain "Ship goods for VP"
         :prospector "Get money"
         "Choose this role")]])

(defn plantation-choice-ui [game-data]
  (let [face-up-plantations (:face-up-plantations game-data)
        quarry-supply (:quarry-supply game-data)
        current-player-data (current-role-executor game-data)]
    [:div.role-execution
     [:h2 "🌱 Settler - Choose a Plantation"]
     [:div
      [:p "Select a plantation tile to place on your island:"]
      [:div.choice-grid
       ;; Face-up plantation choices
       (for [[idx plantation-type] (map-indexed vector face-up-plantations)]
         ^{:key (str "face-up-" idx)}
         [:div.choice-card {:on-click #(handle-plantation-choice plantation-type)}
          [:h3 (name plantation-type)]])

;; Quarry choice (only if player qualifies)
       (let [role-selector-idx (:role-selector-idx game-data)
             current-player-idx (:role-execution-current-idx game-data)
             is-role-selector (= current-player-idx role-selector-idx)
             has-construction-hut (some #(and (= (:type %) :construction-hut)
                                              (:colonists %)
                                              (pos? (:colonists %)))
                                        (:buildings current-player-data))
             can-take-quarry (and (pos? quarry-supply) (or is-role-selector has-construction-hut))]
         (when can-take-quarry
           ^{:key "quarry"}
           [:div.choice-card {:on-click #(handle-plantation-choice :quarry)}
            [:h3 "quarry"]
            [:p "Available: " quarry-supply]
            (when-not is-role-selector
              [:p.privilege-text "Construction Hut allows quarry"])]))

;; Skip option (always available)
       ^{:key "skip"}
       [:div.choice-card.skip {:on-click #(handle-skip-role :settler)}
        [:h3 "Skip"]
        [:p "Don't take any tile"]]]]]))

(defn building-card
  "Renders a building card with cost in first circle, worker slots, and VP in corner"
  [building-key building-info on-click available-count]
  (let [cost (:cost building-info)
        vp (:vp building-info)
        worker-slots (:worker building-info)
        building-name (-> building-key name (clojure.string/replace "-" " "))
        description (or (:description building-info) "")
        ;; Extract key benefit from description for display
        benefit (cond
                  (clojure.string/includes? description "colonist")
                  "+ 1 colonist for settling (settler phase)"
                  (clojure.string/includes? description "doubloon")
                  (let [amount (if (clojure.string/includes? description "extra 2") "2" "1")]
                    (str "+ " amount " doubloon for trading"))
                  (clojure.string/includes? description "victory point")
                  "+ extra victory points"
                  (and (seq description) (> (count description) 0))
                  (-> description
                      (clojure.string/split #"\.")
                      first
                      (subs 0 (min 50 (count description))))
                  :else
                  "Special building ability")]
    [:div.building-card {:on-click on-click}
     ;; VP in top right corner
     [:div.building-vp vp]

     ;; Building name
     [:div.building-name building-name]

     ;; Worker slots - all empty circles
     [:div.worker-slots
      (for [i (range worker-slots)]
        ^{:key i}
        [:div.worker-circle])]

     ;; Key benefit
     [:div.building-benefit benefit]

     ;; Available count
     [:div.building-available (str "Available: " available-count)]

     ;; Cost in bottom right
     [:div.building-cost (str "$" cost)]]))

(defn building-choice-ui [game-data]
  (let [current-player-data (current-role-executor game-data)
;; Get building types the player already owns
        owned-building-types (set (map :type (:buildings current-player-data)))
        affordable-buildings (filter (fn [[building-key building-info]]
                                       (and (>= (:money current-player-data) (:cost building-info))
                                            (pos? (get (:building-supply game-data) building-key 0))
                                            ;; Can't build same type twice
                                            (not (contains? owned-building-types building-key))))
                                     state/buildings)]
    [:div.role-execution
     [:h2 "🏗️ Builder - Choose a Building"]
     (if (seq affordable-buildings)
       [:div
        [:p "Select a building to construct or skip (You have $" (:money current-player-data) "):"]
        [:div.building-grid
         ;; Building options
         (for [[building-key building-info] affordable-buildings]
           ^{:key building-key}
           [building-card building-key building-info
            #(handle-building-choice building-key)
            (get (:building-supply game-data) building-key 0)])
         ;; Skip option
         ^{:key "skip"}
         [:div.choice-card.skip {:on-click #(handle-skip-role :builder)}
          [:h3 "Skip"]
          [:p "Don't build anything"]]]]
       [:div
        [:p (:name current-player-data) " cannot afford any buildings (You have $" (:money current-player-data) ")."]
        [:div.choice-grid
         [:div.choice-card.skip {:on-click #(handle-skip-role :builder)}
          [:h3 "Skip"]
          [:p "Can't afford buildings"]]]])]))

(defn good-choice-ui [game-data role]
  (let [current-player-data (current-role-executor game-data)
        available-goods (filter #(pos? (get-in current-player-data [:goods %] 0)) [:corn :indigo :sugar :tobacco :coffee])
        title (case role
                :trader "Trader - Choose Good to Sell"
                :captain "Captain - Choose Good to Ship"
                "Choose a Good")]
    [:div.role-execution
     [:h2 "📦 " title]
     (if (seq available-goods)
       [:div
        [:p "Select a good from your inventory:"]
        [:div.choice-grid
         (for [good-type available-goods]
           ^{:key good-type}
           [:div.choice-card {:on-click #(handle-good-choice good-type role)}
            [:h3 (name good-type)]
            [:p "You have: " (get-in current-player-data [:goods good-type] 0)]])]]
       [:div
        [:p (:name current-player-data) " has no goods to "
         (case role :trader "sell" :captain "ship" "use") "."]
        [:div.choice-grid
         [:div.choice-card.skip {:on-click #(handle-skip-role role)}
          [:h3 "Skip"]
          [:p "No goods available"]]]])]))

(defn role-execution-ui [game-data]
  (let [selected-role (:selected-role game-data)
        executor-player (current-role-executor game-data)]
;; DO NOT auto-execute here - it causes multiple executions on every render
    ;; Auto-execution should only happen once in handle-ai-turn or handle-role-selection

    (case selected-role
      :settler [plantation-choice-ui game-data]
      :builder [building-choice-ui game-data]
      :trader [good-choice-ui game-data :trader]
      :captain [good-choice-ui game-data :captain]
      ;; For automatic roles, show status
      (:mayor :craftsman)
      [:div.role-execution
       [:h2 "🔄 " (name selected-role) " - Executing Automatically"]
       [:p (:name executor-player) " is executing " (name selected-role) "..."]]
      ;; Default case
      [:div.role-execution
       [:h2 "Role Execution"]
       [:p "Role " (name selected-role) " is being executed..."]])))

(defn worker-slots-display [occupied total]
  "Display worker slots as filled/empty circles"
  (let [filled (repeat occupied "●")
        empty (repeat (- total occupied) "○")]
    (str (apply str filled) (apply str empty))))

(defn get-building-capacity [building-type]
  "Get the worker capacity for a building type"
  (get-in state/buildings [building-type :worker] 1))

(defn player-board [player current?]
  [:div.player-board {:class (when current? "current-player")}
   [:div.player-header
    [:h4 (str (:name player) (when current? " ⭐"))]
    [:div.player-quick-stats
     [:span.money "💰$" (:money player)]
     [:span.vp "🏆" (:victory-points player)]
     [:span.san-juan "🏘️" (get player :san-juan-colonists 0)]]]

   [:div.player-assets
;; Buildings (more compact)
    (when (seq (:buildings player))
      [:div.buildings-compact
       [:strong "🏢 "]
       (for [[idx building] (map-indexed vector (:buildings player))]
         (let [building-type (if (map? building) (:type building) building)
               occupied (if (map? building) (:colonists building 0) 0)
               total-capacity (get-building-capacity building-type)]
           ^{:key idx} [:span.building-chip
                        (str (name building-type) " " (worker-slots-display occupied total-capacity))]))])

;; Plantations (more compact)
    (when (seq (:plantations player))
      [:div.plantations-compact
       [:strong "🌱 "]
       (for [[idx plantation] (map-indexed vector (:plantations player))]
         (let [plantation-type (if (map? plantation) (:type plantation) plantation)
               occupied (if (map? plantation) (:colonists plantation 0) 0)
               total-capacity 1] ; plantations always have 1 worker slot
           ^{:key idx} [:span.plantation-chip
                        (str (name plantation-type) " " (worker-slots-display occupied total-capacity))]))])

    ;; Goods (inline)
    (let [goods-with-amounts (filter #(> (second %) 0) (:goods player))]
      (when (seq goods-with-amounts)
        [:div.goods-compact
         [:strong "📦 "]
         (for [[good amount] goods-with-amounts]
           ^{:key good} [:span.good-chip (str (name good) ":" amount)])]))]])

(defn common-area [game-data]
  [:div.common-area
   [:h3 "🏢 Game State"]

   ;; Top row - key supplies
   [:div.supply-row
    [:div.supply-item
     [:strong "🏆 VP: "] (:victory-point-supply game-data)]
    [:div.supply-item
     [:strong "👥 Colonists: "] (:colonist-supply game-data)]
    [:div.supply-item
     [:strong "🚢 Ship: "] (get game-data :colonist-ship 0)]]

;; Face-up plantations (horizontal)
   [:div.supply-section
    [:strong "🌱 Available Plantations: "]
    (let [face-up (:face-up-plantations game-data)]
      (for [[idx plantation-type] (map-indexed vector face-up)]
        ^{:key idx} [:span.supply-chip (name plantation-type)]))
    [:span.supply-chip (str "quarries:" (get game-data :quarry-supply 0))]]

   ;; Goods supply (horizontal) 
   [:div.supply-section
    [:strong "📦 Goods: "]
    (for [[good count] (sort-by first (:goods-supply game-data))]
      ^{:key good} [:span.supply-chip (str (name good) ":" count)])]

;; Trading house (if not empty)
   (when (seq (:trading-house game-data))
     [:div.supply-section
      [:strong "🏪 Trading House: "]
      (for [item (:trading-house game-data)]
        (let [good-name (if (map? item) (:good item) item)]
          ^{:key (str "th-" good-name)} [:span.supply-chip (name good-name)]))])

;; Ships (horizontal)
   [:div.supply-section
    [:strong "⛵ Ships: "]
    (for [[idx ship] (map-indexed vector (:ships game-data))]
      ^{:key idx} [:span.supply-chip
                   (if (:good ship)
                     (str (name (:good ship)) " " (:amount ship 0) "/" (:capacity ship 0))
                     (str "Empty/" (:capacity ship 0)))])]])

(defn game-log-ui []
  [:div.game-log
   [:h3 "📜 Game Log"]
   [:div.log-entries
    (if (seq @game-log)
      (for [[idx entry] (map-indexed vector (reverse @game-log))]
        ^{:key idx}
        [:div.log-entry
         [:span.timestamp (:timestamp entry)]
         (when (:player entry)
           [:span.player (:player entry) ":"])
         [:span.message (:message entry)]])
      [:div.log-empty "No events yet..."])]])

(defn game-board []
  (let [game-data (:game-state @game-state)]
    (if game-data
      (let [current-player-data (current-player game-data)]
        [:div.game-board-compact
         ;; Compact header bar
         [:div.header-bar
          [:h2 "🏝️ Puerto Rico"]
          [:div.game-status
           [:span.status-item "📅 Round " (:round game-data)]
           [:span.status-item "⚡ " (name (:phase game-data))]
           [:span.status-item "👑 " (:name (state/current-governor game-data))]
           [:span.status-item "👤 " (:name current-player-data)]]]

         ;; Players in horizontal row (compact)
         [:div.players-row
          (for [[idx player] (map-indexed vector (:players game-data))]
            ^{:key (:id player)}
            [player-board player (= idx (:current-player-idx game-data))])]

         ;; Main action area and common area side by side
         [:div.main-area
          [:div.action-area
           (let [executor (current-role-executor game-data)
                 ;; Determine if we need to show AI button
                 ai-player (cond
                             ;; Role execution phase - check if executor is AI
                             (= (:phase game-data) :role-execution)
                             (when (:is-ai executor) executor)
                             ;; Role selection phase - check if current player is AI
                             (= (:phase game-data) :role-selection)
                             (when (:is-ai current-player-data) current-player-data)
                             ;; Default
                             :else nil)]
             (cond
               ;; Show AI button when it's an AI player's turn
               ai-player
               [:div.ai-turn-section
                [:h3 "🤖 AI Player Turn"]
                [:p (:name ai-player) " is "
                 (case (:phase game-data)
                   :role-selection "selecting a role"
                   :role-execution (str "executing " (name (:selected-role game-data)))
                   "taking their turn") "..."]
                [:button.ai-turn-button {:on-click #(handle-ai-turn game-data)}
                 "▶️ Execute " (:name ai-player) "'s Turn"]]

               ;; Role execution phase - human turn
               (= (:phase game-data) :role-execution)
               [role-execution-ui game-data]

               ;; Role selection phase - human turn
               :else
               [:div.roles-section-compact
                [:h3 "🎭 Available Roles"]
                [:div.roles-grid-compact
                 (for [role state/roles]
                   (let [available? (contains? (:available-roles game-data) role)
                         gold-amount (get-in game-data [:role-gold role] 0)]
                     ^{:key role} [role-card role available? gold-amount handle-role-selection]))]]))]

          [:div.sidebar
           [common-area game-data]
           [:div.log-compact
            [:h4 "📜 Recent"]
            [:div.log-entries-mini
             (if (seq @game-log)
               (for [[idx entry] (map-indexed vector (take 3 (reverse @game-log)))]
                 ^{:key idx}
                 [:div.log-entry-mini
                  (when (:player entry)
                    [:span.player-mini (:player entry) ": "])
                  [:span.message-mini (:message entry)]])
               [:div.log-empty-mini "No events yet..."])]]]]])
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
