(ns puerto-rico.core
  (:require [reagent.core :as reagent]
            [reagent.dom :as rdom]
            [puerto-rico.game.state :as state]
            [puerto-rico.game.rules :as rules]
            [puerto-rico.ai.heuristic :as ai]))

;; Forward declaration for function used before definition
(declare handle-automatic-role-execution game-state-watcher)

;; Simple game state atom for demo
(defonce game-state (reagent/atom {:game-state nil}))

(defonce game-log (reagent/atom []))

;; State for viewing historical game states
(defonce historical-view (reagent/atom {:active false :state-index nil}))

;; State for tracking AI action display  
(defonce ai-action-display (reagent/atom {:show false :player "" :action ""}))

(defonce game-log (reagent/atom []))

;; State for tracking AI action display
(defonce ai-action-display (reagent/atom {:show false :player "" :action ""}))

(defonce game-log (reagent/atom []))

;; State for tracking AI action display
(defonce ai-action-display (reagent/atom {:show false :player "" :action ""}))

;; Game log state
(defonce game-log (reagent/atom []))

(defn add-log-entry [message & [player-name]]
  (let [current-game (:game-state @game-state)
        round-num (:round current-game 1)
        current-player-idx (:current-player-idx current-game)
        players-count (count (:players current-game))
        ;; Calculate turn number within round (1-based)
        turn-num (if current-player-idx (inc current-player-idx) 1)
        turn-label (str round-num "." turn-num)
        entry {:turn turn-label
               :message message
               :player player-name
               :game-state-snapshot current-game ; Store the game state before this move
               :timestamp (.getTime (js/Date.))}]
    (swap! game-log conj entry)
    ;; No limit - keep all entries to see start of game
    ))

(defn view-historical-state [log-index]
  "Switch to viewing a historical game state"
  (swap! historical-view assoc :active true :state-index log-index))

(defn return-to-current-state []
  "Return to viewing the current game state"
  (swap! historical-view assoc :active false :state-index nil))

(defn get-display-game-state []
  "Get the game state that should be displayed (current or historical)"
  (if (:active @historical-view)
    ;; Return historical state
    (let [log-index (:state-index @historical-view)
          log-entries @game-log]
      (if (and log-index (< log-index (count log-entries)))
        {:game-state (:game-state-snapshot (nth log-entries log-index))
         :historical true
         :log-index log-index}
        @game-state))
    ;; Return current state with AI automation
    (let [game-data (game-state-watcher)]
      {:game-state game-data
       :historical false})))

(defn clear-log []
  (reset! game-log []))

;; Initialize a real game with players
(defn create-new-game []
  (clear-log)
  (let [players [(state/new-player 1 "Alice (Human)")
                 (assoc (state/new-player 2 "Bob (AI)") :is-ai true)
                 (assoc (state/new-player 3 "Carol (AI)") :is-ai true)]]
    (add-log-entry "New game started with 3 players")
    (state/new-game-state players)))

(defn create-ai-only-game []
  (clear-log)
  (let [players [(assoc (state/new-player 1 "Alice (AI)") :is-ai true)
                 (assoc (state/new-player 2 "Bob (AI)") :is-ai true)
                 (assoc (state/new-player 3 "Carol (AI)") :is-ai true)]]
    (add-log-entry "AI-only game started - watch the AIs compete!")
    (state/new-game-state players)))

;; Helper functions
(defn current-player [game-data]
  (state/current-player game-data))

