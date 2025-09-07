(ns puerto-rico.core
  (:require [reagent.core :as reagent]
            [reagent.dom :as rdom]
            [puerto-rico.game.state :as state]
            [puerto-rico.game.rules :as rules]))

(defonce game-state (reagent/atom {:game-state nil}))

(defonce game-log (reagent/atom []))

;; State for tracking AI action display  
(defonce ai-action-display (reagent/atom {:show false :player "" :action ""}))

(defn show-ai-action [player-name action-text]
  "Show AI action briefly, then auto-hide"
  (reset! ai-action-display {:show true :player player-name :action action-text})
  (js/setTimeout #(reset! ai-action-display {:show false :player "" :action ""}) 500))

(defn add-log-entry [message & [player-name]]
  (let [timestamp (.toLocaleTimeString (js/Date.))]
    (swap! game-log conj {:timestamp timestamp
                          :player player-name
                          :message message})))

(defn clear-log []
  (reset! game-log []))

(defn create-new-game []
  (clear-log)
  (let [players [{:id 1 :name "Alice (Human)" :is-ai false}
                 {:id 2 :name "Bob (AI)" :is-ai true}
                 {:id 3 :name "Carol (AI)" :is-ai true}]]
    (add-log-entry "New game started with 3 players")
    (state/new-game-state players)))

(defn current-player [game-data]
  (get (:players game-data) (:current-player-idx game-data)))

(defn current-role-executor [game-data]
  (if (= (:phase game-data) :role-execution)
    (get (:players game-data) (:role-execution-current-idx game-data))
    (current-player game-data)))

(defn handle-role-selection [role]
  (swap! game-state update :game-state
         (fn [game-data]
           (when game-data
             (let [current-player-data (current-player game-data)
                   result (rules/select-role game-data (:id current-player-data) role)]
               (add-log-entry (str "Selected " (name role) " role") (:name current-player-data))
               result)))))

(defn handle-plantation-choice [plantation-type]
  (swap! game-state update :game-state
         (fn [game-data]
           (when game-data
             (let [executor (current-role-executor game-data)
                   result (-> (rules/execute-role game-data :settler (:id executor) plantation-type)
                              (rules/end-role-execution))]
               (add-log-entry (str "Took " (name plantation-type) " plantation") (:name executor))
               result)))))

(defn handle-building-choice [building-key]
  (swap! game-state update :game-state
         (fn [game-data]
           (when game-data
             (let [executor (current-role-executor game-data)
                   building-name (-> building-key name (clojure.string/replace "-" " "))
                   result (-> (rules/execute-role game-data :builder (:id executor) building-key)
                              (rules/end-role-execution))]
               (add-log-entry (str "Built " building-name) (:name executor))
               result)))))

(defn handle-good-choice [good-type role]
  (swap! game-state update :game-state
         (fn [game-data]
           (when game-data
             (let [executor (current-role-executor game-data)
                   result (-> (rules/execute-role game-data role (:id executor) good-type)
                              (rules/end-role-execution))]
               (add-log-entry (str (case role
                                     :trader "Sold"
                                     :captain "Shipped"
                                     "Used") " " (name good-type)) (:name executor))
               result)))))

(defn handle-skip-role [role]
  (swap! game-state update :game-state
         (fn [game-data]
           (when game-data
             (let [executor (current-role-executor game-data)
                   result (rules/end-role-execution game-data)]
               (add-log-entry (str "Skipped " (name role)) (:name executor))
               result)))))

(defn execute-ai-turn-async [game-data]
  "Execute AI turn with visual feedback"
  (let [ai-player (if (= (:phase game-data) :role-execution)
                    (current-role-executor game-data)
                    (when (:is-ai (current-player game-data)) (current-player game-data)))]
    (when ai-player
      (case (:phase game-data)
        :role-selection
        (let [available-roles (:available-roles game-data)
              role (rand-nth (vec available-roles))]
          (show-ai-action (:name ai-player) (str "Selected " (name role) " role"))
          (js/setTimeout #(handle-role-selection role) 500))

        :role-execution
        (let [selected-role (:selected-role game-data)]
          (case selected-role
            :settler
            (let [face-up-plantations (:face-up-plantations game-data)
                  quarry-supply (:quarry-supply game-data)
                  executor-player (current-role-executor game-data)
                  role-selector-idx (:role-selector-idx game-data)
                  current-player-idx (:role-execution-current-idx game-data)
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
                  (show-ai-action (:name executor-player) (str "Took " (name choice)))
                  (js/setTimeout #(handle-plantation-choice choice) 500))))

            :builder
            (let [executor-player (current-role-executor game-data)
                  affordable-buildings (filter (fn [[building-key building-info]]
                                                 (and (>= (:money executor-player) (:cost building-info))
                                                      (pos? (get (:building-supply game-data) building-key 0))))
                                               state/buildings)]
              (if (seq affordable-buildings)
                (let [[building-key _] (rand-nth affordable-buildings)
                      building-name (-> building-key name (clojure.string/replace "-" " "))]
                  (show-ai-action (:name executor-player) (str "Built " building-name))
                  (js/setTimeout #(handle-building-choice building-key) 500))
                (do
                  (show-ai-action (:name executor-player) "Skipped building (no money)")
                  (js/setTimeout #(handle-skip-role :builder) 500))))

            (:trader :captain)
            (let [executor-player (current-role-executor game-data)
                  available-goods (filter #(pos? (get-in executor-player [:goods %] 0))
                                          [:corn :indigo :sugar :tobacco :coffee])]
              (if (seq available-goods)
                (let [good (rand-nth available-goods)]
                  (show-ai-action (:name executor-player)
                                  (str (case selected-role
                                         :trader "Sold"
                                         :captain "Shipped") " " (name good)))
                  (js/setTimeout #(handle-good-choice good selected-role) 500))
                (do
                  (show-ai-action (:name executor-player) (str "Skipped " (name selected-role) " (no goods)"))
                  (js/setTimeout #(handle-skip-role selected-role) 500))))

            (:mayor :craftsman)
            (let [executor-player (current-role-executor game-data)]
              (show-ai-action (:name executor-player) (str "Executed " (name selected-role)))
              (js/setTimeout
               #(swap! game-state update :game-state
                       (fn [gd]
                         (when gd
                           (-> (rules/execute-role gd selected-role (:id executor-player))
                               (rules/end-role-execution))))) 500))

            nil))))))

;; Auto-execute AI turns when it's their turn
(defn game-state-watcher []
  (let [game-data (:game-state @game-state)]
    (when game-data
      (let [current-player-data (current-player game-data)
            executor (current-role-executor game-data)]
        (cond
          ;; Role selection phase - auto-execute if AI
          (and (= (:phase game-data) :role-selection) (:is-ai current-player-data))
          (when (not (:show @ai-action-display))
            (execute-ai-turn-async game-data))

          ;; Role execution phase - auto-execute if AI executor  
          (and (= (:phase game-data) :role-execution) (:is-ai executor))
          (when (not (:show @ai-action-display))
            (execute-ai-turn-async game-data)))))
    game-data))

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
  (let [current-player-data (current-role-executor game-data)
        face-up-plantations (:face-up-plantations game-data)
        quarry-supply (:quarry-supply game-data)
        role-selector-idx (:role-selector-idx game-data)
        current-player-idx (:role-execution-current-idx game-data)
        is-role-selector (= current-player-idx role-selector-idx)
        has-construction-hut (some #(and (= (:type %) :construction-hut)
                                         (:colonists %)
                                         (pos? (:colonists %)))
                                   (:buildings current-player-data))
        can-take-quarry (and (pos? quarry-supply) (or is-role-selector has-construction-hut))]
    [:div.role-execution
     [:h2 "🌱 Settler - Choose a Plantation"]
     [:div.choice-grid
      ;; Face-up plantation options
      (for [plantation-type face-up-plantations]
        ^{:key (str "plantation-" plantation-type)}
        [:div.choice-card.plantation-choice {:on-click #(handle-plantation-choice plantation-type)}
         [:h3 (str (name plantation-type) " plantation")]
         [:p "Grow " (name plantation-type)]])
      ;; Quarry option (if available)
      (when can-take-quarry
        ^{:key "quarry"}
        [:div.choice-card.quarry-choice {:on-click #(handle-plantation-choice :quarry)}
         [:h3 "Quarry"]
         [:p "Reduces building costs"]])
      ;; Skip option
      ^{:key "skip"}
      [:div.choice-card.skip {:on-click #(handle-skip-role :settler)}
       [:h3 "Skip"]
       [:p "Don't take any tile"]]]]))

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
        owned-building-types (set (map :type (:buildings current-player-data)))
        affordable-buildings (filter (fn [[building-key building-info]]
                                       (and (>= (:money current-player-data) (:cost building-info))
                                            (pos? (get (:building-supply game-data) building-key 0))
                                            (not (contains? owned-building-types building-key))))
                                     state/buildings)]
    [:div.role-execution
     [:h2 "🏗️ Builder - Choose a Building"]
     (if (seq affordable-buildings)
       [:div
        [:p "Select a building to construct or skip (You have $" (:money current-player-data) "):"]
        [:div.building-grid
         (for [[building-key building-info] affordable-buildings]
           ^{:key building-key}
           [building-card building-key building-info
            #(handle-building-choice building-key)
            (get (:building-supply game-data) building-key 0)])
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
        available-goods (filter #(pos? (get-in current-player-data [:goods %] 0)) [:corn :indigo :sugar :tobacco :coffee])]
    [:div.role-execution
     [:h2 (case role
            :trader "💰 Trader - Sell Goods"
            :captain "🚢 Captain - Ship Goods"
            "Choose Goods")]
     (if (seq available-goods)
       [:div
        [:p "Select a good to " (case role :trader "sell" :captain "ship") ":"]
        [:div.choice-grid
         (for [good available-goods]
           ^{:key good}
           [:div.choice-card.good-choice {:on-click #(handle-good-choice good role)}
            [:h3 (name good)]
            [:p "You have: " (get-in current-player-data [:goods good])]])
         ^{:key "skip"}
         [:div.choice-card.skip {:on-click #(handle-skip-role role)}
          [:h3 "Skip"]
          [:p (case role
                :trader "Don't sell"
                :captain "Don't ship")]]]]
       [:div
        [:p (:name current-player-data) " has no goods to " (case role :trader "sell" :captain "ship") "."]
        [:div.choice-grid
         [:div.choice-card.skip {:on-click #(handle-skip-role role)}
          [:h3 "Skip"]
          [:p (str "No goods to " (case role :trader "sell" :captain "ship"))]]]])]))

(defn role-execution-ui [game-data]
  (let [selected-role (:selected-role game-data)]
    (case selected-role
      :settler [plantation-choice-ui game-data]
      :builder [building-choice-ui game-data]
      :trader [good-choice-ui game-data :trader]
      :captain [good-choice-ui game-data :captain]
      (:mayor :craftsman)
      [:div.role-execution
       [:h2 "🔄 " (name selected-role) " - Executing Automatically"]
       [:p "All players will be processed automatically..."]]
      [:div.role-execution
       [:h2 "Role Execution"]
       [:p "Role " (name selected-role) " is being executed..."]])))

(defn worker-slots-display [occupied total]
  [:div.worker-slots
   (for [i (range total)]
     ^{:key i}
     [:div.worker-circle {:class (when (< i occupied) "occupied")}])])

(defn get-building-capacity [building-type]
  (get-in state/buildings [building-type :worker] 0))

(defn player-board [player current?]
  [:div.player-board {:class (when current? "current-player")}
   [:div.player-header
    [:h4 (:name player)]
    [:div.player-quick-stats
     [:span "💰$" (:money player)]
     [:span "🏆" (:victory-points player)]
     [:span "👥" (+ (:colonists-in-hand player) (:san-juan-colonists player))]]]
   [:div.player-assets
    [:div
     "🏗️ "
     (for [[idx building] (map-indexed vector (:buildings player))]
       ^{:key (str (:id player) "-building-" idx)}
       [:span.building-chip
        (-> (:type building) name (clojure.string/replace "-" " "))
        " " (worker-slots-display (:colonists building) (get-building-capacity (:type building)))])]
    [:div
     "🌱 "
     (for [[idx plantation] (map-indexed vector (:plantations player))]
       ^{:key (str (:id player) "-plantation-" idx)}
       [:span.plantation-chip
        (name (:type plantation))
        " " (if (pos? (:colonists plantation)) "●" "○")])]
    [:div
     "📦 "
     (for [[good amount] (:goods player)]
       (when (pos? amount)
         ^{:key (str (:id player) "-good-" good)}
         [:span.good-chip (str (name good) " " amount)]))]]])

(defn common-area [game-data]
  [:div.common-area
   [:h3 "🌊 Common Area"]
   [:div.supply-section
    [:strong "Ships: "]
    (for [[idx ship] (map-indexed vector (:ships game-data))]
      ^{:key (str "ship-" idx)}
      [:span.supply-chip
       (str "Cap:" (:capacity ship) " "
            (if (:good ship)
              (str (name (:good ship)) ":" (:amount ship))
              "Empty"))])]
   [:div.supply-section
    [:strong "Trading House: "]
    (if (seq (:trading-house game-data))
      (for [[idx trade] (map-indexed vector (:trading-house game-data))]
        ^{:key (str "trade-" idx)}
        [:span.supply-chip (name (:good trade))])
      [:span "Empty"])]
   [:div.supply-section
    [:strong "Plantations: "]
    (for [plantation (:face-up-plantations game-data)]
      ^{:key (str "face-up-" plantation)}
      [:span.supply-chip (name plantation)])
    [:span.supply-chip (str "Quarries:" (:quarry-supply game-data))]]
   [:div.supply-section
    [:strong "Colonists: "]
    [:span.supply-chip (str "Ship:" (:colonist-ship game-data))]
    [:span.supply-chip (str "Supply:" (:colonist-supply game-data))]]])

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
  (let [game-data (game-state-watcher)]
    (if game-data
      (let [current-player-data (current-player game-data)
            ai-display @ai-action-display]
        [:div.game-board-compact
         [:div.header-bar
          [:h2 "🏝️ Puerto Rico"]
          [:div.game-status
           [:span.status-item "📅 Round " (:round game-data)]
           [:span.status-item "⚡ " (name (:phase game-data))]
           [:span.status-item "👑 " (:name (state/current-governor game-data))]
           [:span.status-item "👤 " (:name current-player-data)]]]

         [:div.players-row
          (for [[idx player] (map-indexed vector (:players game-data))]
            ^{:key (:id player)}
            [player-board player (= idx (:current-player-idx game-data))])]

         [:div.main-area
          [:div.action-area
           (if (:show ai-display)
             [:div.ai-action-display
              [:h3 "🤖 " (:player ai-display)]
              [:p (:action ai-display)]]
             (let [executor (current-role-executor game-data)]
               (cond
                 (and (= (:phase game-data) :role-execution) (not (:is-ai executor)))
                 [role-execution-ui game-data]

                 (and (= (:phase game-data) :role-selection) (not (:is-ai current-player-data)))
                 [:div.roles-section-compact
                  [:h3 "🎭 Available Roles"]
                  [:div.roles-grid-compact
                   (for [role state/roles]
                     (let [available? (contains? (:available-roles game-data) role)
                           gold-amount (get-in game-data [:role-gold role] 0)]
                       ^{:key role} [role-card role available? gold-amount handle-role-selection]))]]

                 :else
                 [:div.waiting
                  [:h3 "⏳ Waiting"]
                  [:p "Game is processing..."]])))]

          [:div.sidebar
           [common-area game-data]
           [game-log-ui]]]])
      [:div.no-game
       [:h1 "🏝️ Puerto Rico"]
       [:p "Welcome to the Puerto Rico board game!"]
       [:button {:on-click #(swap! game-state assoc :game-state (create-new-game))}
        "🎮 Start New Game"]])))

(defn main-panel []
  [:div
   [game-board]])

(defn init []
  (rdom/render [main-panel] (.getElementById js/document "app")))

(defn test-nrepl-connection []
  (println "nREPL connection test successful!"))
