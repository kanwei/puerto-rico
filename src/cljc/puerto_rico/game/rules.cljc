(ns puerto-rico.game.rules
  "Puerto Rico game rules implementation"
  (:require [puerto-rico.game.state :as state]))

;; Forward declarations
(declare end-role-execution execute-role has-occupied-building?)

;; Role execution functions

(defn execute-settler [game-state player-id plantation-choice]
  "Execute the settler role - player gets to take a plantation from face-up tiles or a quarry
   plantation-choice can be:
   - A plantation type from face-up tiles
   - :quarry
   - :random-from-deck (for hacienda bonus draw)"
  (let [player-idx (->> (:players game-state)
                        (map-indexed vector)
                        (filter #(= (:id (second %)) player-id))
                        first
                        first)
        player (get-in game-state [:players player-idx])
        face-up-plantations (:face-up-plantations game-state)
        quarry-supply (:quarry-supply game-state)
        plantation-deck (:plantation-supply game-state)
        ;; Check if player has occupied buildings
        has-hospice (has-occupied-building? player :hospice)
        has-hacienda (has-occupied-building? player :hacienda)
        has-construction-hut (has-occupied-building? player :construction-hut)]
    (cond
      ;; Invalid parameters
      (not (and plantation-choice player-idx
                (>= player-idx 0)
                (< player-idx (count (:players game-state)))))
      game-state

      ;; Drawing random plantation from deck (hacienda bonus)
      (= plantation-choice :random-from-deck)
      (if (and has-hacienda (seq plantation-deck))
        (let [drawn-plantation (first plantation-deck)
              ;; Add plantation to player
              game-after-draw (-> game-state
                                  (update-in [:players player-idx :plantations]
                                             conj {:type drawn-plantation :colonists 0})
                                  (update :plantation-supply rest)
                                  (update :plantation-supply vec))
              ;; Apply hospice bonus if applicable
              final-game (if has-hospice
                           (let [colonist-supply (:colonist-supply game-after-draw)
                                 colonist-ship (:colonist-ship game-after-draw)
                                 plantation-count (count (get-in game-after-draw [:players player-idx :plantations]))
                                 new-plantation-idx (dec plantation-count)]
                             (cond
                               (pos? colonist-supply)
                               (-> game-after-draw
                                   (update-in [:players player-idx :plantations new-plantation-idx :colonists] inc)
                                   (update :colonist-supply dec))
                               (pos? colonist-ship)
                               (-> game-after-draw
                                   (update-in [:players player-idx :plantations new-plantation-idx :colonists] inc)
                                   (update :colonist-ship dec))
                               :else game-after-draw))
                           game-after-draw)]
          final-game)
        game-state)

      ;; Choosing a quarry
      (= plantation-choice :quarry)
      (if (pos? quarry-supply)
        (let [;; Add quarry to player's plantations
              game-after-quarry (-> game-state
                                    (update-in [:players player-idx :plantations]
                                               conj {:type :quarry :colonists 0})
                                    (update :quarry-supply dec))
              ;; Apply hospice bonus if applicable
              final-game (if has-hospice
                           (let [colonist-supply (:colonist-supply game-after-quarry)
                                 colonist-ship (:colonist-ship game-after-quarry)
                                 plantation-count (count (get-in game-after-quarry [:players player-idx :plantations]))
                                 new-plantation-idx (dec plantation-count)]
                             (cond
                               ;; Take colonist from supply
                               (pos? colonist-supply)
                               (-> game-after-quarry
                                   (update-in [:players player-idx :plantations new-plantation-idx :colonists] inc)
                                   (update :colonist-supply dec))
                               ;; Take colonist from ship
                               (pos? colonist-ship)
                               (-> game-after-quarry
                                   (update-in [:players player-idx :plantations new-plantation-idx :colonists] inc)
                                   (update :colonist-ship dec))
                               ;; No colonists available
                               :else game-after-quarry))
                           game-after-quarry)]
          final-game)
        game-state)

      ;; Choosing from face-up plantations
      (some #(= % plantation-choice) face-up-plantations)
      (let [;; Remove the chosen plantation WITHOUT drawing replacement yet
            idx (.indexOf face-up-plantations plantation-choice)
            updated-face-up (vec (concat (subvec face-up-plantations 0 idx)
                                         (subvec face-up-plantations (inc idx))))
            ;; Update player's plantations
            updated-players (assoc-in (:players game-state)
                                      [player-idx :plantations]
                                      (conj (get-in (:players game-state) [player-idx :plantations])
                                            {:type plantation-choice :colonists 0}))
            ;; Basic game state after taking plantation (no replenishment yet)
            game-after-plantation (assoc game-state
                                         :face-up-plantations updated-face-up
                                         :players updated-players)
            ;; Apply hospice bonus if applicable
            final-game (if has-hospice
                         (let [colonist-supply (:colonist-supply game-after-plantation)
                               colonist-ship (:colonist-ship game-after-plantation)
                               plantation-count (count (get-in game-after-plantation [:players player-idx :plantations]))
                               new-plantation-idx (dec plantation-count)]
                           (cond
                             ;; Take colonist from supply
                             (pos? colonist-supply)
                             (-> game-after-plantation
                                 (update-in [:players player-idx :plantations new-plantation-idx :colonists] inc)
                                 (update :colonist-supply dec))
                             ;; Take colonist from ship
                             (pos? colonist-ship)
                             (-> game-after-plantation
                                 (update-in [:players player-idx :plantations new-plantation-idx :colonists] inc)
                                 (update :colonist-ship dec))
                             ;; No colonists available
                             :else game-after-plantation))
                         game-after-plantation)]
        final-game)

      ;; Invalid choice
      :else
      game-state)))

(defn replenish-plantations [game-state]
  "Replenish face-up plantations after all players have taken their turn in Settler phase.
   Discard any remaining face-up tiles and draw new ones (one more than number of players)."
  (let [num-players (count (:players game-state))
        target-count (inc num-players)
        current-face-up (:face-up-plantations game-state)
        plantation-deck (:plantation-supply game-state)
        ;; Discard any remaining face-up plantations to discard pile
        new-discard (vec (concat (or (:plantation-discard game-state) []) current-face-up))
        ;; Draw new plantations from deck
        draw-count (min target-count (count plantation-deck))
        new-face-up (vec (take draw-count plantation-deck))
        new-deck (vec (drop draw-count plantation-deck))
        ;; If deck is empty but we need more, shuffle discard pile
        final-state (if (and (< (count new-face-up) target-count)
                             (seq new-discard))
                      (let [shuffled-discard (shuffle new-discard)
                            additional-needed (- target-count (count new-face-up))
                            additional-draw (min additional-needed (count shuffled-discard))
                            additional-tiles (take additional-draw shuffled-discard)
                            remaining-discard (vec (drop additional-draw shuffled-discard))]
                        (-> game-state
                            (assoc :face-up-plantations (vec (concat new-face-up additional-tiles)))
                            (assoc :plantation-supply new-deck)
                            (assoc :plantation-discard remaining-discard)))
                      (-> game-state
                          (assoc :face-up-plantations new-face-up)
                          (assoc :plantation-supply new-deck)
                          (assoc :plantation-discard new-discard)))]
    final-state))

(defn get-tile-capacity [tile]
  "Get the maximum number of colonists a tile can hold"
  (if (contains? tile :type)
    ;; For buildings, look up capacity from building info
    (if-let [building-info (get state/buildings (:type tile))]
      (get building-info :worker 1) ;; Default to 1 if no worker count specified
      1) ;; Default capacity
    1)) ;; Plantations have 1 circle

(defn get-empty-spaces [tile]
  "Get number of empty spaces on a tile"
  (- (get-tile-capacity tile) (:colonists tile 0)))

(defn smart-place-colonists [player]
  "Intelligently place colonists to maximize production and efficiency"
  (let [colonists-to-place (+ (:colonists-in-hand player) (:san-juan-colonists player))

        ;; Get all plantation and building info with spaces
        plantations (map-indexed
                     (fn [idx p] {:idx idx :type :plantation :tile p
                                  :good-type (:type p) :empty (get-empty-spaces p)})
                     (:plantations player))
        buildings (map-indexed
                   (fn [idx b] {:idx idx :type :building :tile b
                                :good-type (get-in state/buildings [(:type b) :good])
                                :empty (get-empty-spaces b)})
                   (:buildings player))

        ;; Find production chains (plantation + building for same good)
        production-chains (for [p plantations
                                b buildings
                                :when (and (:good-type p) (:good-type b)
                                           (= (:good-type p) (:good-type b))
                                           (> (:empty p) 0) (> (:empty b) 0))]
                            {:plantation p :building b :good (:good-type p)})

;; Priority scoring function
        score-placement (fn [item]
                          (cond
                            ;; Highest priority: Complete production chains
                            (some (fn [chain] (or (= item (:plantation chain))
                                                  (= item (:building chain))))
                                  production-chains) 100
                            ;; Very high priority: Utility buildings (warehouses, hacienda, etc)
                            (and (= (:type item) :building)
                                 (contains? #{:small-warehouse :large-warehouse :hacienda
                                              :construction-hut :hospice :small-market :large-market
                                              :office :factory :university :harbor :wharf}
                                            (get-in item [:tile :type]))) 90
                            ;; High priority: Quarries (building discounts)  
                            (and (= (:type item) :plantation)
                                 (= (:good-type item) :quarry)) 80
                            ;; Medium priority: Production buildings without matching plantations
                            (and (= (:type item) :building) (:good-type item)) 50
                            ;; Lower priority: Plantations without matching buildings
                            (and (= (:type item) :plantation) (:good-type item)) 30
                            ;; Lowest: Large buildings (usually only 1 worker slot)
                            (and (= (:type item) :building)
                                 (contains? #{:guild-hall :residence :customs-house :city-hall :fortress}
                                            (get-in item [:tile :type]))) 10
                            ;; Default
                            :else 20))

        ;; Get all placeable items and sort by strategic value
        all-items (concat plantations buildings)
        items-with-spaces (filter #(> (:empty %) 0) all-items)
        sorted-items (sort-by score-placement > items-with-spaces)

        ;; Place colonists strategically
        [updated-player remaining-colonists]
        (reduce (fn [[player remaining] item]
                  (if (<= remaining 0)
                    [player remaining]
                    (let [spaces-to-fill (min remaining (:empty item))
                          tile-path (if (= (:type item) :plantation)
                                      [:plantations (:idx item)]
                                      [:buildings (:idx item)])]
                      [(update-in player (conj tile-path :colonists) + spaces-to-fill)
                       (- remaining spaces-to-fill)])))
                [player colonists-to-place]
                sorted-items)]

    (-> updated-player
        (assoc :colonists-in-hand 0)
        (assoc :san-juan-colonists remaining-colonists))))

(defn execute-mayor [game-state]
  "Execute the mayor role - distribute colonists according to Puerto Rico rules"
  (let [role-selector-idx (:role-selector-idx game-state)
        num-players (count (:players game-state))

        ;; Step 1: Role selector gets privilege (1 colonist from supply, not ship)
        game-after-privilege (if (and role-selector-idx (> (:colonist-supply game-state) 0))
                               (-> game-state
                                   (update-in [:players role-selector-idx :colonists-in-hand] inc)
                                   (update :colonist-supply dec))
                               game-state)

        ;; Step 2: Distribute ALL colonists from ship to players (round-robin from governor)
        colonists-on-ship (:colonist-ship game-after-privilege)
        game-after-ship (if (> colonists-on-ship 0)
                          (loop [game game-after-privilege
                                 remaining-colonists colonists-on-ship
                                 current-player-idx (:governor-idx game-after-privilege)]
                            (if (<= remaining-colonists 0)
                              (assoc game :colonist-ship 0)
                              (let [next-player-idx (mod (inc current-player-idx) num-players)]
                                (recur
                                 (update-in game [:players current-player-idx :colonists-in-hand] inc)
                                 (dec remaining-colonists)
                                 next-player-idx))))
                          game-after-privilege)

;; Step 3: Smart-place colonists for ALL players
        game-after-placement (update game-after-ship :players
                                     (fn [players]
                                       (mapv smart-place-colonists players)))

        ;; Step 4: Refill colonist ship based on empty building circles across ALL players
        total-empty-building-circles (reduce +
                                             (map (fn [player]
                                                    (reduce +
                                                            (map (fn [building]
                                                                   (max 0 (- (get-tile-capacity building)
                                                                             (:colonists building))))
                                                                 (:buildings player))))
                                                  (:players game-after-placement)))

        ;; Mayor should place at least as many colonists as there are players
        colonists-to-add (max num-players total-empty-building-circles)
        colonists-available (min colonists-to-add (:colonist-supply game-after-placement))

        final-game (-> game-after-placement
                       (update :colonist-ship + colonists-available)
                       (update :colonist-supply - colonists-available))]

    (println "Mayor executed (single action for entire table):")
    (println "  Role selector privilege colonist:" (if (and role-selector-idx (> (:colonist-supply game-state) 0)) "given" "none available"))
    (println "  Colonists distributed from ship:" colonists-on-ship)
    (println "  All players auto-placed colonists")
    (println "  Empty building circles across all players:" total-empty-building-circles)
    (println "  New colonists added to ship:" colonists-available)
    final-game))

(defn can-build-building? [player building-key building-info]
  "Check if player can afford to build a building"
  (and (>= (:money player) (:cost building-info))
       ;; Check if player already has this building type
       (not (some #(= (:type %) building-key) (:buildings player)))
       (< (count (:buildings player)) 12)))

(defn execute-builder [game-state player-id building-choice]
  "Execute the builder role - player builds a building"
  (let [player-idx (->> (:players game-state)
                        (map-indexed vector)
                        (filter #(= (:id (second %)) player-id))
                        first
                        first)
        player (get-in game-state [:players player-idx])
        building-info (get state/buildings building-choice)]
    (if (and building-choice
             building-info
             (can-build-building? player building-choice building-info))
      (-> game-state
          ;; Add building as a map with colonist tracking
          (update-in [:players player-idx :buildings] conj {:type building-choice :colonists 0})
          (update-in [:players player-idx :money] - (:cost building-info)))
      game-state)))

(defn execute-craftsman [game-state]
  "Execute the craftsman role - players produce goods in turn order"
  (let [role-selector-idx (:role-selector-idx game-state)
        num-players (count (:players game-state))
        ;; Create turn order starting with role selector
        turn-order (concat (range role-selector-idx num-players)
                           (range 0 role-selector-idx))

        ;; Function to produce goods for a single player
        produce-goods-for-player (fn [current-game-state player-idx is-role-selector?]
                                   (let [player (get-in current-game-state [:players player-idx])
                                         current-supply (:goods-supply current-game-state)

                                         ;; Get occupied plantations (have at least 1 colonist)
                                         occupied-plantations (filter #(and (:colonists %) (> (:colonists %) 0)) (:plantations player))

                                         ;; Get occupied production buildings
                                         occupied-production-buildings (filter #(and (:colonists %) (> (:colonists %) 0)
                                                                                     ;; Check if building is a production type
                                                                                     (let [building-info (get state/buildings (:type %))]
                                                                                       (= (:type building-info) :production))) (:buildings player))

;; Corn is special - it produces without a building
                                         corn-plantations (filter #(= (:type %) :corn) occupied-plantations)
                                         corn-production (reduce + (map :colonists corn-plantations))

                                         ;; Other goods need both plantation and production building
                                         other-goods-production
                                         (reduce (fn [acc building]
                                                   (let [building-info (get state/buildings (:type building))
                                                         good-type (:good building-info)
                                                         matching-plantations (filter #(= (:type %) good-type) occupied-plantations)]
                                                     (if (seq matching-plantations)
                                                       ;; Can produce this good (have both plantation and building)
                                                       (assoc acc good-type (min (count matching-plantations)
                                                                                 (:colonists building)))
                                                       acc)))
                                                 {} occupied-production-buildings)

                                         ;; Combine all production
                                         total-production (assoc other-goods-production :corn corn-production)

                                         ;; Limit production by current goods supply availability
                                         limited-production (reduce (fn [acc [good-type amount]]
                                                                      (if (> amount 0)
                                                                        (let [available-supply (get current-supply good-type 0)
                                                                              actual-amount (min amount available-supply)]
                                                                          (if (> actual-amount 0)
                                                                            (assoc acc good-type actual-amount)
                                                                            acc))
                                                                        acc))
                                                                    {} total-production)

;; Add role selector privilege: +1 of first good produced
                                         privilege-production (if (and is-role-selector? (seq limited-production))
                                                                (let [privilege-good (first (keys limited-production))
                                                                      available-supply (get current-supply privilege-good 0)
                                                                      current-production (get limited-production privilege-good 0)]
                                                                  ;; Only add privilege if there's at least 1 more good available in supply
                                                                  (if (>= available-supply (inc current-production))
                                                                    (update limited-production privilege-good inc)
                                                                    limited-production))
                                                                limited-production)

                                         ;; Calculate factory bonus if player has occupied factory
                                         has-factory (has-occupied-building? player :factory)
                                         goods-types-produced (count (filter #(> (second %) 0) privilege-production))
                                         factory-bonus (if (and has-factory (> goods-types-produced 1))
                                                         (case goods-types-produced
                                                           2 1 ; 2 kinds = 1 doubloon
                                                           3 2 ; 3 kinds = 2 doubloons
                                                           4 3 ; 4 kinds = 3 doubloons
                                                           5 5 ; 5 kinds = 5 doubloons
                                                           0) ; 1 or 0 kinds = no bonus
                                                         0)

                                         ;; Update player's goods
                                         updated-goods (reduce (fn [goods [good-type amount]]
                                                                 (update goods good-type + amount))
                                                               (:goods player) privilege-production)

                                         ;; Update player with new goods and factory bonus
                                         updated-player (-> player
                                                            (assoc :goods updated-goods)
                                                            (update :money + factory-bonus))

                                         ;; Update goods supply by removing produced goods
                                         updated-supply (reduce (fn [supply [good-type amount]]
                                                                  (update supply good-type - amount))
                                                                current-supply privilege-production)]

                                     (println "Player" (:name player) "produced:" privilege-production)
                                     (when (> factory-bonus 0)
                                       (println "  Factory bonus:" factory-bonus "doubloons for" goods-types-produced "kinds of goods"))

                                     ;; Return updated game state
                                     (-> current-game-state
                                         (assoc-in [:players player-idx] updated-player)
                                         (assoc :goods-supply updated-supply))))

        ;; Process each player in turn order
        final-game-state (reduce (fn [current-game player-idx]
                                   (let [is-role-selector? (= player-idx role-selector-idx)]
                                     (produce-goods-for-player current-game player-idx is-role-selector?)))
                                 game-state
                                 turn-order)]

    (println "Craftsman executed (players produced in turn order)")
    (println "Final goods supply:" (:goods-supply final-game-state))
    final-game-state))

(defn can-trade-good? [game-state player good]
  "Check if player can trade a specific good"
  (and (pos? (get-in player [:goods good] 0))
       (not (contains? (set (map :good (:trading-house game-state))) good))
       (< (count (:trading-house game-state)) 4)))

(defn has-occupied-building? [player building-type]
  "Check if player has an occupied building of the given type"
  (some #(and (= (:type %) building-type) (pos? (:colonists %)))
        (:buildings player)))

(defn clear-full-trading-house [game-state]
  "Clear trading house if it contains 4 different goods, returning goods to supply"
  (let [trading-house (:trading-house game-state)
        unique-goods (set (map :good trading-house))]
    (if (>= (count unique-goods) 4)
      ;; Trading house is full - return goods to supply and clear it
      (let [trading-house-goods (frequencies (map :good trading-house))]
        (-> game-state
            (update :goods-supply (fn [goods-supply]
                                    (merge-with + goods-supply trading-house-goods)))
            (assoc :trading-house [])))
      ;; Trading house not full - no change
      game-state)))

(defn execute-trader [game-state player-id good-choice]
  "Execute the trader role - sell goods to trading house"
  (let [player-idx (->> (:players game-state)
                        (map-indexed vector)
                        (filter #(= (:id (second %)) player-id))
                        first
                        first)
        player (get-in game-state [:players player-idx])
        role-selector-idx (:role-selector-idx game-state)
        is-role-selector? (= player-idx role-selector-idx)
        base-trade-value (case good-choice
                           :corn 0
                           :indigo 1
                           :sugar 2
                           :tobacco 3
                           :coffee 4
                           0)
        ;; Check for market building bonuses
        market-bonus (cond
                       (has-occupied-building? player :large-market) 2
                       (has-occupied-building? player :small-market) 1
                       :else 0)
        ;; Role selector gets +1 privilege bonus
        privilege-bonus (if is-role-selector? 1 0)
        total-value (+ base-trade-value market-bonus privilege-bonus)]
    (if (and good-choice (can-trade-good? game-state player good-choice))
      (let [game-after-trade (-> game-state
                                 (update-in [:players player-idx :goods good-choice] dec)
                                 (update-in [:players player-idx :money] + total-value)
                                 (update :trading-house conj {:good good-choice :player-id player-id}))]
        ;; Check if trading house is now full and clear it if so
        (clear-full-trading-house game-after-trade))
      game-state)))

(defn award-victory-points
  "Award victory points to a player, limited by available supply.
   Returns updated game state and actual VPs awarded."
  [game-state player-idx vps-requested]
  (let [vps-available (:victory-point-supply game-state)
        vps-to-award (min vps-requested vps-available)]
    [(-> game-state
         (update-in [:players player-idx :victory-points] + vps-to-award)
         (update :victory-point-supply - vps-to-award))
     vps-to-award]))

(defn find-ship-for-good [ships good amount]
  "Find appropriate ship for shipping goods according to Puerto Rico rules:
   1. If any ship already contains the same good type, you MUST use it (regardless of capacity)
   2. Only if no partially filled ships contain this good type can you use an empty ship
   3. You cannot choose between multiple valid ships - the rules determine which ship to use"
  (let [;; Separate ships into partially filled and empty
        partially-filled-ships (->> ships
                                    (map-indexed vector)
                                    (filter (fn [[idx ship]]
                                              (and (:good ship) (pos? (:amount ship))))))
        empty-ships (->> ships
                         (map-indexed vector)
                         (filter (fn [[idx ship]]
                                   (nil? (:good ship)))))

        ;; Check if any partially filled ship contains this good type
        ;; NOTE: We don't check capacity here - you MUST use existing ship regardless
        compatible-filled-ship (->> partially-filled-ships
                                    (filter (fn [[idx ship]]
                                              (= (:good ship) good)))
                                    (first))

        ;; Find the smallest empty ship that can hold the goods
        compatible-empty-ship (->> empty-ships
                                   (filter (fn [[idx ship]]
                                             (>= (:capacity ship) amount)))
                                   (sort-by (fn [[idx ship]] (:capacity ship)))
                                   (first))]

    ;; Puerto Rico rule: MUST use partially filled ship if available for this good type
    (cond
      ;; Rule 1: If there's a partially filled ship with the same good type, MUST use it
      compatible-filled-ship compatible-filled-ship

      ;; Rule 2: Only if no partially filled ship has this good type, can use empty ship
      (and (nil? compatible-filled-ship) compatible-empty-ship)
      compatible-empty-ship

      ;; Rule 3: Cannot ship if no valid ship exists
      :else nil)))

(defn return-full-ships-to-supply [game-state]
  "Check all ships and return goods from full ships to supply, then reset those ships"
  (let [ships (:ships game-state)]
    (reduce (fn [game ship-idx]
              (let [ship (get ships ship-idx)]
                (if (= (:amount ship) (:capacity ship))
                  ;; Ship is full - return goods to supply and reset ship
                  (-> game
                      (update-in [:goods-supply (:good ship)] + (:amount ship))
                      (assoc-in [:ships ship-idx :good] nil)
                      (assoc-in [:ships ship-idx :amount] 0))
                  ;; Ship not full - no change
                  game)))
            game-state
            (range (count ships)))))

(defn can-ship-goods? [game-state player]
  "Check if player has any goods that can actually be shipped"
  (let [goods (:goods player)
        ships (:ships game-state)]
    (some (fn [[good-type amount]]
            (and (pos? amount)
                 (find-ship-for-good ships good-type amount)))
          goods)))

(defn can-trade-any-goods? [game-state player]
  "Check if player has any goods that can be traded"
  (let [goods (:goods player)]
    (some (fn [[good-type amount]]
            (and (pos? amount)
                 (can-trade-good? game-state player good-type)))
          goods)))

(defn can-produce-goods? [game-state player]
  "Check if player can actually produce any goods"
  (let [plantations (:plantations player)
        buildings (:buildings player)
        occupied-plantations (filter #(pos? (:colonists %)) plantations)
        occupied-production-buildings (filter #(and (pos? (:colonists %))
                                                    (let [building-info (get state/buildings (:type %))]
                                                      (= (:type building-info) :production))) buildings)]
    (or
      ;; Can produce corn (just need occupied corn plantations)
     (some #(= (:type %) :corn) occupied-plantations)
      ;; Can produce other goods (need both plantation and production building)
     (some (fn [building]
             (let [building-info (get state/buildings (:type building))
                   good-type (:good building-info)]
               (some #(= (:type %) good-type) occupied-plantations)))
           occupied-production-buildings))))

(defn execute-captain [game-state player-id good-choice]
  "Execute the captain role - ship goods"
  (let [player-idx (->> (:players game-state)
                        (map-indexed vector)
                        (filter #(= (:id (second %)) player-id))
                        first
                        first)
        player (get-in game-state [:players player-idx])
        amount-to-ship (get-in player [:goods good-choice] 0)
        ship-choice (find-ship-for-good (:ships game-state) good-choice amount-to-ship)]
    (if (and good-choice (pos? amount-to-ship) ship-choice)
      (let [[ship-idx ship] ship-choice
            actual-amount (min amount-to-ship (- (:capacity ship) (:amount ship)))
            ;; Award base VPs using helper function that respects supply
            [game-after-base-vps vps-awarded] (award-victory-points game-state player-idx actual-amount)
            ;; Check for harbor bonus: +1 VP per loading action (not per barrel)
            has-harbor (has-occupied-building? player :harbor)
            harbor-bonus (if has-harbor 1 0)
            ;; Award harbor bonus VP if applicable
            [game-after-harbor harbor-vps-awarded] (if has-harbor
                                                     (award-victory-points game-after-base-vps player-idx harbor-bonus)
                                                     [game-after-base-vps 0])
            ;; Check if this player is the Captain (role selector) and award bonus
            is-captain (= player-idx (:role-selector-idx game-state))
            captain-bonus (if (and is-captain
                                   (not (get-in game-state [:captain-bonus-awarded] false)))
                            1
                            0)
            [game-with-captain-bonus captain-vps-awarded] (if (pos? captain-bonus)
                                                            (award-victory-points game-after-harbor player-idx captain-bonus)
                                                            [game-after-harbor 0])
            ;; Mark that captain bonus has been awarded if applicable
            final-game (if (pos? captain-bonus)
                         (assoc game-with-captain-bonus :captain-bonus-awarded true)
                         game-with-captain-bonus)]
        (-> final-game
            (update-in [:players player-idx :goods good-choice] - actual-amount)
            (assoc-in [:ships ship-idx :good] good-choice)
            (update-in [:ships ship-idx :amount] + actual-amount)))
      game-state)))

;; Role selection and execution
(defn select-role [game-state player-id role]
  "Player selects a role - all players will then execute this role in turn order"
  (if (contains? (:available-roles game-state) role)
    (let [selector-idx (->> (:players game-state)
                            (map-indexed vector)
                            (filter #(= (:id (second %)) player-id))
                            first
                            first)
          ;; Give player any gold coins on the selected role
          gold-on-role (get-in game-state [:role-gold role] 0)
          game-with-gold (if (> gold-on-role 0)
                           (-> game-state
                               (update-in [:players selector-idx :money] + gold-on-role)
                               (assoc-in [:role-gold role] 0))
                           game-state)
          ;; Increment the counter of players who have selected this round
          game-with-counter (update game-with-gold :players-selected-this-round inc)
          ;; All roles now use the same execution flow
          execution-order (state/create-role-execution-order game-state selector-idx)]
      (-> game-with-counter
          (assoc :selected-role role)
          (assoc :role-selector-idx selector-idx)
          (assoc :role-execution-order execution-order)
          (assoc :role-execution-current-idx (first execution-order))
          (assoc :phase :role-execution)
          (update :available-roles disj role)
          (update :used-roles conj role)))
    game-state))

(defn calculate-warehouse-storage [player]
  "Calculate how many goods TYPES a player can store based on warehouses"
  (let [has-small-warehouse (has-occupied-building? player :small-warehouse)
        has-large-warehouse (has-occupied-building? player :large-warehouse)]
    (cond
      ;; Both warehouses: 1 base + 1 small + 2 large = 4 types total
      (and has-small-warehouse has-large-warehouse) 4
      ;; Large warehouse only: 1 base + 2 large = 3 types total  
      has-large-warehouse 3
      ;; Small warehouse only: 1 base + 1 small = 2 types total
      has-small-warehouse 2
      ;; No warehouses: 1 type total (but only 1 good of that type)
      :else 1)))

(defn apply-storage-rules [game-state]
  "Apply storage rules at end of captain phase - players must discard excess goods"
  (let [total-discarded (atom {:corn 0 :indigo 0 :sugar 0 :tobacco 0 :coffee 0})]
    (-> game-state
        (update :players
                (fn [players]
                  (mapv (fn [player]
                          (let [current-goods (:goods player)
                                goods-with-amounts (filter #(pos? (second %)) current-goods)
                                goods-types-count (count goods-with-amounts)
                                max-storable-types (calculate-warehouse-storage player)
                                has-no-warehouses (and (not (has-occupied-building? player :small-warehouse))
                                                       (not (has-occupied-building? player :large-warehouse)))]
                            (cond
                              ;; Player can store all their goods types
                              (<= goods-types-count max-storable-types)
                              (if (and has-no-warehouses (> goods-types-count 1))
                                ;; Special case: no warehouses but multiple types - keep only 1 good total
                                (let [goods-priority {:coffee 5 :tobacco 4 :sugar 3 :indigo 2 :corn 1}
                                      best-good-type (first (sort-by #(get goods-priority % 0) >
                                                                     (map first goods-with-amounts)))
                                      new-goods (merge {:corn 0 :indigo 0 :sugar 0 :tobacco 0 :coffee 0}
                                                       {best-good-type 1})
                                      discarded (merge-with - current-goods new-goods)]
                                  ;; Track what was discarded
                                  (swap! total-discarded #(merge-with + % discarded))
                                  (assoc player :goods new-goods))
                                ;; With warehouses or only one type: keep all goods of allowed types
                                (if (and has-no-warehouses (= goods-types-count 1))
                                  ;; No warehouses, one type: keep only 1 good of that type
                                  (let [[good-type _] (first goods-with-amounts)
                                        new-goods (merge {:corn 0 :indigo 0 :sugar 0 :tobacco 0 :coffee 0}
                                                         {good-type 1})
                                        discarded (merge-with - current-goods new-goods)]
                                    ;; Track what was discarded
                                    (swap! total-discarded #(merge-with + % discarded))
                                    (assoc player :goods new-goods))
                                  ;; Has warehouses: keep all goods of the types
                                  player))

                              ;; Player has too many types - must choose which types to keep  
                              :else
                              (let [goods-priority {:coffee 5 :tobacco 4 :sugar 3 :indigo 2 :corn 1}
                                    sorted-goods (sort-by #(get goods-priority (first %) 0) > goods-with-amounts)
                                    goods-to-keep (take max-storable-types sorted-goods)
                                    ;; For no warehouses, keep only 1 good of the best type
                                    final-goods (if has-no-warehouses
                                                  (let [[best-type _] (first goods-to-keep)]
                                                    {best-type 1})
                                                  ;; With warehouses, keep all goods of the selected types
                                                  (into {} goods-to-keep))
                                    new-goods (merge {:corn 0 :indigo 0 :sugar 0 :tobacco 0 :coffee 0}
                                                     final-goods)
                                    discarded (merge-with - current-goods new-goods)]
                                ;; Track what was discarded
                                (swap! total-discarded #(merge-with + % discarded))
                                (assoc player :goods new-goods)))))
                        players)))
        ;; Return discarded goods to supply
        (update :goods-supply #(merge-with + % @total-discarded)))))

(defn advance-role-execution [game-state]
  "Move to the next player in role execution order, or end role if all players have executed"
  (let [execution-order (:role-execution-order game-state)
        current-idx (:role-execution-current-idx game-state)
        current-position (.indexOf execution-order current-idx)
        next-position (inc current-position)]
    (if (>= next-position (count execution-order))
      ;; All players have executed the role, back to role selection
      (let [completed-role (:selected-role game-state)
            game-after-role (let [base-game (-> game-state
                                                (assoc :phase :role-selection)
                                                (assoc :selected-role nil)
                                                (assoc :role-selector-idx nil)
                                                (assoc :role-execution-order nil)
                                                (assoc :role-execution-current-idx nil)
                                                (assoc :current-player-idx (state/next-player-idx game-state)))]
                              (cond
                                ;; If Settler role just finished, replenish plantations and clear hacienda flags
                                (= completed-role :settler)
                                (-> base-game
                                    replenish-plantations
                                    (dissoc :hacienda-used))
                                ;; If Captain role just finished, apply storage rules and empty full ships
                                (= completed-role :captain)
                                (-> base-game
                                    (apply-storage-rules)
                                    (return-full-ships-to-supply)
                                    (dissoc :captain-bonus-awarded))
                                :else base-game))
            players-selected (:players-selected-this-round game-after-role)
            num-players (count (:players game-after-role))]
        ;; Check if round should end (each player has selected a role)
        (if (>= players-selected num-players)
          ;; Round is complete - FIRST check for game end conditions before doing anything else
          (if (state/check-victory-conditions game-after-role)
            ;; Game ends - calculate final scores (keep same round number)
            (let [final-players (mapv (fn [player]
                                        (assoc player :final-score (state/calculate-victory-points player)))
                                      (:players game-after-role))
                  winner (apply max-key :final-score final-players)]
              (-> game-after-role
                  (assoc :players final-players)
                  (assoc :game-over true)
                  (assoc :winner winner)
                  (assoc :phase :game-over)))
            ;; Continue to new round only if game is not ending
            (let [unpicked-roles (clojure.set/difference (set state/roles) (:used-roles game-after-role))]
              (-> game-after-role
                  (update :round inc)
                  (assoc :available-roles (set state/roles))
                  (assoc :used-roles #{})
                  ;; Add gold to unpicked roles BEFORE starting new round
                  (update :role-gold (fn [role-gold]
                                       (reduce (fn [rg role]
                                                 (update rg role inc))
                                               role-gold
                                               unpicked-roles)))
                  ;; Rotate governor to next player
                  (assoc :governor-idx (mod (inc (:governor-idx game-after-role)) num-players))
                  (assoc :current-player-idx (mod (inc (:governor-idx game-after-role)) num-players)) ;; Governor goes first
                  (assoc :players-selected-this-round 0)
                  (assoc :phase :role-selection)
                  ;; Clear trading house

;; Reset full ships and return goods to supply
                  (return-full-ships-to-supply))))
          ;; Round continues with next player
          game-after-role))
      ;; Move to next player in execution order
      (assoc game-state :role-execution-current-idx (nth execution-order next-position)))))

(defn execute-role [game-state role player-id & args]
  "Execute the selected role with given arguments"
  (case role
    :settler (execute-settler game-state player-id (first args))
    :mayor (execute-mayor game-state)
    :builder (execute-builder game-state player-id (first args))
    :craftsman (execute-craftsman game-state)
    :trader (execute-trader game-state player-id (first args))
    :captain (execute-captain game-state player-id (first args))
    :prospector (let [executor-idx (:role-execution-current-idx game-state)
                      selector-idx (:role-selector-idx game-state)]
                  ;; Only the role selector gets the 1 gold bonus
                  (if (= executor-idx selector-idx)
                    (update-in game-state [:players executor-idx :money] inc)
                    game-state))
    game-state))

;; Game phase management
(defn end-role-execution [game-state]
  "End the current role execution and return to role selection phase"
  ;; Skip all player turns and end the role immediately
  (let [game-after-role (-> game-state
                            (assoc :phase :role-selection)
                            (assoc :selected-role nil)
                            (assoc :role-selector-idx nil)
                            (assoc :role-execution-order nil)
                            (assoc :role-execution-current-idx nil)
                            (assoc :current-player-idx (state/next-player-idx game-state)))
        players-selected (:players-selected-this-round game-after-role)
        num-players (count (:players game-after-role))]
    ;; Check if round should end (each player has selected a role)
    (if (>= players-selected num-players)
      ;; Round is complete - check for game end conditions FIRST
      (if (state/check-victory-conditions game-after-role)
        ;; Game ends - calculate final scores
        (let [final-players (mapv (fn [player]
                                    (assoc player :final-score (state/calculate-victory-points player)))
                                  (:players game-after-role))
              winner (apply max-key :final-score final-players)]
          (-> game-after-role
              (assoc :players final-players)
              (assoc :game-over true)
              (assoc :winner winner)
              (assoc :phase :game-over)))
        ;; Continue to new round only if game is not ending
        (let [unpicked-roles (clojure.set/difference (set state/roles) (:used-roles game-after-role))]
          (-> game-after-role
              (update :round inc)
              (assoc :available-roles (set state/roles))
              (assoc :used-roles #{})
              ;; Add gold to unpicked roles BEFORE starting new round
              (update :role-gold (fn [role-gold]
                                   (reduce (fn [rg role]
                                             (update rg role inc))
                                           role-gold
                                           unpicked-roles)))
              ;; Rotate governor to next player
              (assoc :governor-idx (mod (inc (:governor-idx game-after-role)) num-players))
              (assoc :current-player-idx (mod (inc (:governor-idx game-after-role)) num-players)) ;; Governor goes first
              (assoc :players-selected-this-round 0)
              (assoc :phase :role-selection)
              ;; Reset full ships and return goods to supply
              (return-full-ships-to-supply))))
      ;; Round continues with next player
      game-after-role)))

; This function is no longer needed - round ending logic moved to advance-role-execution

;; Game validation functions
(defn valid-move? [game-state player-id move]
  "Validate if a move is legal in the current game state"
  (case (:type move)
    :select-role (and (= (:phase game-state) :role-selection)
                      (= (:id (state/current-player game-state)) player-id)
                      (contains? (:available-roles game-state) (:role move)))
    :role-action (and (= (:phase game-state) :role-execution)
                      (= (:selected-role game-state) (:role move)))
    false))

(defn apply-move [game-state move]
  "Apply a validated move to the game state"
  (case (:type move)
    :select-role (select-role game-state (:player-id move) (:role move))
    :role-action (-> (apply execute-role game-state (:role move) (:player-id move) (:args move))
                     (advance-role-execution))
    game-state))
