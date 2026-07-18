(ns puerto-rico.game.rules
  "Puerto Rico game rules implementation"
  (:require [clojure.set :as set]
            [puerto-rico.game.state :as state]))

;; Forward declarations
(declare end-role-execution execute-role has-occupied-building?)

;; Trade value of each good (also used to break ties when auto-choosing goods)
(def good-values {:corn 0 :indigo 1 :sugar 2 :tobacco 3 :coffee 4})

;; Role execution functions

(defn- add-colonist-from-supply-or-ship
  "Take a colonist from the supply (or the ship if the supply is empty) and place
   it on the tile at tile-path. Used by the hospice and university."
  [game-state tile-path]
  (cond
    (pos? (:colonist-supply game-state))
    (-> game-state
        (update-in (conj tile-path :colonists) inc)
        (update :colonist-supply dec))

    (pos? (:colonist-ship game-state))
    (-> game-state
        (update-in (conj tile-path :colonists) inc)
        (update :colonist-ship dec))

    :else game-state))

(defn- place-plantation
  "Give the player a new tile, applying the hospice bonus if requested."
  [game-state player-idx tile-type hospice-applies?]
  (let [game-after (update-in game-state [:players player-idx :plantations]
                              conj {:type tile-type :colonists 0})
        new-idx (dec (count (get-in game-after [:players player-idx :plantations])))]
    (if (and hospice-applies?
             (has-occupied-building? (get-in game-after [:players player-idx]) :hospice))
      (add-colonist-from-supply-or-ship game-after [:players player-idx :plantations new-idx])
      game-after)))

(defn island-full? [player]
  (>= (count (:plantations player)) 12))

(defn may-take-quarry?
  "Only the settler (role selector) may take a quarry, or the owner of an
   occupied construction hut."
  [game-state player-idx]
  (or (= player-idx (:role-selector-idx game-state))
      (has-occupied-building? (get-in game-state [:players player-idx]) :construction-hut)))