(defn current-role-executor [game-data]
  "Get the player who is currently executing the role"
  (when-let [executor-idx (:role-execution-current-idx game-data)]
    (nth (:players game-data) executor-idx)))

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
;; NOTE: This function is not currently used - execute-ai-turn-async is used instead
;; Kept for reference in case synchronous AI turns are needed in the future
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
            ;; Use heuristic AI for all decisions
            (case (:phase current-game)
              :role-selection
              (let [available-roles (:available-roles current-game)
                    ;; Use heuristic AI to select best role
                    role-scores (map (fn [role]
                                       [role (ai/evaluate-role-utility current-game player-id role)])
                                     available-roles)
                    sorted-scores (sort-by second > role-scores)
                    best-role (first (first sorted-scores))]
                ;; Log all scores for debugging
                (js/console.log "AI Role Selection Debug for" (:name ai-player))
                (js/console.log "Available roles and scores:")
                (doseq [[role score] sorted-scores]
                  (js/console.log (str "  " (name role) ": " score)))
                (when best-role
                  (js/console.log "Selected:" (name best-role) "with score:" (second (first sorted-scores)))
                  (add-log-entry (str "AI chose " (name best-role) " role (score: "
                                      (second (first sorted-scores)) ")") (:name ai-player))
                  (handle-role-selection best-role)))

              :role-execution
              (let [selected-role (:selected-role current-game)
                    executor-player (current-role-executor current-game)]
                (case selected-role
                  :settler
                  (let [face-up-plantations (:face-up-plantations current-game)
                        quarry-supply (:quarry-supply current-game)
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
                      (let [plantation-scores (map (fn [p]
                                                     [p (ai/evaluate-plantation-value current-game executor-player p)])
                                                   available-choices)
                            sorted-scores (sort-by second > plantation-scores)
                            best-choice (first (first sorted-scores))]
                        (js/console.log "AI Plantation Selection Debug for" (:name executor-player))
                        (js/console.log "Available plantations and scores:")
                        (doseq [[p score] sorted-scores]
                          (js/console.log (str "  " (name p) ": " score)))
                        (when best-choice
                          (js/console.log "Selected:" (name best-choice) "with score:" (second (first sorted-scores)))
                          (add-log-entry (str "AI chose " (name best-choice) " (score: "
                                              (second (first sorted-scores)) ")") (:name executor-player))
                          (handle-plantation-choice best-choice)))))

                  :builder
                  (let [affordable-buildings (keys (filter (fn [[building-key building-info]]
                                                             (let [occupied-quarries (count (filter #(and (= (:type %) :quarry)
                                                                                                          (pos? (:colonists %)))
                                                                                                    (:plantations executor-player)))
                                                                   base-cost (:cost building-info)
                                                                   actual-cost (max 1 (- base-cost (min occupied-quarries (dec base-cost))))]
                                                               (and (>= (:money executor-player) actual-cost)
                                                                    (pos? (get (:building-supply current-game) building-key 0))
                                                                    (not (some #(= (:type %) building-key) (:buildings executor-player))))))
                                                           state/buildings))]
                    (if (seq affordable-buildings)
                      (let [building-key (ai/select-best-building current-game executor-player affordable-buildings)]
                        (if building-key
                          (handle-building-choice building-key)
                          (handle-skip-role :builder)))
                      (handle-skip-role :builder)))

                  :trader
                  (let [tradeable-goods (filter #(rules/can-trade-good? current-game executor-player %)
                                                [:corn :indigo :sugar :tobacco :coffee])]
                    (if (seq tradeable-goods)
                      (let [good (ai/select-best-trade current-game executor-player tradeable-goods)]
                        (if good
                          (handle-good-choice good :trader)
                          (handle-skip-role :trader)))
                      (handle-skip-role :trader)))

                  :captain
                  (let [shippable-goods (filter #(pos? (get-in executor-player [:goods %] 0))
                                                [:corn :indigo :sugar :tobacco :coffee])]
                    (if (seq shippable-goods)
                      (let [good (ai/select-best-shipping current-game executor-player shippable-goods)]
                        (if good
                          (handle-good-choice good :captain)
                          (handle-skip-role :captain)))
                      (handle-skip-role :captain)))

                  ;; For automatic roles, trigger automatic execution  
                  (:mayor :craftsman)
                  (handle-automatic-role-execution current-game)

                  ;; For other roles, do nothing
                  nil)))))))))

