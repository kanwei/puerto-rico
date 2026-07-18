(ns puerto-rico.core
  (:require [clojure.string :as str]
            [reagent.core :as reagent]
            [reagent.dom.client :as rdc]
            [puerto-rico.game.state :as state]
            [puerto-rico.game.rules :as rules]
            [puerto-rico.ai.heuristic :as ai]
            [puerto-rico.ai.personalities :as personalities]))

;; Forward declaration for function used before definition
(declare handle-automatic-role-execution)

;; Simple game state atom for demo
(defonce game-state (reagent/atom {:game-state nil}))

;; State for viewing historical game states
(defonce historical-view (reagent/atom {:active false :state-index nil}))

;; State for tracking AI action display
(defonce ai-action-display (reagent/atom {:show false :player "" :action ""}))

;; Game log state
(defonce game-log (reagent/atom []))

(defn role-display-name [role]
  ;; :prospector-2 is the second prospector placard in 5-player games
  (str/replace (name role) #"-2$" ""))

(defn add-log-entry [message & [player-name]]
  (let [current-game (:game-state @game-state)
        round-num (:round current-game 1)
        current-player-idx (:current-player-idx current-game)
        players-count (count (:players current-game))
        ;; Calculate turn number within round (1-based)
        turn-num (if current-player-idx (inc current-player-idx) 1)
        turn-label (str round-num "." turn-num)
        entry {:turn                turn-label
               :message             message
               :player              player-name
               :game-state-snapshot current-game            ; Store the game state before this move
               :timestamp           (.getTime (js/Date.))}]
    (swap! game-log conj entry)))
;; No limit - keep all entries to see start of game

(defn view-historical-state [log-index]
  "Switch to viewing a historical game state"
  (swap! historical-view assoc :active true :state-index log-index))

(defn return-to-current-state []
  "Return to viewing the current game state"
  (swap! historical-view assoc :active false :state-index nil))

(defn get-display-game-state []
  "Get the game state that should be displayed (current or historical).
   Pure - AI turns are driven by a watch on game-state, never by rendering."
  (if (:active @historical-view)
    ;; Return historical state
    (let [log-index (:state-index @historical-view)
          log-entries @game-log]
      (if (and log-index (< log-index (count log-entries)))
        {:game-state (:game-state-snapshot (nth log-entries log-index))
         :historical true
         :log-index  log-index}
        @game-state))
    ;; Return current state
    {:game-state (:game-state @game-state)
     :historical false}))

(defn clear-log []
  (reset! game-log []))

;; Initialize a real game with players
(defn create-new-game []
  (clear-log)
  (let [players [(state/new-player 1 "Alice (Human)")
                 (let [personality (personalities/assign-random-personality)]
                   (-> (state/new-player 2 "Bob")
                       (assoc :is-ai true)
                       (assoc :personality personality)))
                 (let [personality (personalities/assign-random-personality)]
                   (-> (state/new-player 3 "Carol")
                       (assoc :is-ai true)
                       (assoc :personality personality)))]]
    (add-log-entry "New game started with 3 players")
    (state/new-game-state players)))

(defn create-ai-only-game []
  (clear-log)
  (let [players [(let [personality (personalities/assign-random-personality)]
                   (-> (state/new-player 1 "Alice")
                       (assoc :is-ai true)
                       (assoc :personality personality)))
                 (let [personality (personalities/assign-random-personality)]
                   (-> (state/new-player 2 "Bob")
                       (assoc :is-ai true)
                       (assoc :personality personality)))
                 (let [personality (personalities/assign-random-personality)]
                   (-> (state/new-player 3 "Carol")
                       (assoc :is-ai true)
                       (assoc :personality personality)))]]
    (add-log-entry "New AI-only game started with 3 players")
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
        ;; Only log for human players (AI logs with score)
        (when-not (:is-ai current-player-data)
          (let [gold-on-role (get-in current-game [:role-gold role] 0)
                role-msg (if (> gold-on-role 0)
                           (str "🎭 Selected " (role-display-name role) " role (+" gold-on-role " gold)")
                           (str "🎭 Selected " (role-display-name role) " role"))]
            (add-log-entry role-msg (:name current-player-data))
            (when (contains? #{:prospector :prospector-2} role)
              (add-log-entry (str "💰 Gained 1 doubloon from Prospector") (:name current-player-data)))))
        (swap! game-state assoc :game-state new-game-state)

        ;; Auto-execute roles that don't require player choices
        (when (contains? #{:mayor :craftsman :prospector :prospector-2} role)
          (handle-automatic-role-execution new-game-state))

        (js/console.log "Role selected:" role "New game state:" new-game-state)))))

;; Role execution handlers
(defn handle-plantation-choice [plantation-type]
  (let [current-game (:game-state @game-state)]
    (when current-game
      (let [executor-player (current-role-executor current-game)
            player-id (:id executor-player)]
        (when (and executor-player player-id)
          (let [game-after (rules/execute-role current-game :settler player-id plantation-type)
                ;; The hacienda bonus draw does not consume the regular take
                new-game-state (if (= plantation-type :random-from-deck)
                                 game-after
                                 (rules/advance-role-execution game-after))]
            ;; Only log for human players (AI logs with score)
            (when-not (:is-ai executor-player)
              (add-log-entry (str "🌱 Took " (name plantation-type) " plantation") (:name executor-player)))
            (swap! game-state assoc :game-state new-game-state)
            (js/console.log "Plantation chosen:" plantation-type "for player:" (:name executor-player) "New game state:" new-game-state)))))))

(defn handle-building-choice [building-key]
  (let [current-game (:game-state @game-state)]
    (when current-game
      (let [executor-player (current-role-executor current-game)
            player-id (:id executor-player)]
        (when (and executor-player player-id)
          (let [building-info (get state/buildings building-key)
                base-cost (:cost building-info)
                ;; Same cost the engine charges: quarry discount + builder privilege
                actual-cost (rules/building-cost current-game executor-player building-key)
                discount (- base-cost actual-cost)
                new-game-state (-> current-game
                                   (rules/execute-role :builder player-id building-key)
                                   (rules/advance-role-execution))]
            ;; Only log for human players (AI logs with score)
            (when-not (:is-ai executor-player)
              (let [cost-msg (if (> discount 0)
                               (str " for $" actual-cost " (was $" base-cost ", -" discount " discount)")
                               (str " for $" actual-cost))]
                (add-log-entry (str "🏗️ Built " (name building-key) cost-msg) (:name executor-player))))
            (swap! game-state assoc :game-state new-game-state)
            (js/console.log "Building chosen:" building-key "for player:" (:name executor-player) "New game state:" new-game-state)))))))

(defn handle-good-choice [good-type role]
  (let [current-game (:game-state @game-state)]
    (when current-game
      (let [executor-player (current-role-executor current-game)
            player-id (:id executor-player)
            executor-idx (:role-execution-current-idx current-game)]
        (when (and executor-player player-id)
          (let [;; Capture state before action
                money-before (:money executor-player)
                vp-before (:victory-points executor-player)
                goods-before (get-in executor-player [:goods good-type] 0)

                ;; Execute role. A captain turn that loaded nothing counts as
                ;; a pass so the looping captain phase always terminates.
                game-after-role (rules/execute-role current-game role player-id good-type)
                new-game-state (rules/advance-role-execution
                                (if (and (= role :captain)
                                         (identical? game-after-role current-game))
                                  (rules/pass-captain-turn game-after-role)
                                  game-after-role))

                ;; Get updated player state
                executor-after (nth (:players new-game-state) executor-idx)
                money-after (:money executor-after)
                vp-after (:victory-points executor-after)
                goods-after (get-in executor-after [:goods good-type] 0)

                ;; Calculate changes
                money-gained (- money-after money-before)
                vp-gained (- vp-after vp-before)
                goods-shipped (- goods-before goods-after)]

            ;; Only log for human players (AI logs with details)
            (when-not (:is-ai executor-player)
              (let [action-text (case role
                                  :trader (str "💰 Sold " (name good-type)
                                               " for " money-gained " doubloons")
                                  :captain (str "🚢 Shipped " goods-shipped " " (name good-type)
                                                " for " vp-gained " VP")
                                  (str "Used " (name good-type)))]
                (add-log-entry action-text (:name executor-player))))

            (swap! game-state assoc :game-state new-game-state)
            (js/console.log "Good chosen:" good-type "for role:" role "for player:" (:name executor-player) "New game state:" new-game-state)))))))

(defn handle-wharf-choice
  "Captain phase: ship ALL goods of one kind to the supply via the wharf"
  [good-type]
  (let [current-game (:game-state @game-state)]
    (when current-game
      (let [executor-player (current-role-executor current-game)
            player-id (:id executor-player)
            executor-idx (:role-execution-current-idx current-game)]
        (when (and executor-player player-id)
          (let [amount (get-in executor-player [:goods good-type] 0)
                vp-before (:victory-points executor-player)
                game-after-role (rules/execute-role current-game :captain player-id good-type :wharf)
                new-game-state (rules/advance-role-execution
                                (if (identical? game-after-role current-game)
                                  (rules/pass-captain-turn game-after-role)
                                  game-after-role))
                vp-after (:victory-points (nth (:players new-game-state) executor-idx))]
            (when-not (:is-ai executor-player)
              (add-log-entry (str "⚓ Wharf-shipped " amount " " (name good-type)
                                  " for " (- vp-after vp-before) " VP")
                             (:name executor-player)))
            (swap! game-state assoc :game-state new-game-state)))))))

(defn handle-skip-role [role]
  (let [current-game (:game-state @game-state)]
    (when current-game
      (let [executor-player (current-role-executor current-game)
            player-id (:id executor-player)]
        (when (and executor-player player-id)
          (let [new-game-state (rules/advance-role-execution
                                (if (= role :captain)
                                  (rules/pass-captain-turn current-game)
                                  current-game))]
            ;; Only log for human players (AI already logs its skip)
            (when-not (:is-ai executor-player)
              (let [action-text (case role
                                  :trader "💼 Skipped trading (no goods)"
                                  :captain "⛵ Skipped shipping (no goods)"
                                  :builder "🔨 Skipped building (can't afford)"
                                  :settler "🚫 Skipped settler (no plantations)"
                                  "⏭️ Skipped")]
                (add-log-entry action-text (:name executor-player))))
            (swap! game-state assoc :game-state new-game-state)
            (js/console.log "Player skipped role:" role "for player:" (:name executor-player))))))))

(defn handle-automatic-role-execution [game-data]
  "Automatically execute roles that don't require choices. Mayor and craftsman
   execute once for the whole table; the prospector only affects the selector -
   all three end the role immediately."
  (let [current-role (:selected-role game-data)
        executor (current-role-executor game-data)]
    (case current-role
      (:mayor :craftsman)
      (if (= (:role-execution-current-idx game-data) (:role-selector-idx game-data))
        (swap! game-state assoc :game-state
               (-> game-data
                   (rules/execute-role current-role nil)
                   (rules/end-role-execution)))
        ;; Shouldn't happen (these roles end right after the selector executes)
        (swap! game-state update :game-state rules/advance-role-execution))

      (:prospector :prospector-2)
      (swap! game-state assoc :game-state
             (-> game-data
                 (rules/execute-role current-role (:id executor))
                 (rules/end-role-execution)))

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
              personality (:personality ai-player)
              personality-fns (personalities/get-personality-functions personality)
              role-weights ((:role-weights personality-fns) game-data player-id)
              ;; Combine heuristic evaluation with personality weights
              role-scores (map (fn [role]
                                 (let [heuristic-score (ai/evaluate-role-utility game-data player-id role)
                                       personality-weight (get role-weights role 50)]
                                   [role (* heuristic-score (/ personality-weight 50))]))
                               available-roles)
              sorted-scores (sort-by second > role-scores)
              best-role (first (first sorted-scores))
              best-score (second (first sorted-scores))]
          ;; Log decision process to console
          (js/console.log "=== AI Role Selection ===" (:name ai-player)
                          "(" (personalities/personality-name personality) ")")
          (js/console.log "Available roles and scores:")
          (doseq [[role score] sorted-scores]
            (js/console.log (str "  " (name role) ": " score)))
          (js/console.log (str "Selected: " (name best-role) " (score: " best-score ")"))

          ;; Update display and execute selection
          (reset! ai-action-display {:show   true :player (:name ai-player)
                                     :action (str "🎭 Selected " (role-display-name best-role) " role")})
          (let [gold-on-role (get-in game-data [:role-gold best-role] 0)
                role-msg (if (> gold-on-role 0)
                           (str "🎭 Selected " (role-display-name best-role) " role (+" gold-on-role " gold, score: " (Math/round best-score) ")")
                           (str "🎭 Selected " (role-display-name best-role) " role (score: " (Math/round best-score) ")"))]
            (add-log-entry role-msg (:name ai-player))
            (when (contains? #{:prospector :prospector-2} best-role)
              (add-log-entry (str "💰 Gained 1 doubloon from Prospector") (:name ai-player))))
          (js/setTimeout #(do
                            (reset! ai-action-display {:show false :player "" :action ""})
                            (handle-role-selection best-role)) 250))

        :role-execution
        (let [selected-role (:selected-role game-data)
              executor-player (current-role-executor game-data)]
          (case selected-role
            :settler
            (let [current-player-idx (:role-execution-current-idx game-data)
                  has-hacienda (state/has-occupied-building? executor-player :hacienda)
                  hacienda-used (get-in game-data [:hacienda-used current-player-idx] false)
                  personality (:personality executor-player)
                  personality-fns (personalities/get-personality-functions personality)
                  plantation-score-fn (:plantation-score personality-fns)
                  wants-hacienda? (and has-hacienda (not hacienda-used)
                                       (seq (:plantation-supply game-data))
                                       (not (rules/island-full? executor-player))
                                       (or (= personality :builder) (> (rand) 0.2)))
                  ;; Guard against watcher re-entry before any async work
                  _ (reset! ai-action-display {:show true :player (:name executor-player)
                                               :action "🌱 Choosing a plantation..."})
                  ;; Hacienda bonus draw happens synchronously, before the regular take
                  game-after-hacienda
                  (if wants-hacienda?
                    (let [updated-game (-> (rules/execute-role game-data :settler (:id executor-player) :random-from-deck)
                                           (assoc-in [:hacienda-used current-player-idx] true))
                          drawn-type (-> updated-game :players (nth current-player-idx) :plantations last :type)]
                      (swap! game-state assoc :game-state updated-game)
                      (add-log-entry (str "🏛️ Used Hacienda - drew " (name drawn-type)) (:name executor-player))
                      updated-game)
                    game-data)
                  executor-now (nth (:players game-after-hacienda) current-player-idx)
                  can-take-quarry (and (pos? (:quarry-supply game-after-hacienda))
                                       (rules/may-take-quarry? game-after-hacienda current-player-idx))
                  available-choices (when-not (rules/island-full? executor-now)
                                      (concat (distinct (:face-up-plantations game-after-hacienda))
                                              (when can-take-quarry [:quarry])))]
              (if (seq available-choices)
                (let [;; Combine heuristic evaluation with personality scoring
                      plantation-scores (map (fn [p]
                                               (let [heuristic-score (ai/evaluate-plantation-value game-after-hacienda executor-now p)
                                                     personality-score (plantation-score-fn game-after-hacienda executor-now p)]
                                                 [p (* heuristic-score (/ personality-score 50))]))
                                             available-choices)
                      sorted-scores (sort-by second > plantation-scores)
                      best-choice (first (first sorted-scores))
                      best-score (second (first sorted-scores))]
                  ;; Log decision
                  (js/console.log "=== AI Plantation Selection ===" (:name executor-player)
                                  "(" (personalities/personality-name personality) ")")
                  (doseq [[p score] sorted-scores]
                    (js/console.log (str "  " (name p) ": " score)))
                  (js/console.log (str "Selected: " (name best-choice) " (score: " best-score ")"))

                  (reset! ai-action-display {:show   true :player (:name executor-player)
                                             :action (str "🌱 Took " (name best-choice) " plantation")})
                  (add-log-entry (str "🌱 Took " (name best-choice) " plantation (score: " (Math/round best-score) ")") (:name executor-player))
                  (js/setTimeout #(do
                                    (reset! ai-action-display {:show false :player "" :action ""})
                                    (handle-plantation-choice best-choice)) 250))
                (do
                  (add-log-entry "🚫 Skipped settler (island full)" (:name executor-player))
                  (js/setTimeout #(do
                                    (reset! ai-action-display {:show false :player "" :action ""})
                                    (handle-skip-role :settler)) 250))))

            :builder
            (let [personality (:personality executor-player)
                  personality-fns (personalities/get-personality-functions personality)
                  building-score-fn (:building-score personality-fns)
                  ;; Same legality check the engine uses (discounts, supply, city space)
                  affordable-buildings (filter #(rules/can-build-building? game-data executor-player %)
                                               (keys state/buildings))]
              (if (seq affordable-buildings)
                (let [;; Combine heuristic evaluation with personality scoring
                      building-scores (map (fn [building-key]
                                             (let [building-info (get state/buildings building-key)
                                                   heuristic-score (ai/evaluate-building-synergy game-data executor-player
                                                                                                 building-key building-info)
                                                   personality-score (building-score-fn game-data executor-player
                                                                                        building-key building-info)]
                                               [building-key (* heuristic-score (/ personality-score 50))]))
                                           affordable-buildings)
                      sorted-scores (sort-by second > building-scores)
                      best-building (first (first sorted-scores))
                      best-score (second (first sorted-scores))
                      building-name (-> best-building name (str/replace "-" " "))]
                  ;; Log decision
                  (js/console.log "=== AI Building Selection ===" (:name executor-player)
                                  "(" (personalities/personality-name personality) ")")
                  (doseq [[b score] sorted-scores]
                    (js/console.log (str "  " (name b) ": " score)))
                  (js/console.log (str "Selected: " (name best-building) " (score: " best-score ")"))

                  ;; Actual cost as the engine will charge it
                  (let [base-cost (get-in state/buildings [best-building :cost])
                        actual-cost (rules/building-cost game-data executor-player best-building)
                        discount (- base-cost actual-cost)
                        cost-msg (if (> discount 0)
                                   (str " for $" actual-cost " (was $" base-cost ", -" discount " discount)")
                                   (str " for $" actual-cost))]
                    (reset! ai-action-display {:show   true :player (:name executor-player)
                                               :action (str "🏗️ Built " building-name)})
                    (add-log-entry (str "🏗️ Built " building-name cost-msg " (score: " (Math/round best-score) ")") (:name executor-player)))
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
                                          [:corn :indigo :sugar :tobacco :coffee])
                  personality (:personality executor-player)
                  personality-fns (personalities/get-personality-functions personality)
                  trade-multiplier (:trade-multiplier personality-fns)]
              (if (seq tradeable-goods)
                (let [;; Combine heuristic evaluation with personality multiplier
                      trade-scores (map (fn [good]
                                          (let [base-value (ai/evaluate-trade-value game-data executor-player good)]
                                            [good (* base-value trade-multiplier)]))
                                        tradeable-goods)
                      sorted-scores (sort-by second > trade-scores)
                      best-good (first (first sorted-scores))
                      best-score (second (first sorted-scores))]
                  ;; Log decision
                  (js/console.log "=== AI Trade Selection ===" (:name executor-player)
                                  "(" (personalities/personality-name personality) ")")
                  (doseq [[g score] sorted-scores]
                    (js/console.log (str "  " (name g) ": " score)))
                  (js/console.log (str "Selected: " (name best-good) " (score: " best-score ")"))

                  (reset! ai-action-display {:show   true :player (:name executor-player)
                                             :action (str "💰 Sold " (name best-good))})
                  ;; Trade value as the engine pays it: base + markets + privilege
                  (let [base-value (get rules/good-values best-good 0)
                        market-bonus (+ (if (rules/has-occupied-building? executor-player :small-market) 1 0)
                                        (if (rules/has-occupied-building? executor-player :large-market) 2 0))
                        privilege-bonus (if (= (:role-execution-current-idx game-data)
                                               (:role-selector-idx game-data))
                                          1 0)
                        total-value (+ base-value market-bonus privilege-bonus)]
                    (add-log-entry (str "💰 Sold " (name best-good) " for " total-value " doubloons (score: " (Math/round best-score) ")") (:name executor-player)))
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
            (let [executor-idx (:role-execution-current-idx game-data)
                  ;; Goods that can actually be loaded on a cargo ship
                  shippable-goods (filter #(and (pos? (get-in executor-player [:goods %] 0))
                                                (rules/find-ship-for-good (:ships game-data) %
                                                                          (get-in executor-player [:goods %] 0)))
                                          [:corn :indigo :sugar :tobacco :coffee])
                  personality (:personality executor-player)
                  personality-fns (personalities/get-personality-functions personality)
                  ship-multiplier (:ship-multiplier personality-fns)]
              (cond
                (seq shippable-goods)
                (let [;; Combine heuristic evaluation with personality multiplier
                      ship-scores (map (fn [good]
                                         (let [base-value (ai/evaluate-shipping-option game-data executor-player good)]
                                           [good (* base-value ship-multiplier)]))
                                       shippable-goods)
                      sorted-scores (sort-by second > ship-scores)
                      best-good (first (first sorted-scores))
                      best-score (second (first sorted-scores))
                      [_ ship] (rules/find-ship-for-good (:ships game-data) best-good
                                                         (get-in executor-player [:goods best-good] 0))
                      loadable (min (get-in executor-player [:goods best-good] 0)
                                    (- (:capacity ship) (:amount ship)))]
                  ;; Log decision
                  (js/console.log "=== AI Shipping Selection ===" (:name executor-player)
                                  "(" (personalities/personality-name personality) ")")
                  (doseq [[g score] sorted-scores]
                    (js/console.log (str "  " (name g) ": " score)))
                  (js/console.log (str "Selected: " (name best-good) " (score: " best-score ")"))

                  (reset! ai-action-display {:show   true :player (:name executor-player)
                                             :action (str "🚢 Shipped " (name best-good))})
                  (add-log-entry (str "🚢 Shipped " loadable " " (name best-good) " (score: " (Math/round best-score) ")") (:name executor-player))
                  (js/setTimeout #(do
                                    (reset! ai-action-display {:show false :player "" :action ""})
                                    (handle-good-choice best-good :captain)) 250))

                ;; No cargo ship can take anything - use the wharf if available
                (rules/can-use-wharf? game-data executor-idx)
                (let [best-good (->> (:goods executor-player)
                                     (filter #(pos? (second %)))
                                     (sort-by second)
                                     last
                                     first)
                      amount (get-in executor-player [:goods best-good] 0)]
                  (reset! ai-action-display {:show true :player (:name executor-player)
                                             :action (str "⚓ Wharf-shipped " (name best-good))})
                  (add-log-entry (str "⚓ Wharf-shipped " amount " " (name best-good)) (:name executor-player))
                  (js/setTimeout #(do
                                    (reset! ai-action-display {:show false :player "" :action ""})
                                    (handle-wharf-choice best-good)) 250))

                :else
                (do
                  (reset! ai-action-display {:show true :player (:name executor-player) :action "⛵ Skipped shipping (no goods)"})
                  (add-log-entry "⛵ Skipped shipping (no goods)" (:name executor-player))
                  (js/setTimeout #(do
                                    (reset! ai-action-display {:show false :player "" :action ""})
                                    (handle-skip-role :captain)) 250))))

            (:prospector :prospector-2)
            ;; Prospector auto-executes - only selector gets 1 gold
            (let [is-selector (= (:role-execution-current-idx game-data) (:role-selector-idx game-data))]
              (when is-selector
                (reset! ai-action-display {:show   true :player (:name executor-player)
                                           :action "💰 Gained 1 doubloon from Prospector"}))
              ;; Don't log here - already logged when role was selected
              (js/setTimeout #(do
                                (reset! ai-action-display {:show false :player "" :action ""})
                                (handle-automatic-role-execution game-data)) 250))

            (:mayor :craftsman)
            ;; These roles execute for all players at once, only when role selector
            (if (= (:role-execution-current-idx game-data) (:role-selector-idx game-data))
              (do
                (reset! ai-action-display {:show true :player (:name executor-player) :action (str "Executed " (name selected-role))})
                (add-log-entry (str "Executed " (name selected-role) " for all players") (:name executor-player))
                (js/setTimeout #(do
                                  (reset! ai-action-display {:show false :player "" :action ""})
                                  (swap! game-state update :game-state
                                         (fn [gd]
                                           (when gd
                                             (-> (rules/execute-role gd selected-role (:id executor-player))
                                                 (rules/end-role-execution)))))) 250))
              ;; Not role selector - advance synchronously (shouldn't happen: these
              ;; roles end right after the selector executes)
              (swap! game-state update :game-state rules/advance-role-execution))

            nil))))))

