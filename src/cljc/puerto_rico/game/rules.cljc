(ns puerto-rico.game.rules
  "Puerto Rico game rules implementation"
  (:require [puerto-rico.game.state :as state]))

;; Forward declarations
(declare end-role-execution execute-role)

;; Role execution functions

(defn execute-settler [game-state player-id plantation-choice]
  "Execute the settler role - player gets to take a plantation from face-up tiles or a quarry"
  (let [player-idx (->> (:players game-state)
                        (map-indexed vector)
                        (filter #(= (:id (second %)) player-id))
                        first
                        first)
        player (get-in game-state [:players player-idx])
        face-up-plantations (:face-up-plantations game-state)
        quarry-supply (:quarry-supply game-state)
        ;; Check if player has occupied hospice
        has-hospice (has-occupied-building? player :hospice)]
    (cond
      ;; Invalid parameters
      (not (and plantation-choice player-idx
                (>= player-idx 0)
                (< player-idx (count (:players game-state)))))
      game-state

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
                                 new-plantation-idx (dec plantation-count)] ; Index of the quarry we just added
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
      (let [;; Remove the first occurrence of the chosen plantation
            idx (.indexOf face-up-plantations plantation-choice)
            updated-face-up (vec (concat (subvec face-up-plantations 0 idx)
                                         (subvec face-up-plantations (inc idx))))
            ;; Draw replacement from deck
            plantation-deck (:plantation-supply game-state)
            new-face-up (if (seq plantation-deck)
                          (conj updated-face-up (first plantation-deck))
                          updated-face-up)
            new-deck (if (seq plantation-deck)
                       (vec (rest plantation-deck))
                       [])
            ;; Update player's plantations
            updated-players (assoc-in (:players game-state)
                                      [player-idx :plantations]
                                      (conj (get-in (:players game-state) [player-idx :plantations])
                                            {:type plantation-choice :colonists 0}))
            ;; Basic game state after taking plantation
            game-after-plantation (assoc game-state
                                         :face-up-plantations new-face-up
                                         :plantation-supply new-deck
                                         :players updated-players)
            ;; Apply hospice bonus if applicable
            final-game (if has-hospice
                         (let [colonist-supply (:colonist-supply game-after-plantation)
                               colonist-ship (:colonist-ship game-after-plantation)
                               plantation-count (count (get-in game-after-plantation [:players player-idx :plantations]))
                               new-plantation-idx (dec plantation-count)] ; Index of the plantation we just added
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

(defn auto-place-colonists [player]
  "Automatically place colonists for a player on tiles with empty circles"
  (let [colonists-to-place (+ (:colonists-in-hand player) (:san-juan-colonists player))
        ;; Get all tiles (plantations and buildings) with their empty spaces
        plantations-with-spaces (map-indexed
                                 (fn [idx plantation]
                                   {:idx idx :type :plantation :tile plantation :empty-spaces (get-empty-spaces plantation)})
                                 (:plantations player))
        buildings-with-spaces (map-indexed
                               (fn [idx building]
                                 {:idx idx :type :building :tile building :empty-spaces (get-empty-spaces building)})
                               (:buildings player))

        ;; Combine all tiles with empty spaces
        all-empty-spaces (filter #(> (:empty-spaces %) 0)
                                 (concat plantations-with-spaces buildings-with-spaces))

        ;; Sort by empty spaces (prioritize tiles with more spaces)
        sorted-spaces (sort-by :empty-spaces > all-empty-spaces)

        ;; Place colonists on tiles
        [updated-player remaining-colonists]
        (reduce (fn [[player remaining] space-info]
                  (if (<= remaining 0)
                    [player remaining]
                    (let [spaces-to-fill (min remaining (:empty-spaces space-info))
                          tile-path (if (= (:type space-info) :plantation)
                                      [:plantations (:idx space-info)]
                                      [:buildings (:idx space-info)])]
                      [(update-in player (conj tile-path :colonists) + spaces-to-fill)
                       (- remaining spaces-to-fill)])))
                [player colonists-to-place]
                sorted-spaces)]

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

        ;; Step 3: Auto-place colonists for ALL players
        game-after-placement (update game-after-ship :players
                                     (fn [players]
                                       (mapv auto-place-colonists players)))

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
  "Execute the craftsman role - all players produce goods"
  (let [role-selector-idx (:role-selector-idx game-state)

        ;; Function to produce goods for a single player
        produce-goods-for-player (fn [player]
                                   (let [;; Get occupied plantations (have at least 1 colonist)
                                         occupied-plantations (filter #(and (:colonists %) (> (:colonists %) 0)) (:plantations player))
;; Get occupied production buildings
                                         occupied-production-buildings (filter #(and (:colonists %) (> (:colonists %) 0)
                                                                                     ;; Check if building is a production type
                                                                                     (let [building-info (get state/buildings (:type %))]
                                                                                       (= (:type building-info) :production))) (:buildings player))

                ;; Corn is special - it produces without a building
                                         corn-plantations (filter #(= (:type %) :corn) occupied-plantations)
                                         corn-production (count corn-plantations)

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

                ;; Update player's goods (limited by goods supply)
                                         updated-goods (reduce (fn [goods [good-type amount]]
                                                                 (if (and (> amount 0) (> (get-in game-state [:goods-supply good-type] 0) 0))
                                                                   (update goods good-type + amount)
                                                                   goods))
                                                               (:goods player) total-production)]

                                     (println "Player" (:name player) "produced:" total-production)
                                     (assoc player :goods updated-goods)))

        ;; Update all players
        updated-players (mapv produce-goods-for-player (:players game-state))

        ;; Calculate total goods produced to reduce from supply
        total-goods-produced (reduce (fn [acc player]
                                       (merge-with + acc
                                                   (reduce (fn [player-prod [good-type amount]]
                                                             (let [old-amount (get-in (:players game-state) [(.indexOf (:players game-state) player) :goods good-type] 0)
                                                                   new-amount (get-in player [:goods good-type] 0)
                                                                   produced (- new-amount old-amount)]
                                                               (if (> produced 0)
                                                                 (assoc player-prod good-type produced)
                                                                 player-prod)))
                                                           {} (:goods player))))
                                     {} updated-players)

        ;; Update goods supply
        updated-goods-supply (merge-with - (:goods-supply game-state) total-goods-produced)

        ;; Role selector gets privilege: +1 extra good if they produced anything
        final-players (if role-selector-idx
                        (let [role-selector (nth updated-players role-selector-idx)
                              ;; Find what goods they produced
                              produced-goods (filter #(> (get-in role-selector [:goods %] 0) 0) [:corn :indigo :sugar :tobacco :coffee])]
                          (if (seq produced-goods)
                            ;; Give +1 of the first good type they produced
                            (let [privilege-good (first produced-goods)]
                              (println "Role selector gets privilege:" privilege-good)
                              (assoc-in updated-players [role-selector-idx :goods privilege-good]
                                        (inc (get-in role-selector [:goods privilege-good] 0))))
                            updated-players))
                        updated-players)]

    (println "Craftsman executed (all players produce goods)")
    (println "Total goods produced:" total-goods-produced)
    (-> game-state
        (assoc :players final-players)
        (assoc :goods-supply updated-goods-supply))))

(defn can-trade-good? [game-state player good]
  "Check if player can trade a specific good"
  (and (pos? (get-in player [:goods good] 0))
       (not (contains? (set (map :good (:trading-house game-state))) good))
       (< (count (:trading-house game-state)) 4)))

(defn has-occupied-building? [player building-type]
  "Check if player has an occupied building of the given type"
  (some #(and (= (:type %) building-type) (pos? (:colonists % 0)))
        (:buildings player)))

(defn execute-trader [game-state player-id good-choice]
  "Execute the trader role - sell goods to trading house"
  (let [player-idx (->> (:players game-state)
                        (map-indexed vector)
                        (filter #(= (:id (second %)) player-id))
                        first
                        first)
        player (get-in game-state [:players player-idx])
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
        total-value (+ base-trade-value market-bonus)]
    (if (and good-choice (can-trade-good? game-state player good-choice))
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
  "Find appropriate ship for shipping goods according to Puerto Rico rules:
   1. If any ship already contains ANY good and has space, that good MUST go there (if it matches)
   2. Only if no partially filled ships can accept the good can you use an empty ship
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

        ;; Check if any partially filled ship can accept this good type
        compatible-filled-ship (->> partially-filled-ships
                                    (filter (fn [[idx ship]]
                                              (and (= (:good ship) good)
                                                   (>= (- (:capacity ship) (:amount ship)) amount))))
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

      ;; Rule 2: Only if no partially filled ship works, can use empty ship
      (and (nil? compatible-filled-ship) compatible-empty-ship)
      compatible-empty-ship

      ;; Rule 3: Cannot ship if no valid ship exists
      :else nil)))

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
            ;; Award VPs using helper function that respects supply
            [updated-game vps-awarded] (award-victory-points game-state player-idx actual-amount)]
        (-> updated-game
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
          game-with-counter (update game-with-gold :players-selected-this-round inc)]
      (if (= role :prospector)
        ;; Prospector is privilege-only: only selector gets benefit, then next role selection
        (let [game-after-prospector (-> game-with-counter
                                        (update-in [:players selector-idx :money] inc)
                                        (update :available-roles disj role)
                                        (update :used-roles conj role)
                                        (assoc :current-player-idx (state/next-player-idx game-state)))
              players-selected (:players-selected-this-round game-after-prospector)
              num-players (count (:players game-after-prospector))]
          ;; Check if round should end after prospector (each player has selected)
          (if (>= players-selected num-players)
            ;; Round is complete, start new round
            (let [unpicked-roles (clojure.set/difference (set state/roles) (:used-roles game-after-prospector))]
              (-> game-after-prospector
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
                  (assoc :governor-idx (mod (inc (:governor-idx game-after-prospector)) num-players))
                  (assoc :current-player-idx (mod (inc (:governor-idx game-after-prospector)) num-players))
                  (assoc :players-selected-this-round 0)
                  (assoc :phase :role-selection)
                  ;; Clear trading house
                  (assoc :trading-house [])
                  ;; Reset ships if full
                  (update :ships (fn [ships]
                                   (mapv (fn [ship]
                                           (if (= (:amount ship) (:capacity ship))
                                             (assoc ship :good nil :amount 0)
                                             ship))
                                         ships)))))
            ;; Round continues
            game-after-prospector))
        ;; All other roles: all players execute in turn order
        (let [execution-order (state/create-role-execution-order game-state selector-idx)]
          (-> game-with-counter
              (assoc :selected-role role)
              (assoc :role-selector-idx selector-idx)
              (assoc :role-execution-order execution-order)
              (assoc :role-execution-current-idx (first execution-order))
              (assoc :phase :role-execution)
              (update :available-roles disj role)
              (update :used-roles conj role)))))
    game-state))

(defn calculate-warehouse-storage [player]
  "Calculate how many goods types a player can store based on warehouses"
  (let [has-small-warehouse (has-occupied-building? player :small-warehouse)
        has-large-warehouse (has-occupied-building? player :large-warehouse)]
    (cond
      ;; Both warehouses: can store 3 types (1 base + 1 small + 2 large, but note says 3 total)
      (and has-small-warehouse has-large-warehouse) 3
      ;; Large warehouse only: can store 3 types (1 base + 2 large)
      has-large-warehouse 3
      ;; Small warehouse only: can store 2 types (1 base + 1 small)
      has-small-warehouse 2
      ;; No warehouses: can store 1 type (base storage)
      :else 1)))

(defn apply-storage-rules [game-state]
  "Apply storage rules at end of captain phase - players must discard excess goods"
  (update game-state :players
          (fn [players]
            (mapv (fn [player]
                    (let [current-goods (:goods player)
                          goods-with-amounts (filter #(pos? (second %)) current-goods)
                          goods-types-count (count goods-with-amounts)
                          max-storable-types (calculate-warehouse-storage player)]
                      (if (<= goods-types-count max-storable-types)
                        ;; Player can store all their goods
                        player
                        ;; Player must choose which goods to keep
                        ;; For now, keep the most valuable goods (coffee > tobacco > sugar > indigo > corn)
                        ;; TODO: This should be a player choice in the UI
                        (let [goods-priority {:coffee 5 :tobacco 4 :sugar 3 :indigo 2 :corn 1}
                              sorted-goods (sort-by #(get goods-priority (first %) 0) > goods-with-amounts)
                              goods-to-keep (take max-storable-types sorted-goods)
                              goods-to-discard (drop max-storable-types sorted-goods)
                              ;; Calculate new goods amounts (keep all of the storable types)
                              new-goods (reduce (fn [goods [good-type amount]]
                                                  (assoc goods good-type amount))
                                                {:corn 0 :indigo 0 :sugar 0 :tobacco 0 :coffee 0}
                                                goods-to-keep)]
                          ;; Update player's goods (goods are returned to supply automatically)
                          (assoc player :goods new-goods)))))
                  players))))

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
                              ;; If Captain role just finished, apply storage rules and empty full ships
                              (if (= completed-role :captain)
                                (-> base-game
                                    (apply-storage-rules)
                                    (update :ships (fn [ships]
                                                     (mapv (fn [ship]
                                                             (if (= (:amount ship) (:capacity ship))
                                                               (assoc ship :good nil :amount 0)
                                                               ship))
                                                           ships))))
                                base-game))
            players-selected (:players-selected-this-round game-after-role)
            num-players (count (:players game-after-role))]
        ;; Check if round should end (each player has selected a role)
        (if (>= players-selected num-players)
          ;; Round is complete, start new round
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
                (assoc :trading-house [])
                ;; Reset ships if full
                (update :ships (fn [ships]
                                 (mapv (fn [ship]
                                         (if (= (:amount ship) (:capacity ship))
                                           (assoc ship :good nil :amount 0)
                                           ship))
                                       ships)))))
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
    :prospector (let [executor-idx (:role-execution-current-idx game-state)]
                  (update-in game-state [:players executor-idx :money] inc))
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
      ;; Round is complete, start new round
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
            (assoc :trading-house [])
            ;; Reset ships if full
            (update :ships (fn [ships]
                             (mapv (fn [ship]
                                     (if (= (:amount ship) (:capacity ship))
                                       (assoc ship :good nil :amount 0)
                                       ship))
                                   ships)))))
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
    :role-action (-> (execute-role game-state (:role move) (:player-id move) (:args move))
                     (advance-role-execution))
    game-state))
