(ns puerto-rico.core
  (:require [clojure.string :as str]
            [cljs.reader :as reader]
            [reagent.core :as reagent]
            [reagent.dom.client :as rdc]
            [puerto-rico.game.state :as state]
            [puerto-rico.game.rules :as rules]
            [puerto-rico.ai.heuristic :as ai]))

;; Forward declarations for functions used before definition
(declare handle-automatic-role-execution check-ai-turn!)

;; AI moves are computed by the backend (MCTS) via same-origin API calls; when
;; it is unreachable the game falls back to the local heuristic AI.
(def mcts-simulations 200)

;; Simple game state atom for demo
(defonce game-state (reagent/atom {:game-state nil}))

;; State for viewing historical game states
(defonce historical-view (reagent/atom {:active false :state-index nil}))

;; State for tracking AI action display
(defonce ai-action-display (reagent/atom {:show false :player "" :action ""}))

;; Game log state
(defonce game-log (reagent/atom []))

;; --------------------------------------------------------------------------
;; Game setup: player count (3-5) and a controller per seat
;; --------------------------------------------------------------------------

(def player-names ["Alice" "Bob" "Carol" "Dave" "Erin"])

;; controller kinds: :human, :heuristic, :mcts (rollout), :model (NN)
(defonce available-models (reagent/atom []))        ;; ["gen1.onnx" ...]
(defonce setup (reagent/atom {:num-players 3
                              :controllers [{:kind :human} {:kind :mcts} {:kind :mcts}
                                            {:kind :mcts} {:kind :mcts}]}))

