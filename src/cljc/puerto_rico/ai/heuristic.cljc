(ns puerto-rico.ai.heuristic
  "Heuristic-based AI implementation for Puerto Rico
   Uses direct evaluation functions instead of MCTS for better performance
   and more predictable strategic play."
  (:require [puerto-rico.game.state :as state]
            [puerto-rico.game.rules :as rules]))

;; ================================================================================
;; Role Selection Heuristics
;; ================================================================================

(defn evaluate-role-utility
  "Calculate utility score for each role based on player's current situation
   Returns a numerical score - higher is better, negative means avoid"
  [game-state player-id role]
  (let [player (state/player-by-id game-state player-id)
        total-goods (apply + (vals (:goods player)))
        money (:money player)
        buildings (:buildings player)
        plantations (:plantations player)
        empty-buildings (count (filter #(zero? (:colonists %)) buildings))
        empty-plantations (count (filter #(zero? (:colonists %)) plantations))
        total-empty-spots (+ empty-buildings empty-plantations)
        colonist-ship (:colonist-ship game-state)
        trading-house (:trading-house game-state)
        available-ships (->> (:ships game-state)
                             (filter #(< (:amount %) (:capacity %)))
                             count)
        ;; Check if player actually has any goods to trade/ship
        has-goods? (pos? total-goods)
        can-trade? (and has-goods? (rules/can-trade-any-goods? game-state player))
        can-ship? (and has-goods? (rules/can-ship-goods? game-state player))
        can-produce? (rules/can-produce-goods? game-state player)]

    (case role
      :captain
      (if (not can-ship?)
        -200 ; Heavily penalize captain if no goods can be shipped
        (+ 20 (* total-goods 10))) ; Higher score with more goods

      :trader
      (if (not can-trade?)
        -150 ; Heavily penalize trader if no goods can be traded
        (+ 15 (* total-goods 8)))

      :builder
      (if (< money 2)
        -80 ; Avoid builder if very poor
        (+ 10 (* money 3) ; Score based on money available
           (if (>= (count buildings) 10) 15 0))) ; Bonus if close to 12 buildings

      :mayor
      (if (zero? total-empty-spots)
        -60 ; Don't pick mayor if no empty spots
        (+ 12 (* total-empty-spots 5) ; More empty spots = higher score
           (max 0 (- colonist-ship 2)))) ; Bonus if many colonists available

      :craftsman
      (if (not can-produce?)
        -120 ; Heavily penalize craftsman if can't produce anything
        (let [production-potential (->> plantations
                                        (filter #(pos? (:colonists %)))
                                        count)]
          (+ 8 (* production-potential 6))))

      :settler
      (if (>= (count plantations) 12)
        -90 ; Avoid if island is full
        (+ 5 (if (< (count plantations) 8) 8 2))) ; Bonus early game

      (:prospector :prospector-2)
      (if (< money 5)
        8 ; Good when poor
        2) ; Less valuable when rich

      0)))

(defn select-best-role
  "Select the best role from available options using heuristics"
  [game-state player-id available-roles]
  (when (seq available-roles)
    (let [role-scores (map (fn [role]
                             [role (evaluate-role-utility game-state player-id role)])
                           available-roles)
          sorted-scores (sort-by second > role-scores)
          best-role (first (first sorted-scores))]
      ;; Log decision process (works in both CLJ and CLJS)
      #?(:clj (do
                (println (str "\n=== AI Role Selection (Player " player-id ") ==="))
                (println "Available roles and scores:")
                (doseq [[role score] sorted-scores]
                  (println (str "  " (name role) ": " score)))
                (println (str "Selected: " (name best-role) " (score: " (second (first sorted-scores)) ")"))
                (println "==============================\n"))
         :cljs (do
                 (js/console.log (str "=== AI Role Selection (Player " player-id ") ==="))
                 (js/console.log "Available roles and scores:")
                 (doseq [[role score] sorted-scores]
                   (js/console.log (str "  " (name role) ": " score)))
                 (js/console.log (str "Selected: " (name best-role) " (score: " (second (first sorted-scores)) ")"))))
      best-role)))

;; ================================================================================
;; Plantation Selection Heuristics
;; ================================================================================

(defn evaluate-plantation-value
  "Score a plantation based on its strategic value"
  [game-state player plantation-type]
  (let [buildings (:buildings player)
        has-matching-production (some #(= (get-in state/buildings [(:type %) :good])
                                          plantation-type)
                                      buildings)
        corn-count (count (filter #(= (:type %) :corn) (:plantations player)))
        sugar-count (count (filter #(= (:type %) :sugar) (:plantations player)))
        coffee-count (count (filter #(= (:type %) :coffee) (:plantations player)))
        tobacco-count (count (filter #(= (:type %) :tobacco) (:plantations player)))
        indigo-count (count (filter #(= (:type %) :indigo) (:plantations player)))]
    (case plantation-type
      :quarry 100 ; Always prefer quarries for building discounts
      :corn 80 ; Corn is valuable - no building needed
      :indigo (if has-matching-production
                (- 60 (* indigo-count 10)) ; Good if have building, diminishing returns
                20)
      :sugar (if has-matching-production
               (- 70 (* sugar-count 10))
               25)
      :tobacco (if has-matching-production
                 (- 75 (* tobacco-count 10))
                 15)
      :coffee (if has-matching-production
                (- 65 (* coffee-count 10))
                10)
      0)))

(defn select-best-plantation
  "Choose the best plantation from available options"
  [game-state player available-plantations]
  (when (seq available-plantations)
    (let [plantation-scores (map (fn [plantation]
                                   [plantation (evaluate-plantation-value game-state player plantation)])
                                 available-plantations)
          sorted-scores (sort-by second > plantation-scores)
          best-plantation (first (first sorted-scores))]
      ;; Log decision process
      (println (str "\n=== AI Plantation Selection (Player " (:name player) ") ==="))
      (println "Available plantations and scores:")
      (doseq [[plantation score] sorted-scores]
        (println (str "  " (name plantation) ": " score)))
      (println (str "Selected: " (name best-plantation) " (score: " (second (first sorted-scores)) ")"))
      (println "==============================\n")
      best-plantation)))

;; ================================================================================
;; Building Selection Heuristics
;; ================================================================================

(defn evaluate-building-synergy
  "Evaluate how well a building fits with current strategy"
  [game-state player building-key building-info]
  (let [actual-cost (rules/building-cost game-state player building-key)
        buildings-owned (set (map :type (:buildings player)))
        plantations-owned (set (map :type (:plantations player)))
        game-round (or (:round game-state) 1)
        ;; Kinds of goods the player has production buildings for
        production-kinds (count (filter #(get-in state/buildings [% :good]) buildings-owned))]
    (if (not (rules/can-build-building? game-state player building-key))
      -1000 ; Can't afford it (or no supply/space/duplicate)
      (+ (case building-key
           ;; Production buildings - fixed names to match actual game
           :small-indigo-maker (if (plantations-owned :indigo) 50 0)
           :small-sugar-maker (if (plantations-owned :sugar) 55 0)
           :large-indigo-maker (if (and (plantations-owned :indigo)
                                        (>= (count (filter #(= (:type %) :indigo)
                                                           (:plantations player))) 2))
                                 60 0)
           :large-sugar-maker (if (and (plantations-owned :sugar)
                                       (>= (count (filter #(= (:type %) :sugar)
                                                          (:plantations player))) 2))
                                65 0)
           :tobacco-maker (if (plantations-owned :tobacco) 70 0)
           :coffee-maker (if (plantations-owned :coffee) 75 0)

           ;; Small economic buildings (early game)
           :small-market (if (< game-round 6) 45 20)
           :hacienda (if (< (count (:plantations player)) 8) 40 10)
           :construction-hut (if (< (count (:buildings player)) 6) 35 10)
           :small-warehouse 30

           ;; Large economic buildings (mid-late game)
           :hospice (if (> game-round 4) 60 20)
           :office 50
           :large-market (if (buildings-owned :small-market) 55 30)
           :large-warehouse 35
           :factory (if (>= production-kinds 3) 80 20)
           :university 40
           :harbor (if (> game-round 6) 50 20)
           :wharf (if (> game-round 7) 60 25)

           ;; Victory point buildings (late game)
           :guild-hall (if (>= game-round 9) 90 30)
           :residence (if (>= game-round 9) 85 25)
           :fortress (if (>= game-round 9) 80 20)
           :customs-house (if (>= game-round 9) 75 15)
           :city-hall (if (>= game-round 9) 70 10)

           0)
         ;; Cost-effectiveness bonus: cheaper (after discounts) is better
         (/ 50 (inc actual-cost))))))

(defn select-best-building
  "Choose the best building to construct"
  [game-state player available-buildings]
  (when (seq available-buildings)
    (let [building-scores (map (fn [building-key]
                                 (let [building-info (get state/buildings building-key)]
                                   [building-key (evaluate-building-synergy game-state player
                                                                            building-key building-info)]))
                               available-buildings)
          sorted-scores (sort-by second > building-scores)
          best-building (first (first sorted-scores))]
      ;; Log decision process
      (println (str "\n=== AI Building Selection (Player " (:name player) ") ==="))
      (println "Available buildings and scores:")
      (doseq [[building score] sorted-scores]
        (println (str "  " (name building) ": " score)))
      (println (str "Selected: " (if best-building (name best-building) "none")
                    " (score: " (second (first sorted-scores)) ")"))
      (println "==============================\n")
      best-building)))

;; ================================================================================
;; Trading Heuristics
;; ================================================================================

(defn evaluate-trade-value
  "Score a good for trading based on its value and scarcity"
  [game-state player good-type]
  (let [base-value (get rules/good-values good-type 0)
        good-count (get-in player [:goods good-type] 0)
        has-small-market? (rules/has-occupied-building? player :small-market)
        has-large-market? (rules/has-occupied-building? player :large-market)
        bonus-money (+ (if has-small-market? 1 0)
                       (if has-large-market? 2 0))]
    (if (not (rules/can-trade-good? game-state player good-type))
      -100 ; Can't trade this good
      (+ (* base-value 20)
         (* bonus-money 10)
         (if (> good-count 3) 10 0))))) ; Bonus for excess goods

(defn select-best-trade
  "Choose the best good to trade"
  [game-state player available-goods]
  (when (seq available-goods)
    (->> available-goods
         (map (fn [good]
                [good (evaluate-trade-value game-state player good)]))
         (filter #(>= (second %) 0)) ; Only consider valid trades
         (sort-by second >)
         first
         first)))

;; ================================================================================
;; Shipping Heuristics
;; ================================================================================

(defn evaluate-shipping-option
  "Score a shipping choice by the VP it actually earns (goods loadable on the
   ship the rules force this good onto)"
  [game-state player good-type]
  (let [good-count (get-in player [:goods good-type] 0)
        has-harbor? (rules/has-occupied-building? player :harbor)
        ship-entry (when (pos? good-count)
                     (rules/find-ship-for-good (:ships game-state) good-type good-count))]
    (if (nil? ship-entry)
      -100 ; Can't ship this good
      (let [[_ ship] ship-entry
            loadable (min good-count (- (:capacity ship) (:amount ship)))]
        (+ (* loadable 10)
           (if has-harbor? 5 0)
           ;; Slight preference for higher value goods to clear storage
           (get rules/good-values good-type 0))))))

(defn select-best-shipping
  "Choose the best good to ship for captain phase"
  [game-state player available-goods]
  (when (seq available-goods)
    (->> available-goods
         (map (fn [good]
                [good (evaluate-shipping-option game-state player good)]))
         (filter #(>= (second %) 0)) ; Only consider valid shipping
         (sort-by second >)
         first
         first)))

;; ================================================================================
;; Main AI Decision Function
;; ================================================================================

(defn get-heuristic-move
  "Generate the best move using heuristics for the current game state.
   Returns a role keyword (role selection), a choice keyword, a captain args
   vector like [:corn :wharf], :execute for no-choice roles, or nil to pass."
  [game-state player-id]
  (case (:phase game-state)
    :role-selection
    (let [available-roles (:available-roles game-state)]
      (select-best-role game-state player-id available-roles))

    :role-execution
    (let [role (:selected-role game-state)
          player (state/player-by-id game-state player-id)
          player-idx (state/player-index game-state player-id)]
      (case role
        :settler
        (when-not (rules/island-full? player)
          (let [available-plantations (concat (distinct (:face-up-plantations game-state))
                                              (when (and (pos? (:quarry-supply game-state))
                                                         (rules/may-take-quarry? game-state player-idx))
                                                [:quarry]))]
            (select-best-plantation game-state player available-plantations)))

        :builder
        (let [available-buildings (filter #(rules/can-build-building? game-state player %)
                                          (keys state/buildings))]
          (select-best-building game-state player available-buildings))

        :trader
        (let [tradeable-goods (filter #(rules/can-trade-good? game-state player %)
                                      (keys (:goods-supply game-state)))]
          (select-best-trade game-state player tradeable-goods))

        :captain
        (let [shippable-goods (filter #(and (pos? (get-in player [:goods %] 0))
                                            (rules/find-ship-for-good (:ships game-state) %
                                                                      (get-in player [:goods %] 0)))
                                      (keys (:goods-supply game-state)))]
          (if (seq shippable-goods)
            (select-best-shipping game-state player shippable-goods)
            ;; No cargo ship can take anything - use the wharf if we have one
            (when (rules/can-use-wharf? game-state player-idx)
              (let [best-good (->> (:goods player)
                                   (filter #(pos? (second %)))
                                   (sort-by second)
                                   last
                                   first)]
                [best-good :wharf]))))

        ;; For roles with no choices
        :execute))

    nil))

(defn ai-select-move
  "Main entry point for heuristic AI - returns a move in the expected format.
   Always returns a move during role execution (a pass move when there is no
   legal choice) so the game keeps advancing."
  [game-state player-id & [difficulty]]
  (let [move (get-heuristic-move game-state player-id)]
    ;; Convert move to the format expected by the game engine
    (case (:phase game-state)
      :role-selection
      (when move
        {:type :select-role :role move :player-id player-id})

      :role-execution
      {:type :role-action
       :role (:selected-role game-state)
       :player-id player-id
       :args (cond
               (nil? move) []
               (= move :execute) []
               (vector? move) move
               :else [move])}

      nil)))