(defn ai-turn-needed? [game-data]
  (boolean
   (and game-data
        (not (:game-over game-data))
        (case (:phase game-data)
          :role-selection (:is-ai (current-player game-data))
          :role-execution (:is-ai (current-role-executor game-data))
          false))))

(defn check-ai-turn!
  "Run the next AI turn if one is due and no AI action is already in flight"
  []
  (let [game-data (:game-state @game-state)]
    (when (and (ai-turn-needed? game-data)
               (not (:show @ai-action-display)))
      (execute-ai-turn-async game-data))))

;; Drive AI turns from game-state changes (never from rendering: mutating
;; reagent atoms during a render pass freezes the React render queue)
(add-watch game-state :ai-driver
           (fn [_ _ old-val new-val]
             (when (not= (:game-state old-val) (:game-state new-val))
               (js/setTimeout check-ai-turn! 50))))

;; Components
(defn role-card [role available? gold-amount on-select]
  [:div.role-card {:class    (when-not available? "disabled")
                   :on-click (when available? #(on-select role))}
   [:h3 (role-display-name role)]
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
         (:prospector :prospector-2) "Get money"
         "Choose this role")]])

(defn plantation-choice-ui [game-data]
  (let [face-up-plantations (:face-up-plantations game-data)
        quarry-supply (:quarry-supply game-data)
        plantation-deck (:plantation-supply game-data)
        current-player-data (current-role-executor game-data)
        has-hacienda (state/has-occupied-building? current-player-data :hacienda)
        role-selector-idx (:role-selector-idx game-data)
        current-player-idx (:role-execution-current-idx game-data)
        is-role-selector (= current-player-idx role-selector-idx)
        island-full (rules/island-full? current-player-data)
        can-take-quarry (and (pos? quarry-supply)
                             (rules/may-take-quarry? game-data current-player-idx))
        ;; Track if hacienda bonus was used this turn
        hacienda-used (get-in game-data [:hacienda-used current-player-idx] false)]
    [:div.role-execution
     [:h2 "🌱 Settler - Choose a Plantation"]
     (if island-full
       [:div
        [:p "Your island is full (12 spaces) - you cannot take any more tiles."]
        [:div.choice-grid
         [:div.choice-card.skip {:on-click #(handle-skip-role :settler)}
          [:h3 "Skip"]
          [:p "Island full"]]]]
       [:div
        ;; Hacienda bonus - draw from deck BEFORE regular turn
        (when (and has-hacienda (not hacienda-used) (seq plantation-deck))
          [:div.hacienda-bonus
           [:h3 "🏛️ Hacienda Bonus (use before regular turn)"]
           [:p "You may draw a random plantation from the deck first"]
           [:div.choice-grid
            [:div.choice-card {:on-click (fn []
                                           ;; Draw from deck
                                           (handle-plantation-choice :random-from-deck)
                                           ;; Mark hacienda as used
                                           (swap! game-state assoc-in [:game-state :hacienda-used current-player-idx] true))}
             [:h3 "Draw Random"]
             [:p (str (count plantation-deck) " tiles in deck")]]
            [:div.choice-card.skip {:on-click #(swap! game-state assoc-in [:game-state :hacienda-used current-player-idx] true)}
             [:h3 "Skip Bonus"]
             [:p "Continue to regular turn"]]]])

        [:p "Select a plantation tile to place on your island:"]
        [:div.choice-grid
         ;; Face-up plantation choices
         (for [[idx plantation-type] (map-indexed vector face-up-plantations)]
           ^{:key (str "face-up-" idx)}
           [:div.choice-card {:on-click #(handle-plantation-choice plantation-type)}
            [:h3 (name plantation-type)]])

         ;; Quarry choice (settler privilege, or occupied construction hut)
         (when can-take-quarry
           ^{:key "quarry"}
           [:div.choice-card {:on-click #(handle-plantation-choice :quarry)}
            [:h3 "quarry"]
            [:p "Available: " quarry-supply]
            (when-not is-role-selector
              [:p.privilege-text "Construction Hut allows quarry"])])

         ;; Skip option (always available)
         ^{:key "skip"}
         [:div.choice-card.skip {:on-click #(handle-skip-role :settler)}
          [:h3 "Skip"]
          [:p "Don't take any tile"]]]])]))

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
        ;; Same legality check the engine uses (discounts, supply, city space)
        affordable-buildings (filter (fn [[building-key _]]
                                       (rules/can-build-building? game-data current-player-data building-key))
                                     state/buildings)]
    [:div.role-execution
     [:h2 "🏗️ Builder - Choose a Building"]
     (if (seq affordable-buildings)
       [:div
        [:p "Select a building to construct or skip (You have $" (:money current-player-data) "):"]
        [:div.building-grid
         ;; Building options - show the discounted price the engine will charge
         (for [[building-key building-info] affordable-buildings]
           ^{:key building-key}
           [building-card building-key
            (assoc building-info :cost (rules/building-cost game-data current-player-data building-key))
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
        executor-idx (:role-execution-current-idx game-data)
        available-goods (cond
                          (= role :captain)
                          ;; For captain, check if goods can actually be shipped
                          (filter #(and (pos? (get-in current-player-data [:goods %] 0))
                                        (rules/find-ship-for-good (:ships game-data) %
                                                                  (get-in current-player-data [:goods %] 0)))
                                  [:corn :indigo :sugar :tobacco :coffee])

                          (= role :trader)
                          ;; For trader, check if goods can actually be traded (not already in trading house)
                          (filter #(rules/can-trade-good? game-data current-player-data %)
                                  [:corn :indigo :sugar :tobacco :coffee])

                          :else
                          ;; For other roles, just check if player has the good
                          (filter #(pos? (get-in current-player-data [:goods %] 0))
                                  [:corn :indigo :sugar :tobacco :coffee]))
        ;; Captain only: wharf lets the player ship all goods of one kind to the supply
        wharf-available (and (= role :captain)
                             (rules/can-use-wharf? game-data executor-idx))
        wharf-goods (when wharf-available
                      (filter #(pos? (get-in current-player-data [:goods %] 0))
                              [:corn :indigo :sugar :tobacco :coffee]))
        ;; Loading on a cargo ship is mandatory when possible
        must-load (and (= role :captain) (seq available-goods))
        title (case role
                :trader "Trader - Choose Good to Sell"
                :captain "Captain - Choose Good to Ship"
                "Choose a Good")]
    [:div.role-execution
     [:h2 "📦 " title]
     (if (or (seq available-goods) (seq wharf-goods))
       [:div
        (when (seq available-goods)
          [:div
           [:p (if must-load
                 "You must load goods - select a good from your inventory:"
                 "Select a good from your inventory:")]
           [:div.choice-grid
            (for [good-type available-goods]
              ^{:key good-type}
              [:div.choice-card {:on-click #(handle-good-choice good-type role)}
               [:h3 (name good-type)]
               [:p "You have: " (get-in current-player-data [:goods good-type] 0)]])]])
        (when (seq wharf-goods)
          [:div
           [:p "⚓ Or use your wharf to ship ALL goods of one kind to the supply:"]
           [:div.choice-grid
            (for [good-type wharf-goods]
              ^{:key (str "wharf-" (name good-type))}
              [:div.choice-card {:on-click #(handle-wharf-choice good-type)}
               [:h3 "⚓ " (name good-type)]
               [:p "Ship all " (get-in current-player-data [:goods good-type] 0) " for VP"]])]])
        ;; Loading is mandatory when possible - skip only allowed otherwise
        (when-not must-load
          [:div.choice-grid
           [:div.choice-card.skip {:on-click #(handle-skip-role role)}
            [:h3 "Skip"]
            [:p (if (= role :captain) "Don't use wharf" "Don't sell")]]])]
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
    [:h4.player-name (str (:name player)
                          (when (:is-ai player)
                            (str " (" (personalities/personality-name (:personality player)) ")"))
                          (when current? " ⭐"))]
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
               total-capacity 1]                            ; plantations always have 1 worker slot
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
          {:class    (when (= idx (:state-index @historical-view)) "selected")
           :on-click #(view-historical-state idx)}
          [:span.turn-number (:turn entry)]
          (when (:player entry)
            [:span.player (:player entry) ":"])
          [:span.message (:message entry)]]))
      [:div.log-empty "No events yet..."])]])

(defn game-over-main-pane [game-data]
  "Game over display in the main action area"
  (let [players-with-breakdown (map (fn [player]
                                      (let [breakdown (state/calculate-victory-points-breakdown player)]
                                        (assoc player :vp-breakdown breakdown)))
                                    (:players game-data))
        sorted-players (sort-by (juxt #(get-in % [:vp-breakdown :total-vps])
                                      state/tiebreaker-value)
                                #(compare %2 %1)
                                players-with-breakdown)
        ;; Prefer the engine's winner (it applies the doubloons+goods tiebreaker)
        winner-id (get-in game-data [:winner :id])
        winner (or (first (filter #(= (:id %) winner-id) players-with-breakdown))
                   (first sorted-players))]
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
                   (for [role (or (:roles game-data) state/roles)]
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

;; React 18 root (created once, survives hot reloads)
(defonce app-root
  (delay (rdc/create-root (.getElementById js/document "app"))))

(defn init
  "Initialize the application"
  []
  (js/console.log "Initializing Puerto Rico application...")
  (rdc/render @app-root [main-panel]))

(defn test-nrepl-connection
  "Test function to verify Shadow-CLJS nREPL is working"
  []
  (js/alert "🎉 Shadow-CLJS nREPL Connection Test Successful! 🎉")
  (js/console.log "Shadow-CLJS nREPL is connected and working properly")
  (js/console.log "Current timestamp:" (js/Date.))
  :success)

;; Initialize immediately
(init)