(defn handle-automatic-role-execution [game-data]
  "Automatically execute roles that don't require choices"
  (let [current-role (:selected-role game-data)]
    (case current-role
      :mayor
      (do
        (swap! game-state assoc :game-state (rules/execute-role game-data :mayor nil))
        (swap! game-state update :game-state rules/advance-role-execution))

      :craftsman
      (do
        (swap! game-state assoc :game-state (rules/execute-role game-data :craftsman nil))
        (swap! game-state update :game-state rules/advance-role-execution))

      :prospector
      ;; Prospector requires no choices from any player
      (let [executor (current-role-executor game-data)
            updated-game (rules/execute-role game-data :prospector (:id executor))]
        (swap! game-state assoc :game-state (rules/advance-role-execution updated-game)))

      ;; Other roles require player choices
      nil)))

;; Game state watcher for AI turns
(defn execute-ai-turn-async [game-data]
  "Execute AI turn with visual feedback and heuristic decision-making"
  (let [ai-player (if (= (:phase game-data) :role-execution)
                    (current-role-executor game-data)
                    (when (:is-ai (current-player game-data)) (current-player game-data)))]
    (when ai-player
      (case (:phase game-data)
        :role-selection
        (let [available-roles (:available-roles game-data)
              player-id (:id ai-player)
              ;; Use heuristic AI to evaluate all roles
              role-scores (map (fn [role]
                                 [role (ai/evaluate-role-utility game-data player-id role)])
                               available-roles)
              sorted-scores (sort-by second > role-scores)
              best-role (first (first sorted-scores))
              best-score (second (first sorted-scores))]
          ;; Log decision process to console
          (js/console.log "=== AI Role Selection ===" (:name ai-player))
          (js/console.log "Available roles and scores:")
          (doseq [[role score] sorted-scores]
            (js/console.log (str "  " (name role) ": " score)))
          (js/console.log (str "Selected: " (name best-role) " (score: " best-score ")"))

          ;; Update display and execute selection
          (reset! ai-action-display {:show true :player (:name ai-player)
                                     :action (str "Selected " (name best-role) " role (score: " best-score ")")})
          (add-log-entry (str "Selected " (name best-role) " role (score: " best-score ")") (:name ai-player))
          (js/setTimeout #(do
                            (reset! ai-action-display {:show false :player "" :action ""})
                            (handle-role-selection best-role)) 250))

        :role-execution
        (let [selected-role (:selected-role game-data)
              executor-player (current-role-executor game-data)]
          (case selected-role
            :settler
            (let [face-up-plantations (:face-up-plantations game-data)
                  quarry-supply (:quarry-supply game-data)
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
                (let [;; Use heuristic AI for plantation selection
                      plantation-scores (map (fn [p]
                                               [p (ai/evaluate-plantation-value game-data executor-player p)])
                                             available-choices)
                      sorted-scores (sort-by second > plantation-scores)
                      best-choice (first (first sorted-scores))
                      best-score (second (first sorted-scores))]
                  ;; Log decision
                  (js/console.log "=== AI Plantation Selection ===" (:name executor-player))
                  (doseq [[p score] sorted-scores]
                    (js/console.log (str "  " (name p) ": " score)))
                  (js/console.log (str "Selected: " (name best-choice) " (score: " best-score ")"))

                  (reset! ai-action-display {:show true :player (:name executor-player)
                                             :action (str "Took " (name best-choice) " (score: " best-score ")")})
                  (add-log-entry (str "Took " (name best-choice) " (score: " best-score ")") (:name executor-player))
                  (js/setTimeout #(do
                                    (reset! ai-action-display {:show false :player "" :action ""})
                                    (handle-plantation-choice best-choice)) 250))))

            :builder
            (let [;; Calculate affordable buildings with quarry discounts
                  affordable-buildings (keys (filter (fn [[building-key building-info]]
                                                       (let [occupied-quarries (count (filter #(and (= (:type %) :quarry)
                                                                                                    (pos? (:colonists %)))
                                                                                              (:plantations executor-player)))
                                                             base-cost (:cost building-info)
                                                             actual-cost (max 1 (- base-cost (min occupied-quarries (dec base-cost))))]
                                                         (and (>= (:money executor-player) actual-cost)
                                                              (pos? (get (:building-supply game-data) building-key 0))
                                                              (not (some #(= (:type %) building-key) (:buildings executor-player))))))
                                                     state/buildings))]
              (if (seq affordable-buildings)
                (let [;; Use heuristic AI for building selection
                      building-scores (map (fn [building-key]
                                             (let [building-info (get state/buildings building-key)]
                                               [building-key (ai/evaluate-building-synergy game-data executor-player
                                                                                           building-key building-info)]))
                                           affordable-buildings)
                      sorted-scores (sort-by second > building-scores)
                      best-building (first (first sorted-scores))
                      best-score (second (first sorted-scores))
                      building-name (-> best-building name (clojure.string/replace "-" " "))]
                  ;; Log decision
                  (js/console.log "=== AI Building Selection ===" (:name executor-player))
                  (doseq [[b score] sorted-scores]
                    (js/console.log (str "  " (name b) ": " score)))
                  (js/console.log (str "Selected: " (name best-building) " (score: " best-score ")"))

                  (reset! ai-action-display {:show true :player (:name executor-player)
                                             :action (str "Built " building-name " (score: " best-score ")")})
                  (add-log-entry (str "Built " building-name " (score: " best-score ")") (:name executor-player))
                  (js/setTimeout #(do
                                    (reset! ai-action-display {:show false :player "" :action ""})
                                    (handle-building-choice best-building)) 250))
                (do
                  (reset! ai-action-display {:show true :player (:name executor-player) :action "Skipped building (can't afford)"})
                  (add-log-entry "Skipped building (can't afford)" (:name executor-player))
                  (js/setTimeout #(do
                                    (reset! ai-action-display {:show false :player "" :action ""})
                                    (handle-skip-role :builder)) 250))))

            :trader
            (let [tradeable-goods (filter #(rules/can-trade-good? game-data executor-player %)
                                          [:corn :indigo :sugar :tobacco :coffee])]
              (if (seq tradeable-goods)
                (let [;; Use heuristic AI for trade selection
                      trade-scores (map (fn [good]
                                          [good (ai/evaluate-trade-value game-data executor-player good)])
                                        tradeable-goods)
                      sorted-scores (sort-by second > trade-scores)
                      best-good (first (first sorted-scores))
                      best-score (second (first sorted-scores))]
                  ;; Log decision
                  (js/console.log "=== AI Trade Selection ===" (:name executor-player))
                  (doseq [[g score] sorted-scores]
                    (js/console.log (str "  " (name g) ": " score)))
                  (js/console.log (str "Selected: " (name best-good) " (score: " best-score ")"))

                  (reset! ai-action-display {:show true :player (:name executor-player)
                                             :action (str "Sold " (name best-good) " (score: " best-score ")")})
                  (add-log-entry (str "Sold " (name best-good) " (score: " best-score ")") (:name executor-player))
                  (js/setTimeout #(do
                                    (reset! ai-action-display {:show false :player "" :action ""})
                                    (handle-good-choice best-good :trader)) 250))
                (do
                  (reset! ai-action-display {:show true :player (:name executor-player) :action "Skipped trading (no goods)"})
                  (add-log-entry "Skipped trading (no goods)" (:name executor-player))
                  (js/setTimeout #(do
                                    (reset! ai-action-display {:show false :player "" :action ""})
                                    (handle-skip-role :trader)) 250))))

            :captain
            (let [shippable-goods (filter #(pos? (get-in executor-player [:goods %] 0))
                                          [:corn :indigo :sugar :tobacco :coffee])]
              (if (seq shippable-goods)
                (let [;; Use heuristic AI for shipping selection
                      ship-scores (map (fn [good]
                                         [good (ai/evaluate-shipping-option game-data executor-player good)])
                                       shippable-goods)
                      sorted-scores (sort-by second > ship-scores)
                      best-good (first (first sorted-scores))
                      best-score (second (first sorted-scores))]
                  ;; Log decision
                  (js/console.log "=== AI Shipping Selection ===" (:name executor-player))
                  (doseq [[g score] sorted-scores]
                    (js/console.log (str "  " (name g) ": " score)))
                  (js/console.log (str "Selected: " (name best-good) " (score: " best-score ")"))

                  (reset! ai-action-display {:show true :player (:name executor-player)
                                             :action (str "Shipped " (name best-good) " (score: " best-score ")")})
                  (add-log-entry (str "Shipped " (name best-good) " (score: " best-score ")") (:name executor-player))
                  (js/setTimeout #(do
                                    (reset! ai-action-display {:show false :player "" :action ""})
                                    (handle-good-choice best-good :captain)) 250))
                (do
                  (reset! ai-action-display {:show true :player (:name executor-player) :action "Skipped captain (no goods)"})
                  (add-log-entry "Skipped captain (no goods)" (:name executor-player))
                  (js/setTimeout #(do
                                    (reset! ai-action-display {:show false :player "" :action ""})
                                    (handle-skip-role :captain)) 250))))

            (:mayor :craftsman)
            (let []
              (reset! ai-action-display {:show true :player (:name executor-player) :action (str "Executed " (name selected-role))})
              (add-log-entry (str "Executed " (name selected-role)) (:name executor-player))
              (js/setTimeout #(do
                                (reset! ai-action-display {:show false :player "" :action ""})
                                (swap! game-state update :game-state
                                       (fn [gd]
                                         (when gd
                                           (-> (rules/execute-role gd selected-role (:id executor-player))
                                               (rules/end-role-execution)))))) 250))

            nil))))))

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
        available-goods (cond
                          (= role :captain)
                          ;; For captain, check if goods can actually be shipped
                          (filter #(and (pos? (get-in current-player-data [:goods %] 0))
                                        (rules/find-ship-for-good (:ships game-data) % 1))
                                  [:corn :indigo :sugar :tobacco :coffee])

                          (= role :trader)
                          ;; For trader, check if goods can actually be traded (not already in trading house)
                          (filter #(rules/can-trade-good? game-data current-player-data %)
                                  [:corn :indigo :sugar :tobacco :coffee])

                          :else
                          ;; For other roles, just check if player has the good
                          (filter #(pos? (get-in current-player-data [:goods %] 0))
                                  [:corn :indigo :sugar :tobacco :coffee]))
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
    [:h4.player-name (str (:name player) (when current? " ⭐"))]
    [:div.player-quick-stats
     [:span.money-badge "💰$" (:money player)]
     [:span.vp-badge "🏆" (:victory-points player)]
     [:span.san-juan-badge "🏘️" (get player :san-juan-colonists 0)]]]

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

