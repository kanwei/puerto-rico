(ns puerto-rico.core
  (:require [clojure.string :as str]
            [cljs.reader :as reader]
            [reagent.core :as reagent]
            [reagent.dom.client :as rdc]
            [puerto-rico.game.state :as state]
            [puerto-rico.game.rules :as rules]
            [puerto-rico.ai.heuristic :as ai]
            [puerto-rico.ai.personalities :as personalities]))

;; Forward declarations for functions used before definition
(declare handle-automatic-role-execution check-ai-turn!)

;; Backend that computes AI moves with MCTS. When it is unreachable the game
;; falls back to the local heuristic AI so it still works standalone.
(def api-base "http://localhost:8080")
(def mcts-simulations 200)

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
;; (AI players are driven by backend MCTS - no personalities anymore)
(defn create-new-game []
  (clear-log)
  (let [players [(state/new-player 1 "Alice (Human)")
                 (assoc (state/new-player 2 "Bob") :is-ai true)
                 (assoc (state/new-player 3 "Carol") :is-ai true)]]
    (add-log-entry "New game started with 3 players")
    (state/new-game-state players)))

(defn create-ai-only-game []
  (clear-log)
  (let [players [(assoc (state/new-player 1 "Alice") :is-ai true)
                 (assoc (state/new-player 2 "Bob") :is-ai true)
                 (assoc (state/new-player 3 "Carol") :is-ai true)]]
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
        ;; (the mayor now has placement decisions, so it is NOT auto-executed)
        (when (contains? #{:craftsman :prospector :prospector-2} role)
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
  "Run roles that mostly need no input. The craftsman produces for the whole
   table, then may owe the selector a privilege pick (which stays on screen);
   the prospector only affects the selector and ends immediately."
  (let [current-role (:selected-role game-data)
        executor (current-role-executor game-data)]
    (case current-role
      :craftsman
      (let [after-production (rules/execute-role game-data :craftsman nil)]
        (if (:craftsman-privilege-pending after-production)
          ;; selector must choose which extra good to take
          (swap! game-state assoc :game-state after-production)
          (swap! game-state assoc :game-state (rules/end-role-execution after-production))))

      (:prospector :prospector-2)
      (swap! game-state assoc :game-state
             (-> game-data
                 (rules/execute-role current-role (:id executor))
                 (rules/end-role-execution)))

      ;; Other roles require player choices
      nil)))

(defn handle-craftsman-privilege [good]
  (let [current-game (:game-state @game-state)
        selector (nth (:players current-game) (:role-selector-idx current-game))]
    (when-not (:is-ai selector)
      (add-log-entry (str "⚒️ Craftsman privilege: took an extra " (name good)) (:name selector)))
    (swap! game-state assoc :game-state
           (rules/apply-move current-game
                             {:type :role-action :role :craftsman
                              :player-id (:id selector) :args [:privilege good]}))))

;; --------------------------------------------------------------------------
;; Mayor placement (human)
;; --------------------------------------------------------------------------

(defn handle-mayor-place [dest-kind dest-key]
  (let [current-game (:game-state @game-state)
        executor (current-role-executor current-game)]
    (when executor
      (swap! game-state assoc :game-state
             (rules/apply-move current-game
                               {:type :role-action :role :mayor
                                :player-id (:id executor)
                                :args [:place-colonist dest-kind dest-key]})))))

(defn handle-mayor-done []
  (let [current-game (:game-state @game-state)
        executor (current-role-executor current-game)]
    (when executor
      (when-not (:is-ai executor)
        (add-log-entry "👷 Done placing colonists" (:name executor)))
      (swap! game-state assoc :game-state
             (rules/apply-move current-game
                               {:type :role-action :role :mayor
                                :player-id (:id executor) :args []})))))

(defn handle-mayor-auto-place
  "Place all remaining colonists with the heuristic, then finish the turn"
  []
  (let [current-game (:game-state @game-state)
        executor-idx (:role-execution-current-idx current-game)
        executor (current-role-executor current-game)
        pid (:id executor)]
    (when executor
      (let [placed (loop [gs current-game, n 0]
                     (if-let [args (and (< n 40)
                                        (ai/best-mayor-placement (nth (:players gs) executor-idx)))]
                       (recur (rules/apply-move gs {:type :role-action :role :mayor
                                                    :player-id pid :args args})
                              (inc n))
                       gs))]
        (when-not (:is-ai executor)
          (add-log-entry "👷 Auto-placed colonists" (:name executor)))
        (swap! game-state assoc :game-state
               (rules/apply-move placed {:type :role-action :role :mayor
                                         :player-id pid :args []}))))))

;; --------------------------------------------------------------------------
;; Captain storage (human)
;; --------------------------------------------------------------------------

(defn handle-storage-pick [op good]
  (let [current-game (:game-state @game-state)
        executor (current-role-executor current-game)]
    (when executor
      (when-not (:is-ai executor)
        (add-log-entry (str "📦 Kept " (if (= op :store-kind) "all " "one ") (name good))
                       (:name executor)))
      (swap! game-state assoc :game-state
             (rules/apply-move current-game
                               {:type :role-action :role :captain
                                :player-id (:id executor)
                                :args [op good]})))))

(defn handle-storage-done []
  (let [current-game (:game-state @game-state)
        executor (current-role-executor current-game)]
    (when executor
      (when-not (:is-ai executor)
        (add-log-entry "📦 Done storing (rest discarded)" (:name executor)))
      (swap! game-state assoc :game-state
             (rules/apply-move current-game
                               {:type :role-action :role :captain
                                :player-id (:id executor) :args []})))))

;; --------------------------------------------------------------------------
;; AI turns: computed on the backend with MCTS, applied locally.
;; Falls back to the local heuristic AI when the backend is unreachable.
;; --------------------------------------------------------------------------

(defn describe-move
  "Human-readable log line for an engine move"
  [game-data move]
  (let [args (:args move)
        pretty #(str/replace (name %) "-" " ")]
    (case (:type move)
      :select-role
      (let [gold (get-in game-data [:role-gold (:role move)] 0)]
        (str "🎭 Selected " (role-display-name (:role move)) " role"
             (when (pos? gold) (str " (+" gold " gold)"))))

      :role-action
      (case (:role move)
        :settler (cond
                   (empty? args) "🚫 Skipped settler"
                   (= (first args) :random-from-deck) "🏛️ Used Hacienda (drew a tile)"
                   :else (str "🌱 Took " (name (first args))))
        :builder (if (empty? args)
                   "🔨 Skipped building"
                   (str "🏗️ Built " (pretty (first args))))
        :trader (if (empty? args)
                  "💼 Skipped trading"
                  (str "💰 Sold " (name (first args))))
        :mayor (if (empty? args)
                 "👷 Done placing colonists"
                 (str "👷 Placed colonist on " (pretty (nth args 2))))
        :captain (cond
                   (= (first args) :store-kind) (str "📦 Kept all " (name (second args)))
                   (= (first args) :store-single) (str "📦 Kept one " (name (second args)))
                   (empty? args) (if (:storage-phase game-data)
                                   "📦 Done storing"
                                   "⛵ Passed shipping")
                   (= (second args) :wharf) (str "⚓ Wharf-shipped all " (name (first args)))
                   :else (str "🚢 Shipped " (name (first args))))
        :craftsman (if (= (first args) :privilege)
                     (str "⚒️ Craftsman privilege: extra " (name (second args)))
                     "⚒️ Produced goods (all players)")
        (:prospector :prospector-2) "💰 Prospector: +1 doubloon"
        "⏭️ Acted")
      "⏭️ Acted")))

(defn- heuristic-fallback-move [game-data]
  (let [actor (if (= (:phase game-data) :role-execution)
                (current-role-executor game-data)
                (current-player game-data))]
    (ai/ai-select-move game-data (:id actor))))

(defn- fetch-backend-move
  "POST the game state to the backend; calls back with the MCTS move or nil"
  [game-data callback]
  (-> (js/fetch (str api-base "/api/ai-move")
                (clj->js {:method "POST"
                          :headers {"Content-Type" "application/edn"}
                          :body (pr-str {:game-state game-data
                                         :simulations mcts-simulations})}))
      (.then (fn [resp]
               (if (.-ok resp)
                 (.text resp)
                 (throw (js/Error. (str "HTTP " (.-status resp)))))))
      (.then (fn [text] (callback (:move (reader/read-string text)))))
      (.catch (fn [err]
                (js/console.warn "Backend AI unavailable, using local heuristic:" err)
                (callback nil)))))

(defn execute-ai-turn-async
  "One AI decision: ask the backend for an MCTS move (heuristic fallback),
   then apply it through the engine"
  [game-data]
  (let [ai-player (if (= (:phase game-data) :role-execution)
                    (current-role-executor game-data)
                    (when (:is-ai (current-player game-data)) (current-player game-data)))]
    (when ai-player
      ;; guard against re-entry while the request is in flight
      (reset! ai-action-display {:show true :player (:name ai-player)
                                 :action "🤖 Thinking (MCTS)..."})
      (reagent/flush)
      (fetch-backend-move
       game-data
       (fn [backend-move]
         (let [current (:game-state @game-state)]
           (if (not= current game-data)
             ;; the game moved on while we were thinking - drop the stale move
             ;; and re-kick the driver (its watch already fired and was skipped)
             (do (reset! ai-action-display {:show false :player "" :action ""})
                 (js/setTimeout check-ai-turn! 50))
             (let [move (or backend-move (heuristic-fallback-move game-data))]
               (if (and move (rules/valid-move? current (:player-id move) move))
                 (let [description (str (describe-move current move)
                                        (when-not backend-move " (local)"))]
                   (add-log-entry description (:name ai-player))
                   (reset! ai-action-display {:show true :player (:name ai-player)
                                              :action description})
                   ;; reagent batches re-renders on requestAnimationFrame, which
                   ;; may be throttled outside user events - flush explicitly
                   (reagent/flush)
                   (js/setTimeout
                    #(do (reset! ai-action-display {:show false :player "" :action ""})
                         (swap! game-state assoc :game-state (rules/apply-move current move))
                         (reagent/flush))
                    150))
                 (do
                   (js/console.error "AI produced no valid move:" (pr-str move))
                   (reset! ai-action-display {:show false :player "" :action ""})))))))))))

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
        building-name (-> building-key name (str/replace "-" " "))
        description (:description building-info)]
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

     ;; Full ability description (untruncated)
     (when description
       [:div.building-benefit description])

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

(defn mayor-placement-ui [game-data]
  (let [executor (current-role-executor game-data)
        hand (:colonists-in-hand executor)
        {:keys [plantations buildings]} (rules/placement-destinations executor)
        can-place (and (pos? hand) (or (seq plantations) (seq buildings)))]
    [:div.role-execution
     [:h2 "👷 Mayor - Place Your Colonists"]
     [:p "Your board was picked up for re-arrangement. Colonists in hand: "
      [:strong hand]]
     (if can-place
       [:div
        (when (seq plantations)
          [:div
           [:p "🌱 Place on a plantation:"]
           [:div.choice-grid
            (for [p (sort plantations)]
              ^{:key (str "place-p-" (name p))}
              [:div.choice-card {:on-click #(handle-mayor-place :plantation p)}
               [:h3 (name p)]])]])
        (when (seq buildings)
          [:div
           [:p "🏢 Place in a building:"]
           [:div.choice-grid
            (for [b (sort buildings)]
              ^{:key (str "place-b-" (name b))}
              [:div.choice-card {:on-click #(handle-mayor-place :building b)}
               [:h3 (str/replace (name b) "-" " ")]])]])
        [:div.choice-grid
         [:div.choice-card.skip {:on-click handle-mayor-auto-place}
          [:h3 "Auto-place rest"]
          [:p "Let the computer finish"]]]]
       ;; Nothing placeable (hand empty or board full): the turn can end
       [:div.choice-grid
        [:div.choice-card.skip {:on-click handle-mayor-done}
         [:h3 "Done"]
         [:p (if (pos? hand)
               (str hand " colonist(s) to San Juan")
               "Finish placing")]]])]))

(defn storage-choice-ui [game-data]
  (let [executor-idx (:role-execution-current-idx game-data)
        executor (current-role-executor game-data)
        picks (get-in game-data [:storage-picks executor-idx])
        {:keys [kinds singles]} (rules/legal-storage-picks game-data executor-idx)
        slots (rules/warehouse-kinds-storable executor)]
    [:div.role-execution
     [:h2 "📦 Storage - Choose What to Keep"]
     [:p "Warehouse kinds: " (count (:kinds picks)) "/" slots
      " · Windrose single: " (if-let [s (:single picks)] (name s) "none")
      " - everything else returns to the supply"]
     (when (seq kinds)
       [:div
        [:p "🏬 Keep ALL goods of a kind (warehouse):"]
        [:div.choice-grid
         (for [g (sort kinds)]
           ^{:key (str "kind-" (name g))}
           [:div.choice-card {:on-click #(handle-storage-pick :store-kind g)}
            [:h3 (name g)]
            [:p "Keep all " (get-in executor [:goods g] 0)]])]])
     (when (seq singles)
       [:div
        [:p "🧭 Keep ONE single good (windrose):"]
        [:div.choice-grid
         (for [g (sort singles)]
           ^{:key (str "single-" (name g))}
           [:div.choice-card {:on-click #(handle-storage-pick :store-single g)}
            [:h3 (name g)]
            [:p "Keep 1 of " (get-in executor [:goods g] 0)]])]])
     [:div.choice-grid
      [:div.choice-card.skip {:on-click handle-storage-done}
       [:h3 "Done"]
       [:p "Discard the rest"]]]]))

(defn role-execution-ui [game-data]
  (let [selected-role (:selected-role game-data)
        executor-player (current-role-executor game-data)]
    (case selected-role
      :settler [plantation-choice-ui game-data]
      :builder [building-choice-ui game-data]
      :trader [good-choice-ui game-data :trader]
      :mayor [mayor-placement-ui game-data]
      :captain (if (:storage-phase game-data)
                 [storage-choice-ui game-data]
                 [good-choice-ui game-data :captain])
      :craftsman
      (if-let [candidates (:craftsman-privilege-pending game-data)]
        ;; selector picks which produced kind to take as the privilege
        [:div.role-execution
         [:h2 "⚒️ Craftsman - Choose Your Privilege"]
         [:p "You produced multiple kinds - take one extra good of a kind you made:"]
         [:div.choice-grid
          (for [good (sort candidates)]
            ^{:key (str "priv-" (name good))}
            [:div.choice-card {:on-click #(handle-craftsman-privilege good)}
             [:h3 (name good)]
             [:p "+1 " (name good)]])]]
        [:div.role-execution
         [:h2 "🔄 Craftsman - Executing Automatically"]
         [:p (:name executor-player) " is producing goods..."]])
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