(defn execute-settler [game-state player-id plantation-choice]
  "Execute the settler role - player gets to take a plantation from face-up tiles or a quarry
   plantation-choice can be:
   - A plantation type from face-up tiles
   - :quarry (role selector privilege, or occupied construction hut)
   - :random-from-deck (for hacienda bonus draw)"
  (let [player-idx (state/player-index game-state player-id)
        player (when player-idx (get-in game-state [:players player-idx]))
        face-up-plantations (:face-up-plantations game-state)
        quarry-supply (:quarry-supply game-state)
        plantation-deck (:plantation-supply game-state)
        has-hacienda (when player (has-occupied-building? player :hacienda))]
    (cond
      ;; Invalid parameters or island already full (12 spaces)
      (or (nil? plantation-choice) (nil? player-idx) (island-full? player))
      game-state

      ;; Drawing random plantation from deck (hacienda bonus, once per phase).
      ;; Rulebook: the hospice does NOT grant a colonist for this extra tile.
      (= plantation-choice :random-from-deck)
      (if (and has-hacienda
               (seq plantation-deck)
               (not (get-in game-state [:hacienda-used player-idx])))
        (-> game-state
            (place-plantation player-idx (first plantation-deck) false)
            (update :plantation-supply (comp vec rest))
            (assoc-in [:hacienda-used player-idx] true))
        game-state)

      ;; Choosing a quarry (settler privilege or construction hut)
      (= plantation-choice :quarry)
      (if (and (pos? quarry-supply) (may-take-quarry? game-state player-idx))
        (-> game-state
            (place-plantation player-idx :quarry true)
            (update :quarry-supply dec))
        game-state)

      ;; Choosing from face-up plantations
      (some #(= % plantation-choice) face-up-plantations)
      (let [idx (.indexOf face-up-plantations plantation-choice)
            updated-face-up (vec (concat (subvec face-up-plantations 0 idx)
                                         (subvec face-up-plantations (inc idx))))]
        (-> game-state
            (assoc :face-up-plantations updated-face-up)
            (place-plantation player-idx plantation-choice true)))

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

        ;; Step 2: Distribute ALL colonists from ship, one at a time,
        ;; starting with the mayor (role selector) and going clockwise
        colonists-on-ship (:colonist-ship game-after-privilege)
        distribution-start (or role-selector-idx (:governor-idx game-after-privilege) 0)
        game-after-ship (if (> colonists-on-ship 0)
                          (loop [game game-after-privilege
                                 remaining-colonists colonists-on-ship
                                 current-player-idx distribution-start]
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

        ;; Refill with one colonist per empty building circle, minimum = player count
        colonists-to-add (max num-players total-empty-building-circles)
        colonists-available (min colonists-to-add (:colonist-supply game-after-placement))

        final-game (-> game-after-placement
                       (update :colonist-ship + colonists-available)
                       (update :colonist-supply - colonists-available))]

    ;; Rulebook game-end trigger: the ship could not be fully refilled
    (if (< colonists-available colonists-to-add)
      (assoc final-game :colonist-ship-shortfall true)
      final-game)))

(defn building-cost
  "Actual cost of a building for a player: base cost minus quarry discount
   (1 per occupied quarry, capped by the building's column) minus the builder
   privilege (1 for the role selector during the builder phase). Floor is 0."
  [game-state player building-key]
  (let [building-info (get state/buildings building-key)
        occupied-quarries (count (filter #(and (= (:type %) :quarry)
                                               (pos? (:colonists %)))
                                         (:plantations player)))
        quarry-discount (min occupied-quarries (:column building-info))
        player-idx (state/player-index game-state (:id player))
        privilege-discount (if (and (= (:selected-role game-state) :builder)
                                    (some? (:role-selector-idx game-state))
                                    (= player-idx (:role-selector-idx game-state)))
                             1
                             0)]
    (max 0 (- (:cost building-info) quarry-discount privilege-discount))))

(defn can-build-building?
  "Check if the player may build this building: it exists in the supply, they
   don't own one, they have city space (large buildings need 2), and they can
   afford it after quarry/privilege discounts."
  [game-state player building-key]
  (let [building-info (get state/buildings building-key)]
    (and building-info
         (pos? (get-in game-state [:building-supply building-key] 0))
         (not (some #(= (:type %) building-key) (:buildings player)))
         (<= (+ (state/city-slots-used player)
                (if (= (:type building-info) :large) 2 1))
             12)
         (>= (:money player) (building-cost game-state player building-key)))))

(defn execute-builder [game-state player-id building-choice]
  "Execute the builder role - player builds a building"
  (let [player-idx (state/player-index game-state player-id)
        player (when player-idx (get-in game-state [:players player-idx]))]
    (if (and building-choice
             player
             (can-build-building? game-state player building-choice))
      (let [cost (building-cost game-state player building-choice)
            game-after-build (-> game-state
                                 ;; Add building as a map with colonist tracking
                                 (update-in [:players player-idx :buildings] conj {:type building-choice :colonists 0})
                                 (update-in [:players player-idx :money] - cost)
                                 (update-in [:building-supply building-choice] dec))
            new-building-idx (dec (count (get-in game-after-build [:players player-idx :buildings])))]
        ;; University: owner gets a colonist on the newly built building
        (if (has-occupied-building? player :university)
          (add-colonist-from-supply-or-ship game-after-build
                                            [:players player-idx :buildings new-building-idx])
          game-after-build))
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

                                         ;; Other goods: production = min(occupied matching plantations,
                                         ;; total occupied circles across ALL buildings producing that good)
                                         building-capacity (reduce (fn [acc building]
                                                                     (let [good-type (get-in state/buildings [(:type building) :good])]
                                                                       (if good-type
                                                                         (update acc good-type (fnil + 0) (:colonists building))
                                                                         acc)))
                                                                   {} occupied-production-buildings)
                                         other-goods-production
                                         (reduce (fn [acc [good-type capacity]]
                                                   (let [matching-plantations (count (filter #(= (:type %) good-type)
                                                                                             occupied-plantations))]
                                                     (if (pos? matching-plantations)
                                                       (assoc acc good-type (min matching-plantations capacity))
                                                       acc)))
                                                 {} building-capacity)

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

                                         ;; Role selector privilege: +1 of one produced kind (auto-choose the
                                         ;; most valuable kind that still has supply left)
                                         privilege-production (if (and is-role-selector? (seq limited-production))
                                                                (let [privilege-good (->> (keys limited-production)
                                                                                          (filter #(> (get current-supply % 0)
                                                                                                      (get limited-production % 0)))
                                                                                          (sort-by good-values >)
                                                                                          first)]
                                                                  (if privilege-good
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
    final-game-state))

(defn has-occupied-building? [player building-type]
  "Check if player has an occupied building of the given type"
  (some #(and (= (:type %) building-type) (pos? (:colonists %)))
        (:buildings player)))

(defn can-trade-good? [game-state player good]
  "Check if player can trade a specific good. The trading house buys only
   different kinds (exception: occupied office) and holds at most 4 goods."
  (and (pos? (get-in player [:goods good] 0))
       (< (count (:trading-house game-state)) 4)
       (or (not (contains? (set (map :good (:trading-house game-state))) good))
           (has-occupied-building? player :office))))

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
  "Execute the trader role - sell goods to trading house.
   The full trading house is emptied at the END of the trader phase, not here."
  (let [player-idx (state/player-index game-state player-id)
        player (when player-idx (get-in game-state [:players player-idx]))
        role-selector-idx (:role-selector-idx game-state)
        is-role-selector? (= player-idx role-selector-idx)
        base-trade-value (get good-values good-choice 0)
        ;; Market bonuses stack: small +1 and large +2 (both = +3)
        market-bonus (+ (if (has-occupied-building? player :small-market) 1 0)
                        (if (has-occupied-building? player :large-market) 2 0))
        ;; Role selector gets +1 privilege bonus
        privilege-bonus (if is-role-selector? 1 0)
        total-value (+ base-trade-value market-bonus privilege-bonus)]
    (if (and good-choice player (can-trade-good? game-state player good-choice))
      (-> game-state
          (update-in [:players player-idx :goods good-choice] dec)
          (update-in [:players player-idx :money] + total-value)
          (update :trading-house conj {:good good-choice :player-id player-id}))
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
  "Find the ship this good must be loaded on, per Puerto Rico rules:
   1. If a ship already carries this good, it MUST be used. If that ship is
      full, this good cannot be shipped at all (returns nil).
   2. Otherwise an empty ship is used: the smallest that fits everything, or
      if none fits everything, the one that loads the most (partial load).
   Returns [idx ship] or nil."
  (let [indexed (map-indexed vector ships)
        same-good-ship (first (filter (fn [[_ ship]] (= (:good ship) good)) indexed))
        empty-ships (filter (fn [[_ ship]] (nil? (:good ship))) indexed)]
    (if same-good-ship
      ;; Must use the ship already carrying this good - unless it's full
      (let [[_ ship] same-good-ship]
        (when (< (:amount ship) (:capacity ship))
          same-good-ship))
      ;; No ship carries this good - use an empty ship
      (let [fits-all (filter (fn [[_ ship]] (>= (:capacity ship) amount)) empty-ships)]
        (if (seq fits-all)
          (first (sort-by (fn [[_ ship]] (:capacity ship)) fits-all))
          ;; Nothing fits everything: must load as many as possible on the biggest
          (last (sort-by (fn [[_ ship]] (:capacity ship)) empty-ships)))))))

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

(defn can-use-wharf?
  "Occupied wharf, not yet used this captain phase, and any goods to ship"
  [game-state player-idx]
  (let [player (get-in game-state [:players player-idx])]
    (and (has-occupied-building? player :wharf)
         (not (contains? (get game-state :wharf-used #{}) player-idx))
         (some pos? (vals (:goods player))))))

(defn captain-can-act?
  "A player can act in the captain phase if they can load on a cargo ship or
   use their wharf"
  [game-state player-idx]
  (or (boolean (can-ship-goods? game-state (get-in game-state [:players player-idx])))
      (can-use-wharf? game-state player-idx)))

(defn pass-captain-turn
  "Record a captain-phase turn where the player did not load anything.
   The phase ends after a full lap of consecutive non-loading turns."
  [game-state]
  (update game-state :captain-passes (fnil inc 0)))

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

(defn- award-loading-bonuses
  "Award harbor (+1 VP per loading action) and captain privilege (+1 VP for the
   role selector's first load this phase) after a successful load."
  [game-state player-idx]
  (let [player (get-in game-state [:players player-idx])
        game-after-harbor (if (has-occupied-building? player :harbor)
                            (first (award-victory-points game-state player-idx 1))
                            game-state)
        is-captain (= player-idx (:role-selector-idx game-state))]
    (if (and is-captain (not (:captain-bonus-awarded game-state)))
      (-> (first (award-victory-points game-after-harbor player-idx 1))
          (assoc :captain-bonus-awarded true))
      game-after-harbor)))

(defn execute-captain
  "Execute one captain-phase loading turn. With wharf? truthy, the player ships
   ALL goods of the chosen kind to the supply via their wharf instead of a ship."
  [game-state player-id good-choice & [wharf?]]
  (let [player-idx (state/player-index game-state player-id)
        player (when player-idx (get-in game-state [:players player-idx]))
        amount-to-ship (get-in player [:goods good-choice] 0)]
    (cond
      (or (nil? good-choice) (nil? player) (zero? amount-to-ship))
      game-state

      ;; Wharf: ship all goods of one kind straight to the supply
      wharf?
      (if (can-use-wharf? game-state player-idx)
        (-> (first (award-victory-points game-state player-idx amount-to-ship))
            (award-loading-bonuses player-idx)
            (update-in [:players player-idx :goods good-choice] - amount-to-ship)
            (update-in [:goods-supply good-choice] + amount-to-ship)
            (update :wharf-used (fnil conj #{}) player-idx)
            (assoc :captain-passes 0))
        game-state)

      ;; Normal load onto a cargo ship
      :else
      (if-let [[ship-idx ship] (find-ship-for-good (:ships game-state) good-choice amount-to-ship)]
        (let [actual-amount (min amount-to-ship (- (:capacity ship) (:amount ship)))]
          (if (pos? actual-amount)
            (-> (first (award-victory-points game-state player-idx actual-amount))
                (award-loading-bonuses player-idx)
                (update-in [:players player-idx :goods good-choice] - actual-amount)
                (assoc-in [:ships ship-idx :good] good-choice)
                (update-in [:ships ship-idx :amount] + actual-amount)
                (assoc :captain-passes 0))
            game-state))
        game-state))))

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

(defn warehouse-kinds-storable
  "How many complete KINDS of goods a player may keep at the end of the captain
   phase: small warehouse 1, large warehouse 2, both 3, none 0. Every player may
   additionally keep exactly one single good on their windrose."
  [player]
  (+ (if (has-occupied-building? player :small-warehouse) 1 0)
     (if (has-occupied-building? player :large-warehouse) 2 0)))

(defn- store-player-goods
  "Returns [player-with-kept-goods discarded-goods-map]"
  [player]
  (let [current-goods (:goods player)
        goods-with-amounts (filter #(pos? (second %)) current-goods)
        kinds-storable (warehouse-kinds-storable player)
        ;; Auto-choice: keep the kinds with the most goods (ties: most valuable)
        sorted-kinds (sort-by (fn [[good amount]] [amount (get good-values good 0)])
                              #(compare %2 %1)
                              goods-with-amounts)
        kept-kinds (map first (take kinds-storable sorted-kinds))
        remaining (drop kinds-storable sorted-kinds)
        ;; Windrose: one single good of the most valuable remaining kind
        windrose-kind (->> remaining (map first) (sort-by good-values >) first)
        new-goods (merge {:corn 0 :indigo 0 :sugar 0 :tobacco 0 :coffee 0}
                         (select-keys current-goods kept-kinds)
                         (when windrose-kind {windrose-kind 1}))
        discarded (merge-with - current-goods new-goods)]
    [(assoc player :goods new-goods) discarded]))

(defn apply-storage-rules [game-state]
  "Apply storage rules at end of captain phase - players must discard excess goods"
  (let [results (map store-player-goods (:players game-state))
        updated-players (mapv first results)
        total-discarded (reduce #(merge-with + %1 %2) {} (map second results))]
    (-> game-state
        (assoc :players updated-players)
        (update :goods-supply #(merge-with + % total-discarded)))))

(defn- start-new-round [game-state]
  (let [num-players (count (:players game-state))
        game-roles (or (:roles game-state) state/roles)
        unpicked-roles (set/difference (set game-roles) (:used-roles game-state))
        new-governor (mod (inc (:governor-idx game-state)) num-players)]
    (-> game-state
        (update :round inc)
        (assoc :available-roles (set game-roles))
        (assoc :used-roles #{})
        ;; Unpicked role placards accumulate 1 doubloon each
        (update :role-gold (fn [role-gold]
                             (reduce (fn [rg role] (update rg role (fnil inc 0)))
                                     role-gold
                                     unpicked-roles)))
        ;; Governor passes clockwise; the new governor selects first
        (assoc :governor-idx new-governor)
        (assoc :current-player-idx new-governor)
        (assoc :players-selected-this-round 0)
        (assoc :phase :role-selection))))

(defn- end-game [game-state]
  (let [final-players (mapv (fn [player]
                              (assoc player :final-score (state/calculate-victory-points player)))
                            (:players game-state))
        ;; Most VP wins; ties broken by doubloons + goods (1 good = 1 doubloon)
        winner (last (sort-by (juxt :final-score state/tiebreaker-value) final-players))]
    (-> game-state
        (assoc :players final-players)
        (assoc :game-over true)
        (assoc :winner winner)
        (assoc :phase :game-over))))

(defn- finish-role-execution
  "Perform the completed role's end-of-phase duties, then return to role
   selection - or end the round (and possibly the game) if every player has
   selected a role."
  [game-state]
  (let [completed-role (:selected-role game-state)
        base-game (-> game-state
                      (assoc :phase :role-selection)
                      (assoc :selected-role nil)
                      (assoc :role-selector-idx nil)
                      (assoc :role-execution-order nil)
                      (assoc :role-execution-current-idx nil)
                      (assoc :current-player-idx (state/next-player-idx game-state)))
        game-after-role (case completed-role
                          ;; Settler: discard leftover face-up tiles, reveal new ones
                          :settler (-> base-game
                                       replenish-plantations
                                       (dissoc :hacienda-used))
                          ;; Trader: empty the trading house only if it is full
                          :trader (clear-full-trading-house base-game)
                          ;; Captain: storage rules, then unload full ships
                          :captain (-> base-game
                                       (apply-storage-rules)
                                       (return-full-ships-to-supply)
                                       (dissoc :captain-bonus-awarded :captain-passes :wharf-used))
                          base-game)
        players-selected (:players-selected-this-round game-after-role)
        num-players (count (:players game-after-role))]
    (if (>= players-selected num-players)
      ;; Round complete - the game ends at the end of the round a trigger occurred in
      (if (state/check-victory-conditions game-after-role)
        (end-game game-after-role)
        (start-new-round game-after-role))
      ;; Round continues with next player selecting a role
      game-after-role)))

(defn- advance-captain
  "The captain phase loops clockwise as long as at least one player can load.
   Skips players who cannot act; ends the phase after a full lap of consecutive
   non-loading turns."
  [game-state]
  (let [num-players (count (:players game-state))
        current-idx (:role-execution-current-idx game-state)]
    (loop [idx (mod (inc current-idx) num-players)
           passes (get game-state :captain-passes 0)
           steps 0]
      (cond
        (or (>= passes num-players) (> steps num-players))
        (finish-role-execution game-state)

        (captain-can-act? game-state idx)
        (-> game-state
            (assoc :role-execution-current-idx idx)
            (assoc :captain-passes passes))

        :else
        (recur (mod (inc idx) num-players) (inc passes) (inc steps))))))

(defn advance-role-execution [game-state]
  "Move to the next player in role execution order, or end the role when all
   players have acted. The captain phase instead loops until nobody can load."
  (if (= (:selected-role game-state) :captain)
    (advance-captain game-state)
    (let [execution-order (:role-execution-order game-state)
          current-idx (:role-execution-current-idx game-state)
          current-position (.indexOf execution-order current-idx)
          next-position (inc current-position)]
      (if (>= next-position (count execution-order))
        (finish-role-execution game-state)
        (assoc game-state :role-execution-current-idx (nth execution-order next-position))))))

(defn execute-role [game-state role player-id & args]
  "Execute the selected role with given arguments"
  (case role
    :settler (execute-settler game-state player-id (first args))
    :mayor (execute-mayor game-state)
    :builder (execute-builder game-state player-id (first args))
    :craftsman (execute-craftsman game-state)
    :trader (execute-trader game-state player-id (first args))
    :captain (execute-captain game-state player-id (first args) (second args))
    (:prospector :prospector-2)
    (let [executor-idx (:role-execution-current-idx game-state)
          selector-idx (:role-selector-idx game-state)]
      ;; Only the role selector gets the 1 gold bonus
      (if (= executor-idx selector-idx)
        (update-in game-state [:players executor-idx :money] inc)
        game-state))
    game-state))

;; Game phase management
(defn end-role-execution [game-state]
  "End the current role execution immediately (used for table-wide roles like
   mayor and craftsman, which execute once for all players)"
  (finish-role-execution game-state))

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
    :role-action
    (let [role (:role move)
          game-after (apply execute-role game-state role (:player-id move) (:args move))]
      (if (and (= role :settler)
               (= (first (:args move)) :random-from-deck))
        ;; Hacienda bonus draw happens BEFORE the regular settler take:
        ;; it does not consume the player's turn
        game-after
        (case role
          ;; Mayor and craftsman execute once for the whole table; prospector
          ;; only affects the selector - end the role immediately
          (:mayor :craftsman :prospector :prospector-2)
          (end-role-execution game-after)

          ;; Captain: a turn that loaded nothing counts as a pass so the
          ;; looping phase always terminates
          :captain
          (advance-role-execution (if (identical? game-after game-state)
                                    (pass-captain-turn game-after)
                                    game-after))

          (advance-role-execution game-after))))
    game-state))