(defn game-log-ui []
  [:div.game-log
   [:h3 "📜 Game Log"
    (when (:active @historical-view)
      [:span.historical-indicator
       " (Viewing Historical State - "
       [:button.back-to-current {:on-click return-to-current-state}
        "Back to Current"]
       ")"])]
   [:div.log-entries
    (if (seq @game-log)
      ;; Show all entries in chronological order (oldest first)
      (doall
       (for [[idx entry] (map-indexed vector @game-log)]
         ^{:key idx}
         [:div.log-entry
          {:class (when (= idx (:state-index @historical-view)) "selected")
           :on-click #(view-historical-state idx)}
          [:span.turn-number (:turn entry)]
          (when (:player entry)
            [:span.player (:player entry) ":"])
          [:span.message (:message entry)]]))
      [:div.log-empty "No events yet..."])]])

(defn victory-points-breakdown-compact [player-breakdown]
  "Compact VP breakdown component for main pane display"
  [:div.vp-breakdown-compact
   [:div.vp-summary
    [:div.vp-category-compact
     [:span.vp-icon "🚢"]
     [:span.vp-label "Ship:"]
     [:span.vp-value (:shipping-vps player-breakdown)]]
    [:div.vp-category-compact
     [:span.vp-icon "🏗️"]
     [:span.vp-label "Build:"]
     [:span.vp-value (:building-vps player-breakdown)]]
    [:div.vp-category-compact
     [:span.vp-icon "📦"]
     [:span.vp-label "Goods:"]
     [:span.vp-value (:goods-vps player-breakdown)]]
    (when (pos? (:large-building-bonuses player-breakdown))
      [:div.vp-category-compact
       [:span.vp-icon "🏛️"]
       [:span.vp-label "Bonus:"]
       [:span.vp-value (:large-building-bonuses player-breakdown)]])]
   [:div.vp-total-compact
    [:span.vp-total-label "Total: "]
    [:span.vp-total-value (:total-vps player-breakdown)]]])

(defn game-over-main-pane [game-data]
  "Game over display in the main action area"
  (let [players-with-breakdown (map (fn [player]
                                      (let [breakdown (state/calculate-victory-points-breakdown player)]
                                        (assoc player :vp-breakdown breakdown)))
                                    (:players game-data))
        sorted-players (sort-by #(get-in % [:vp-breakdown :total-vps]) > players-with-breakdown)
        winner (first sorted-players)]
    [:div.game-over-main-pane
     [:div.game-over-header
      [:h2 "🏆 Game Over!"]
      [:div.winner-announcement
       [:span "👑 Winner: "]
       [:span.winner-name (:name winner)]
       [:span.winner-score " (" (get-in winner [:vp-breakdown :total-vps]) " VP)"]]]

     [:div.final-scores-table
      [:h3 "Final Scores:"]
      [:table.scores-table
       [:thead
        [:tr
         [:th "Player"]
         [:th "🚢 Ship"]
         [:th "🏛️ Build"]
         [:th "Total VP"]]]
       [:tbody
        (for [player sorted-players]
          ^{:key (:id player)}
          [:tr {:class (when (= player winner) "winner-row")}
           [:td.player-name (:name player)]
           [:td.ship-points (get-in player [:vp-breakdown :shipping-vps])]
           [:td.building-points (get-in player [:vp-breakdown :building-vps])]
           [:td.total-points (get-in player [:vp-breakdown :total-vps])]])]]]

     [:div.game-over-actions
      [:button.new-game {:on-click #(swap! game-state assoc :game-state (create-new-game))}
       "New Game"]
      [:button.ai-game {:on-click #(swap! game-state assoc :game-state (create-ai-only-game))}
       "AI vs AI"]]]))

(defn game-board []
  (let [display-state (get-display-game-state)
        game-data (:game-state display-state)
        is-historical (:historical display-state)]
    (cond
      ;; Normal game display (including game over overlay)
      game-data
      (let [current-player-data (current-player game-data)]
        [:div.game-board-compact
         ;; Historical state indicator
         (when is-historical
           [:div.historical-banner
            "📊 Viewing Historical State - Log Entry #" (:log-index display-state)])

         ;; Expanded header bar with game state info
         [:div.header-bar-expanded
          [:div.header-row-1
           [:h2 "🏝️ Puerto Rico"]
           [:div.game-status
            [:span.status-item "📅 Round " (:round game-data)]
            [:span.status-item
             (if (:game-over game-data)
               "🏆 Game Over"
               (str "⚡ " (name (:phase game-data))))]
            [:span.status-item "👑 " (:name (state/current-governor game-data))]
            [:span.status-item "👤 " (if current-player-data
                                       (:name current-player-data)
                                       "None")]]]

          [:div.header-row-2
           ;; Key supplies
           [:div.supply-group
            [:span.supply-label "🏆 VP: "] [:span.supply-value (:victory-point-supply game-data)]
            [:span.supply-label "👥 Colonists: "] [:span.supply-value (:colonist-supply game-data)]
            [:span.supply-label "🚢 Ship: "] [:span.supply-value (get game-data :colonist-ship 0)]]

           ;; Available plantations
           [:div.supply-group
            [:span.supply-label "🌱 Plantations: "]
            (let [face-up (:face-up-plantations game-data)
                  deck-count (count (:plantation-supply game-data))
                  discard-count (count (:plantation-discard game-data))]
              [:span
               (for [[idx plantation-type] (map-indexed vector face-up)]
                 ^{:key idx} [:span.supply-chip (name plantation-type)])
               [:span.supply-chip (str "deck:" deck-count)]
               [:span.supply-chip (str "discard:" discard-count)]
               [:span.supply-chip (str "quarries:" (get game-data :quarry-supply 0))]])]

           ;; Goods supply
           [:div.supply-group
            [:span.supply-label "📦 Goods: "]
            (for [[good count] (sort-by first (:goods-supply game-data))]
              ^{:key good} [:span.supply-chip (str (name good) ":" count)])]

           ;; Trading house
           (when (seq (:trading-house game-data))
             [:div.supply-group
              [:span.supply-label "🏪 Trading: "]
              (for [item (:trading-house game-data)]
                (let [good-name (if (map? item) (:good item) item)]
                  ^{:key (str "th-" good-name)} [:span.supply-chip (name good-name)]))])

           ;; Ships
           [:div.supply-group
            [:span.supply-label "⛵ Ships: "]
            (for [[idx ship] (map-indexed vector (:ships game-data))]
              ^{:key idx} [:span.supply-chip
                           (if (:good ship)
                             (str (name (:good ship)) " " (:amount ship 0) "/" (:capacity ship 0))
                             (str "Empty/" (:capacity ship 0)))])]]]

         ;; Players in horizontal row (compact)
         [:div.players-row
          (for [[idx player] (map-indexed vector (:players game-data))]
            ^{:key (:id player)}
            [player-board player (= idx (:current-player-idx game-data))])]

         ;; Main content area - action area (1/3) and game log (2/3)
         [:div.main-content-area
          [:div.action-area-narrow
           (cond
            ;; Show game over screen in main pane
             (:game-over game-data)
             [game-over-main-pane game-data]

            ;; Historical state - just show static info
             is-historical
             [:div.historical-view
              [:h3 "📊 Historical Game State"]
              [:p "This is the game state before the selected log entry was executed."]
              [:p "Game interactions are disabled in historical view."]]

            ;; Current state - show interactive elements
             :else
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
               ;; Auto-execute AI turns with visual feedback
                 ai-player
                 (let [ai-display @ai-action-display]
                   (if (:show ai-display)
                     [:div.ai-action-display
                      [:h3 "🤖 " (:player ai-display)]
                      [:p (:action ai-display)]]
                     [:div.waiting
                      [:h3 "⏳ AI Processing"]
                      [:p (:name ai-player) " is making a decision..."]]))

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
                       ^{:key role} [role-card role available? gold-amount handle-role-selection]))]])))]

          [:div.game-log-expanded
           [game-log-ui]]]])

      ;; No game started
      :else
      [:div.no-game
       [:h1 "🏝️ Puerto Rico"]
       [:p "Welcome to the Puerto Rico board game!"]
       [:div.game-mode-selection
        [:button.game-mode-button {:on-click #(swap! game-state assoc :game-state (create-new-game))}
         "👤 Play as Human"
         [:p.button-description "Play against AI opponents"]]
        [:button.game-mode-button {:on-click #(swap! game-state assoc :game-state (create-ai-only-game))}
         "🤖 Watch AI Battle"
         [:p.button-description "Watch 3 AI players compete"]]]])))

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