(defn fetch-models! []
  (-> (js/fetch "/api/models")
      (.then #(.text %))
      (.then #(reset! available-models (vec (:models (reader/read-string %)))))
      (.catch (fn [_] (reset! available-models [])))))

(defn controller-label [c]
  (case (:kind c)
    :human "Human"
    :heuristic "Heuristic"
    :mcts "MCTS"
    :model (str/replace (:model c) #"\.onnx$" "")
    "AI"))

(defn controller->value [c]
  (case (:kind c)
    :human "human"
    :heuristic "heuristic"
    :mcts "mcts"
    :model (str "model:" (:model c))
    "mcts"))

(defn value->controller [v]
  (cond
    (= v "human") {:kind :human}
    (= v "heuristic") {:kind :heuristic}
    (= v "mcts") {:kind :mcts}
    (str/starts-with? v "model:") {:kind :model :model (subs v 6)}
    :else {:kind :mcts}))

(defn role-display-name [role]
  ;; :prospector-2 is the second prospector placard in 5-player games
  (str/replace (name role) #"-2$" ""))

(defn add-log-entry [message & [player-name stats]]
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
               :stats               stats                    ; AI search diagnostics (nil for humans)
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

;; Build a game from the setup config: one controller per seat. Each player
;; carries :is-ai (for the engine's mayor path + AI-turn detection) and
;; :controller (for routing the move to the right bot/model).
(defn create-configured-game [num-players controllers]
  (clear-log)
  (let [players (mapv (fn [i c]
                        (-> (state/new-player (inc i) (nth player-names i))
                            (assoc :is-ai (not= (:kind c) :human)
                                   :controller c)))
                      (range num-players)
                      controllers)]
    (add-log-entry (str "New game — " num-players " players"))
    (state/new-game-state players)))

(defn start-configured-game! []
  (let [{:keys [num-players controllers]} @setup]
    (swap! game-state assoc :game-state
           (create-configured-game num-players (take num-players controllers)))))

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

(defn- mayor-move! [args]
  (let [current-game (:game-state @game-state)
        executor (current-role-executor current-game)]
    (when executor
      (swap! game-state assoc :game-state
             (rules/apply-move current-game
                               {:type :role-action :role :mayor
                                :player-id (:id executor) :args args})))))

;; Click an empty circle to place a colonist from hand; click a filled circle
;; to take it back into hand. dest-kind is :plantation or :building; idx is the
;; tile's position on that player's board.
(defn handle-mayor-place-at [dest-kind idx] (mayor-move! [:place-at dest-kind idx]))
(defn handle-mayor-remove-at [dest-kind idx] (mayor-move! [:remove-at dest-kind idx]))

(defn handle-mayor-done []
  (let [executor (current-role-executor (:game-state @game-state))]
    (when (and executor (not (:is-ai executor)))
      (add-log-entry "👷 Done placing colonists" (:name executor)))
    (mayor-move! [])))

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

(defn- apply-ai-move!
  "Apply an AI move that was decided for the state game-data. Drops it if the
   game moved on (stale), logs + animates otherwise."
  [game-data ai-player move label & [stats]]
  (let [current (:game-state @game-state)]
    (cond
      (not= current game-data)
      (do (reset! ai-action-display {:show false :player "" :action ""})
          (js/setTimeout check-ai-turn! 50))

      (and move (rules/valid-move? current (:player-id move) move))
      (let [description (str (describe-move current move) label)]
        (add-log-entry description (:name ai-player) stats)
        (reset! ai-action-display {:show true :player (:name ai-player) :action description})
        (reagent/flush)                     ;; RAF batching can be throttled; flush
        (js/setTimeout
         #(do (reset! ai-action-display {:show false :player "" :action ""})
              (swap! game-state assoc :game-state (rules/apply-move current move))
              (reagent/flush))
         150))

      :else
      (do (js/console.error "AI produced no valid move:" (pr-str move))
          (reset! ai-action-display {:show false :player "" :action ""})))))

(defn- fetch-backend-move
  "POST the game state (and optional model) to the backend; calls back with the
   parsed {:move .. :stats ..} decision, or nil on failure so the caller can
   fall back to the heuristic."
  [game-data model callback]
  (-> (js/fetch "/api/ai-move"
                (clj->js {:method "POST"
                          :headers {"Content-Type" "application/edn"}
                          :body (pr-str (cond-> {:game-state game-data
                                                 :simulations mcts-simulations}
                                          model (assoc :model model)))}))
      (.then (fn [resp]
               (if (.-ok resp)
                 (.text resp)
                 (throw (js/Error. (str "HTTP " (.-status resp)))))))
      (.then (fn [text] (callback (reader/read-string text))))
      (.catch (fn [err]
                (js/console.warn "Backend AI unavailable, using local heuristic:" err)
                (callback nil)))))

(defn execute-ai-turn-async
  "One AI decision, routed by the acting player's controller: a heuristic bot
   is computed locally; an MCTS/model bot asks the backend (with the chosen
   model), falling back to the heuristic if the backend is down."
  [game-data]
  (let [ai-player (if (= (:phase game-data) :role-execution)
                    (current-role-executor game-data)
                    (when (:is-ai (current-player game-data)) (current-player game-data)))
        ctrl (:controller ai-player)]
    (when ai-player
      (reset! ai-action-display {:show true :player (:name ai-player)
                                 :action (str "🤖 Thinking (" (controller-label ctrl) ")…")})
      (reagent/flush)
      (if (= (:kind ctrl) :heuristic)
        ;; local heuristic - no backend round-trip
        (js/setTimeout #(apply-ai-move! game-data ai-player
                                        (heuristic-fallback-move game-data) "") 10)
        ;; backend MCTS (rollouts when :model is nil) or NN model
        (fetch-backend-move
         game-data (:model ctrl)
         (fn [decision]
           (let [backend-move (:move decision)]
             (apply-ai-move! game-data ai-player
                             (or backend-move (heuristic-fallback-move game-data))
                             (if backend-move "" " (local)")
                             (:stats decision)))))))))

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

;; --------------------------------------------------------------------------
;; Visual helpers
;; --------------------------------------------------------------------------

(def good-color
  {:coffee "#5c3d2e" :corn "#b3982f" :indigo "#3b4d7a"
   :sugar "#cdb67c" :tobacco "#7a3f38" :quarry "#8f8b84"})

(defn titlecase [s]
  (str/join " " (map str/capitalize (str/split (str/replace (name s) "-" " ") #" "))))

(defn building-description
  "Hover text for a building. Uses the rulebook description when present, and
   synthesizes a short one for plain production buildings (which carry none)."
  [building-type]
  (let [info (get state/buildings building-type)]
    (or (:description info)
        (when-let [g (:good info)]
          (str "Production building — makes 1 " (name g)
               " in the craftsman phase for each colonist working it."))
        "")))

(defn dot [good]
  [:span.dot {:style {:background-color (get good-color good "#8f8b84")}}])

(defn worker-pips [occupied total]
  [:span.pips
   (for [i (range total)]
     ^{:key i} [:span.pip {:class (if (< i occupied) "filled" "empty")}])])

;; Components
(defn role-card [role available? gold-amount on-select]
  [:div.role-card {:class    (when-not available? "disabled")
                   :on-click (when available? #(on-select role))}
   [:div.role-card-head
    [:span.role-card-title (titlecase (role-display-name role))]
    (when (and gold-amount (> gold-amount 0))
      [:span.role-gold (str "+$" gold-amount)])]
   [:span.role-card-desc
    (case role
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
  "One building in the builder tableau. Buildable cards are clickable and show
   the price the player will actually pay (after quarry/privilege discounts);
   owned and unaffordable cards are shown dimmed, like an in-play board."
  [game-data player building-key]
  (let [info (get state/buildings building-key)
        owned? (some #(= (:type %) building-key) (:buildings player))
        buildable? (rules/can-build-building? game-data player building-key)
        base-cost (:cost info)
        cost (rules/building-cost game-data player building-key)
        discount (- base-cost cost)
        supply (get-in game-data [:building-supply building-key] 0)
        state-class (cond owned? "owned" buildable? "buildable" :else "locked")]
    [:div.building-card {:class state-class
                         :on-click (when buildable? #(handle-building-choice building-key))}
     [:div.building-card-top
      [:span.building-cost-badge (if (pos? discount) (str "$" cost) (str "$" base-cost))]
      [:span.building-name (titlecase building-key)]
      [:span.building-vp (str (:vp info) " VP")]]
     [:div.worker-slots
      (for [i (range (:worker info))]
        ^{:key i} [:span.worker-circle])]
     (when (:description info)
       [:div.building-benefit (:description info)])
     [:div.building-card-foot
      (cond
        owned? [:span.building-tag "Built"]
        (not buildable?) [:span.building-tag.locked-tag "Can't build"]
        (pos? discount) [:span.building-tag.discount-tag (str "was $" base-cost)]
        :else [:span])
      [:span.building-supply (str supply " left")]]]))

;; The board groups buildings into four columns by cost tier
(def building-columns
  {1 [:small-indigo-maker :small-sugar-maker :small-market :hacienda :construction-hut :small-warehouse]
   2 [:large-indigo-maker :large-sugar-maker :hospice :office :large-market :large-warehouse]
   3 [:tobacco-maker :coffee-maker :factory :university :harbor :wharf]
   4 [:guild-hall :residence :customs-house :city-hall :fortress]})

(defn building-choice-ui [game-data]
  (let [player (current-role-executor game-data)
        any-buildable? (some #(rules/can-build-building? game-data player %)
                             (keys state/buildings))]
    [:div.role-execution
     [:div.panel-head
      [:h2.panel-title "Builder — choose a building"]
      [:span.panel-sub (str "$" (:money player) " available"
                            (when-not any-buildable? " · nothing affordable"))]]
     [:div.building-columns
      (for [col [1 2 3 4]]
        ^{:key col}
        [:div.building-column
         (for [building-key (get building-columns col)]
           ^{:key building-key}
           [building-card game-data player building-key])])]
     [:div.builder-actions
      [:button.skip-button {:on-click #(handle-skip-role :builder)}
       "Skip — don't build"]]]))

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

(defn mayor-tile
  "One board tile in the mayor screen: a colored dot, the name, and a clickable
   worker circle per slot. Filled circles remove a colonist to hand; empty
   circles place one from hand (disabled when the hand is empty)."
  [dest-kind idx tile hand]
  (let [cap (if (= dest-kind :plantation)
              1
              (get-in state/buildings [(:type tile) :worker] 1))
        occ (:colonists tile 0)]
    [:div.mayor-tile
     (if (= dest-kind :plantation)
       [dot (:type tile)]
       [:span.dot.dot-building])
     [:span.tile-name (titlecase (:type tile))]
     [:div.mayor-circles
      (for [i (range cap)]
        ^{:key i}
        (if (< i occ)
          [:button.mayor-circle.filled
           {:title "Remove worker" :on-click #(handle-mayor-remove-at dest-kind idx)}]
          [:button.mayor-circle.empty
           {:title "Add worker" :disabled (zero? hand)
            :on-click #(handle-mayor-place-at dest-kind idx)}]))]]))

(defn mayor-placement-ui [game-data]
  (let [executor (current-role-executor game-data)
        hand (:colonists-in-hand executor)
        must-place? (and (pos? hand) (pos? (rules/empty-circle-count executor)))]
    [:div.role-execution
     [:div.panel-head
      [:h2.panel-title "Mayor — place colonists"]
      [:span.panel-sub (str hand " in hand")]]
     [:p.muted "Click an empty circle to assign a colonist, or a filled circle to take one back."]
     [:div.mayor-board
      (when (seq (:plantations executor))
        [:div.mayor-group
         [:div.section-label "Plantations"]
         [:div.mayor-tiles
          (for [[idx t] (map-indexed vector (:plantations executor))]
            ^{:key (str "p" idx)} [mayor-tile :plantation idx t hand])]])
      (when (seq (:buildings executor))
        [:div.mayor-group
         [:div.section-label "Buildings"]
         [:div.mayor-tiles
          (for [[idx t] (map-indexed vector (:buildings executor))]
            ^{:key (str "b" idx)} [mayor-tile :building idx t hand])]])]
     [:div.builder-actions
      [:button.skip-button {:on-click handle-mayor-auto-place} "Auto-place"]
      [:button.done-button {:disabled must-place? :on-click handle-mayor-done}
       (cond must-place? "Fill all circles first"
             (pos? hand) (str "Done — " hand " to San Juan")
             :else "Done")]]]))

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
  (let [me? (not (:is-ai player))
        san-juan (get player :san-juan-colonists 0)
        goods (filter #(pos? (second %)) (:goods player))]
    [:div.player-card {:class (when current? "active")}
     [:div.player-card-head
      [:span.player-name (:name player)]
      [:span.badge {:class (if me? "badge-you" "badge-ai")}
       (cond (and current? me?) "You · to act"
             me? "You"
             ;; bots show their controller (model name / MCTS / heuristic)
             :else (str (controller-label (:controller player))
                        (when current? " · to act")))]
      [:div.player-badges
       [:span.stat-badge.money {:title "Doubloons"} (str "$" (:money player))]
       [:span.stat-badge.vp {:title "Victory points"} (:victory-points player) " VP"]
       [:span.stat-badge {:title "City slots used (large buildings take 2 of 12)"}
        (state/city-slots-used player) "/12"]
       (when (pos? san-juan)
         [:span.stat-badge {:title "Colonists in San Juan"} san-juan " SJ"])]]

     (when (seq (:plantations player))
       [:div.card-section
        [:div.section-label "Plantations"]
        [:div.tile-list
         ;; one row per plantation TYPE, with a worked/total worker pip each
         (for [[ptype tiles] (sort-by #(get rules/good-values (first %) 99)
                                      (group-by :type (:plantations player)))]
           ^{:key ptype}
           [:div.tile-row
            [:span.tile-name (name ptype)]
            [worker-pips (count (filter #(pos? (:colonists % 0)) tiles)) (count tiles)]])]])

     (when (seq (:buildings player))
       [:div.card-section
        [:div.section-label "Buildings"]
        [:div.tile-list
         (for [[idx b] (map-indexed vector (:buildings player))]
           (let [cap (get-building-capacity (:type b))
                 vp (get-in state/buildings [(:type b) :vp] 0)
                 special (state/special-vp-for-building player (:type b))]
             ^{:key idx}
             [:div.tile-row.has-tip {:data-tip (building-description (:type b))}
              [:span.tile-name (titlecase (:type b))]
              [:span.tile-row-right
               (when (pos? vp)
                 [:span.vp-badge {:title "Victory points from this building"} vp " VP"])
               (when (pos? special)
                 [:span.special-badge
                  {:title "End-game bonus VP this building is worth at the current board state"}
                  "✦ +" special])
               [worker-pips (:colonists b 0) cap]]]))]])

     (when (seq goods)
       [:div.card-section
        [:div.section-label "Goods"]
        [:div.good-tags
         (for [[good amount] (sort-by #(get rules/good-values (first %) 99) goods)]
           ^{:key good} [:span.good-tag [dot good] (name good) [:span.good-count amount]])]])]))

(defn- fmt-pct [x] (str (js/Math.round (* 100 x)) "%"))
(defn- fmt-val [x] (when (number? x) (.toFixed x 2)))

(defn ai-stats-text
  "Compact one-line diagnostics of an AI decision's MCTS search: the value
   estimate for the line played, the simulation count, and the top candidate
   moves with their visit-share weight. game-data is the state before the move."
  [game-data {:keys [value sims candidates]}]
  (->> (concat
        (when value [(str "v " (fmt-val value))])
        (when sims [(str sims " sims")])
        (map (fn [c] (str (describe-move game-data (:move c)) " " (fmt-pct (:weight c))))
             (take 3 candidates)))
       (remove nil?)
       (str/join "  ·  ")))

(defn game-log-ui []
  [:div.game-log
   [:div.panel-head
    [:h2.panel-title "Game log"]
    (when (:active @historical-view)
      [:span.historical-indicator
       "viewing history "
       [:button.back-to-current {:on-click return-to-current-state}
        "back to current"]])]
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
          [:span.message (:message entry)]
          (when (:stats entry)
            [:span.log-stats {:title "AI search: value estimate · simulations · top moves by visit share"}
             (ai-stats-text (:game-state-snapshot entry) (:stats entry))])]))
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
      [:button.new-game {:on-click #(swap! game-state assoc :game-state (create-configured-game
                                                                         (:num-players @setup)
                                                                         (take (:num-players @setup) (:controllers @setup))))}
       "Rematch"]
      [:button.ai-game {:on-click #(swap! game-state assoc :game-state nil)}
       "New setup"]]]))

(defn setup-screen []
  (let [{:keys [num-players controllers]} @setup
        models @available-models
        options (concat [["Human" "human"] ["Heuristic" "heuristic"] ["MCTS (rollouts)" "mcts"]]
                        (map (fn [m] [(str/replace m #"\.onnx$" "") (str "model:" m)]) models))]
    [:div.start-screen
     [:div.start-card
      [:h1.game-title "Puerto Rico"]
      [:p.start-tagline "Set up a game — choose the number of players and who controls each seat."]

      [:div.setup-row
       [:span.setup-label "Players"]
       [:div.count-toggle
        (for [n [3 4 5]]
          ^{:key n}
          [:button.count-btn {:class (when (= n num-players) "active")
                              :on-click #(swap! setup assoc :num-players n)}
           n])]]

      [:div.setup-seats
       (for [i (range num-players)]
         ^{:key i}
         [:div.setup-seat
          [:span.seat-name (nth player-names i)]
          [:select.seat-select
           {:value (controller->value (nth controllers i))
            :on-change #(swap! setup assoc-in [:controllers i]
                               (value->controller (.. % -target -value)))}
           (for [[label val] options]
             ^{:key val} [:option {:value val} label])]])]

      (when (empty? models)
        [:p.setup-note "No trained models found — bots use rollout MCTS or the heuristic. Train one with train/loop.py to battle networks."])

      [:button.start-button.primary {:on-click start-configured-game!}
       [:span.start-button-title "Start game"]]]]))

(defn game-board []
  (let [display-state (get-display-game-state)
        game-data (:game-state display-state)
        is-historical (:historical display-state)]
    (cond
      ;; Normal game display (including game over overlay)
      game-data
      (let [current-player-data (current-player game-data)
            executor (current-role-executor game-data)
            phase-label (cond
                          (:game-over game-data) "Game over"
                          (= (:phase game-data) :role-selection) "Role selection"
                          (:storage-phase game-data) "Captain · storage"
                          (:craftsman-privilege-pending game-data) "Craftsman · privilege"
                          (:selected-role game-data) (titlecase (role-display-name (:selected-role game-data)))
                          :else (titlecase (:phase game-data)))
            turn-name (cond (:game-over game-data) "—"
                            (= (:phase game-data) :role-execution) (:name executor)
                            current-player-data (:name current-player-data)
                            :else "—")]
        [:div.board
         ;; Historical state indicator
         (when is-historical
           [:div.historical-banner
            "Viewing historical state — log entry #" (:log-index display-state)])

         ;; Header panel
         [:div.board-header
          [:div.header-top
           [:div.title-block
            [:h1.game-title "Puerto Rico"]
            [:span.round-label (str "Round " (:round game-data))]]
           [:div.header-meta
            [:span.meta-label "Phase"]
            [:span.phase-pill phase-label]
            [:span.meta-sep]
            [:span.meta-label "Current turn"]
            [:span.meta-value turn-name]]]

          [:div.header-stats
           [:div.stat-block
            [:span.stat-label "VP pool"]
            [:span.stat-num (:victory-point-supply game-data)]]
           [:div.stat-block
            [:span.stat-label "Colonists"]
            [:span.stat-num (:colonist-supply game-data)]]
           [:div.stat-block
            [:span.stat-label "Ship"]
            [:span.stat-num (get game-data :colonist-ship 0)]]
           [:div.stat-block
            [:span.stat-label "Quarries"]
            [:span.stat-num (get game-data :quarry-supply 0)]]
           [:div.stat-block.stat-wide
            [:span.stat-label "Plantations available"]
            [:div.chip-row
             (for [[ptype n] (sort-by (comp name first) (frequencies (:face-up-plantations game-data)))]
               ^{:key ptype}
               [:span.chip [dot ptype] (name ptype) (when (> n 1) (str " ×" n))])
             [:span.chip-muted (str "deck " (count (:plantation-supply game-data))
                                    " · discard " (count (:plantation-discard game-data)))]]]]

          [:div.header-supply
           ;; left: cargo ships + trading house
           [:div.supply-block
            [:span.stat-label "Cargo ships"]
            [:div.chip-row
             (for [[idx ship] (map-indexed vector (:ships game-data))]
               ^{:key idx}
               [:span.chip
                (if (:good ship)
                  [:span [dot (:good ship)] (str (name (:good ship)) " " (:amount ship 0) " / " (:capacity ship 0))]
                  (str "empty / " (:capacity ship 0)))])]]
           (when (seq (:trading-house game-data))
             [:div.supply-block
              [:span.stat-label "Trading house"]
              [:div.chip-row
               (for [[idx item] (map-indexed vector (:trading-house game-data))]
                 (let [g (if (map? item) (:good item) item)]
                   ^{:key idx} [:span.chip [dot g] (name g)]))]])
           ;; right: goods in supply
           [:div.supply-block.supply-goods
            [:span.stat-label "Goods in supply"]
            [:div.good-tags
             (for [[good n] (sort-by #(get rules/good-values (first %) 99) (:goods-supply game-data))]
               ^{:key good} [:span.good-tag [dot good] (name good) [:span.good-count n]])]]]]

         ;; Players (highlight the player who must actually act now)
         (let [acting-idx (if (= (:phase game-data) :role-execution)
                            (:role-execution-current-idx game-data)
                            (:current-player-idx game-data))]
           [:div.players-row
            (for [[idx player] (map-indexed vector (:players game-data))]
              ^{:key (:id player)}
              [player-board player (= idx acting-idx)])])

         ;; Full-width action panel (buildings get room to lay out like the board)
         [:div.action-panel
          (cond
             ;; Show game over screen in main pane
            (:game-over game-data)
            [game-over-main-pane game-data]

             ;; Historical state - just show static info
            is-historical
            [:div.historical-view
             [:h2.panel-title "Historical state"]
             [:p.muted "This is the game state before the selected log entry. Interactions are disabled."]]

             ;; Current state - show interactive elements
            :else
            (let [ai-player (cond
                              (= (:phase game-data) :role-execution)
                              (when (:is-ai executor) executor)
                              (= (:phase game-data) :role-selection)
                              (when (:is-ai current-player-data) current-player-data)
                              :else nil)]
              (cond
                 ;; Auto-execute AI turns with visual feedback
                ai-player
                (let [ai-display @ai-action-display]
                  [:div.ai-thinking
                   [:div.section-label "AI turn"]
                   [:div.ai-player-name (:name ai-player)]
                   [:div.ai-action-text (if (:show ai-display)
                                          (:action ai-display)
                                          "Making a decision…")]])

                 ;; Role execution phase - human turn
                (= (:phase game-data) :role-execution)
                [role-execution-ui game-data]

                 ;; Role selection phase - human turn
                :else
                [:div.roles-section
                 [:div.panel-head
                  [:h2.panel-title "Choose a role"]
                  [:span.panel-sub (str "Your turn, " (:name current-player-data))]]
                 [:div.roles-grid
                  (for [role (or (:roles game-data) state/roles)]
                    (let [available? (contains? (:available-roles game-data) role)
                          gold-amount (get-in game-data [:role-gold role] 0)]
                      ^{:key role} [role-card role available? gold-amount handle-role-selection]))]])))]

         ;; Full-width game log at the bottom
         [:div.log-panel
          [game-log-ui]]])

      ;; No game started - setup screen
      :else
      [setup-screen])))

(defn main-panel []
  [game-board])

;; React 18 root (created once, survives hot reloads)
(defonce app-root
  (delay (rdc/create-root (.getElementById js/document "app"))))

(defn init
  "Initialize the application"
  []
  (js/console.log "Initializing Puerto Rico application...")
  (fetch-models!)                       ;; populate bot/model dropdowns
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
