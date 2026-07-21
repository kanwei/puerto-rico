(ns puerto-rico.game.state
  "Core game state definitions for Puerto Rico board game")

;; Game constants
(def goods [:corn :indigo :sugar :tobacco :coffee])
(def buildings
  ;; Production Buildings - Column 1
  {:small-indigo-maker {:type :production :cost 1 :good :indigo :vp 1 :worker 1 :count 4 :column 1}
   :small-sugar-maker {:type :production :cost 2 :good :sugar :vp 1 :worker 1 :count 4 :column 1}

   ;; Production Buildings - Column 2  
   :large-indigo-maker {:type :production :cost 3 :good :indigo :vp 2 :worker 3 :count 3 :column 2}
   :large-sugar-maker {:type :production :cost 4 :good :sugar :vp 2 :worker 3 :count 3 :column 2}

   ;; Production Buildings - Column 3
   :tobacco-maker {:type :production :cost 5 :good :tobacco :vp 3 :worker 3 :count 3 :column 3}
   :coffee-maker {:type :production :cost 6 :good :coffee :vp 3 :worker 2 :count 3 :column 3}

   ;; Trade Buildings - Column 1
   :small-market {:type :trade :cost 1 :vp 1 :worker 1 :count 2 :column 1
                  :description "When the owner of an occupied small market sells a barrel in the trader phase, he gets an extra doubloon from the bank for it."}
   :hacienda {:type :plantation :cost 2 :vp 1 :worker 1 :count 2 :column 1
              :description "On his turn in the settler phase, the owner of an occupied hacienda may, before he takes a face-up plantation tile, take an additional tile chosen at random from the rest of the supply."}
   :construction-hut {:type :building :cost 2 :vp 1 :worker 1 :count 2 :column 1
                      :description "In the settler phase, the owner of an occupied construction hut, can place a quarry on his island of one of the face-up plantation tiles."}
   :small-warehouse {:type :storage :cost 3 :vp 1 :worker 1 :count 2 :column 1
                     :description "The owner of an occupied small warehouse may store, at the end of the captain phase, in addition to the single goods barrel he is allowed to store on his windrose, all the barrels of one kind of goods that he chooses."}

   ;; Trade Buildings - Column 2
   :large-market {:type :trade :cost 5 :vp 2 :worker 1 :count 2 :column 2
                  :description "When the owner of an occupied large market sells a good in the trader phase, he gets an extra 2 doubloons from the bank for it."}
   :hospice {:type :plantation :cost 4 :vp 2 :worker 1 :count 2 :column 2
             :description "During the settler phase, when the owner of an occupied hospice places a plantation or quarry tile on his island, he may take a colonist from the colonist supply and place it on this tile."}
   :office {:type :trade :cost 5 :vp 2 :worker 1 :count 2 :column 2
            :description "When the owner of an occupied office sells a good to the trading house in the trader phase, it need not be different than the goods already there. If the trading house is full, the player cannot sell a good there!"}
   :large-warehouse {:type :storage :cost 6 :vp 2 :worker 1 :count 2 :column 2
                     :description "The owner of an occupied large warehouse may store, at the end of the captain phase, in addition to the single goods barrel he is allowed to store on his wind rose, all the barrels of two kinds of goods that he chooses."}

   ;; Trade Buildings - Column 3
   :factory {:type :production :cost 7 :vp 3 :worker 1 :count 2 :column 3
             :description "If the owner of an occupied factory produces goods of more than one kind in the craftsman phase, he earns money from the bank: for two kinds of goods, he earns 1 doubloon, for three kinds of goods, he earns 2 doubloons, for four kinds of goods, he earns 3 doubloons, and for all five kinds of goods, he earns 5 doubloons."}
   :university {:type :building :cost 8 :vp 3 :worker 1 :count 2 :column 3
                :description "During the builder phase, when the owner of an occupied university builds a building in his city, he may take a colonist from the colonist supply and place it on this tile."}
   :harbor {:type :shipping :cost 8 :vp 3 :worker 1 :count 2 :column 3
            :description "Each time, during the captain phase, the owner of an occupied harbour loads goods on a cargo ship, he earns one extra victory point."}
   :wharf {:type :shipping :cost 9 :vp 3 :worker 1 :count 2 :column 3
           :description "During the captain phase, when a player with an occupied wharf must load goods, instead of loading them on a cargo ship, he may place all goods of one kind in the goods supply and score the appropriate victory points as though he has loaded them on a cargo ship."}

   ;; Large Buildings - Column 4
   :guild-hall {:type :large :cost 10 :vp 4 :worker 1 :count 1 :column 4
                :description "The owner of the occupied guild hall earns, at game end, an additional 1 VP for each small production building (occupied or unoccupied) in his city, and an additional 2 VP for each large production building."}
   :residence {:type :large :cost 10 :vp 4 :worker 1 :count 1 :column 4
               :description "The owner of the occupied residence earns, at game end, additional victory points for the plantations and quarries he has placed on his island. For up to nine filled island spaces, he earns 4 VP, for ten filled island spaces, he earns 5 VP, for eleven filled island spaces, he earns 6 VP, and for all twelve spaces filled, he earns 7 VP."}
   :customs-house {:type :large :cost 10 :vp 4 :worker 1 :count 1 :column 4
                   :description "The owner of the occupied customs house earns, at game end, one additional victory point for every four victory points he acquired during the game."}
   :city-hall {:type :large :cost 10 :vp 4 :worker 1 :count 1 :column 4
               :description "The owner of the occupied city hall earns, at game end, one additional victory point for each violet building and each green building(occupied or unoccupied) in his city."}
   :fortress {:type :large :cost 10 :vp 4 :worker 1 :count 1 :column 4
              :description "The owner of the occupied fortress earns, at game end, one additional victory point for every three colonists on his player board."}})

(def roles [:settler :mayor :builder :craftsman :trader :captain :prospector])

(defn roles-for-player-count
  "Role placards in play: 2 players use seven placards (one prospector), 3 players
   use no prospector, 4 players one, 5 players two."
  [num-players]
  (case num-players
    2 [:settler :mayor :builder :craftsman :trader :captain :prospector]
    3 [:settler :mayor :builder :craftsman :trader :captain]
    4 [:settler :mayor :builder :craftsman :trader :captain :prospector]
    5 [:settler :mayor :builder :craftsman :trader :captain :prospector :prospector-2]
    [:settler :mayor :builder :craftsman :trader :captain :prospector]))

(defn selections-per-round
  "Role selections in one governor cycle before roles reset and the governor
   passes. The 2-player deluxe variant runs a longer cycle: the players alternate
   picking until each has taken three (six total of the seven placards), one
   placard left for a doubloon. Every other count picks exactly one role per
   player per round."
  [num-players]
  (if (= num-players 2) 6 num-players))

(defn starting-colonist-supply
  "Colonist supply (not counting the colonists that start on the ship)."
  [num-players]
  (case num-players 2 40, 3 55, 4 75, 5 95, 95))

(defn starting-victory-point-supply [num-players]
  (case num-players 2 65, 3 75, 4 100, 5 126, 126))

(defn num-cargo-ships
  "Cargo ships in play: the 2-player variant uses only the 4- and 6-space ships."
  [num-players]
  (if (= num-players 2) 2 3))

(def plantation-tiles
  {:corn 10 :indigo 12 :sugar 11 :tobacco 9 :coffee 8 :quarry 8})

(defn create-building-supply
  "Create building supply from building definitions with counts"
  [buildings]
  (reduce (fn [supply [building-key building-info]]
            (assoc supply building-key (:count building-info)))
          {}
          buildings))

(def production-building-keys
  "The six good-producing buildings. (The factory is :production-typed in the
   building def but counts as a beige building, so it is excluded here.)"
  #{:small-indigo-maker :small-sugar-maker :large-indigo-maker
    :large-sugar-maker :tobacco-maker :coffee-maker})

(defn create-building-supply-2p
  "2-player supply: one of each beige building, two of each of the six
   production buildings."
  []
  (into {} (map (fn [k] [k (if (production-building-keys k) 2 1)]) (keys buildings))))

(defn create-role-execution-order
  "Create the order in which players execute a role, starting with the role selector"
  [game-state role-selector-idx]
  (let [player-count (count (:players game-state))]
    (vec (take player-count
               (map #(mod % player-count)
                    (range role-selector-idx (+ role-selector-idx player-count)))))))

;; Player state structure
(defn new-player
  [id name]
  {:id id
   :name name
   :money 2
   :goods {:corn 0 :indigo 0 :sugar 0 :tobacco 0 :coffee 0}
   ;; Each plantation/building tracks its colonists
   :plantations [] ;; [{:type :corn :colonists 0} {:type :indigo :colonists 1}]
   :buildings [] ;; [{:type :small-indigo-maker :colonists 0}]
   ;; Track colonists in hand (from mayor role)
   :colonists-in-hand 0
   ;; San Juan storage for colonists that can't be placed
   :san-juan-colonists 0
   :victory-points 0
   :is-ai false})

;; Game state structure
(defn new-game-state
  [players]
  (let [num-players (count players)
        ;; Starting money equals doubloons per rulebook: 2P=3, 3P=2, 4P=3, 5P=4
        starting-money (case num-players 2 3, 3 2, 4 3, 5 4, 2)
        ;; Set up initial plantations according to rules
        players-with-plantations
        (vec (map-indexed
              (fn [idx player]
                (let [plantation (cond
                                   (= idx 0) :indigo ;; Governor gets indigo
                                   (and (= num-players 2) (= idx 1)) :corn
                                   (and (= num-players 3) (= idx 1)) :indigo
                                   (and (= num-players 3) (= idx 2)) :corn
                                   (and (= num-players 4) (= idx 1)) :indigo
                                   (and (= num-players 4) (>= idx 2)) :corn
                                   (and (= num-players 5) (>= idx 3)) :corn
                                   (and (= num-players 5) (= idx 1)) :indigo
                                   (and (= num-players 5) (= idx 2)) :indigo
                                   :else nil)
                      player (assoc player :money starting-money)]
                  (if plantation
                    (assoc player :plantations [{:type plantation :colonists 0}])
                    player)))
              players))
        game-roles (roles-for-player-count num-players)
        ;; Calculate plantation supply after initial distribution
        initial-plantations (map (fn [idx]
                                   (cond
                                     (= idx 0) :indigo
                                     (and (= num-players 2) (= idx 1)) :corn
                                     (and (= num-players 3) (= idx 1)) :indigo
                                     (and (= num-players 3) (= idx 2)) :corn
                                     (and (= num-players 4) (= idx 1)) :indigo
                                     (and (= num-players 4) (>= idx 2)) :corn
                                     (and (= num-players 5) (= idx 1)) :indigo
                                     (and (= num-players 5) (= idx 2)) :indigo
                                     (and (= num-players 5) (>= idx 3)) :corn
                                     :else nil))
                                 (range num-players))
        plantation-reductions (frequencies (remove nil? initial-plantations))
        ;; 2-player variant: remove three of each plantation tile (incl. quarry)
        ;; from the supply before dealing.
        base-tiles (if (= num-players 2)
                     (into {} (map (fn [[k v]] [k (- v 3)]) plantation-tiles))
                     plantation-tiles)
        remaining-plantations (merge-with - base-tiles plantation-reductions)
        ;; Separate quarries from regular plantations  
        regular-plantation-types [:corn :indigo :sugar :tobacco :coffee]
        quarry-count (:quarry remaining-plantations)
        regular-plantations (select-keys remaining-plantations regular-plantation-types)
        ;; Create shuffled deck from regular plantations only
        plantation-deck (shuffle (mapcat (fn [[type count]]
                                           (repeat count type))
                                         regular-plantations))
        ;; Draw n+1 face-up plantations
        face-up-count (inc num-players)
        face-up-plantations (vec (take face-up-count plantation-deck))
        remaining-deck (vec (drop face-up-count plantation-deck))]
    {:players players-with-plantations
     :current-player-idx 0
     :governor-idx 0 ;; Track who is the governor
     :round 1
     :phase :role-selection
     :selected-role nil
     :role-selector-idx nil
     :role-execution-order nil
     :role-execution-current-idx nil
     :roles game-roles
     :available-roles (set game-roles)
     :used-roles #{}
     ;; Track gold coins on each role card
     :role-gold (into {} (map #(vector % 0) game-roles))
     ;; Track how many players have selected roles this round
     :players-selected-this-round 0
     ;; Roles picked this round, in selection order (for the header role track)
     :role-pick-order []
     :plantation-supply remaining-deck
     :plantation-discard []
     :face-up-plantations face-up-plantations
     :quarry-supply quarry-count
     ;; 2-player variant removes two tokens of each good from the supply.
     :goods-supply (if (= num-players 2)
                     {:corn 8 :indigo 9 :sugar 9 :tobacco 7 :coffee 7}
                     {:corn 10 :indigo 11 :sugar 11 :tobacco 9 :coffee 9})
     :colonist-supply (starting-colonist-supply num-players)
     ;; Colonist ship for Mayor role (starts with number of players)
     :colonist-ship (count players-with-plantations)
     :building-supply (if (= num-players 2)
                        (create-building-supply-2p)
                        (create-building-supply buildings))
     :victory-point-supply (starting-victory-point-supply num-players)
     :ships (case num-players
              2 [{:capacity 4 :good nil :amount 0}
                 {:capacity 6 :good nil :amount 0}]
              3 [{:capacity 4 :good nil :amount 0}
                 {:capacity 5 :good nil :amount 0}
                 {:capacity 6 :good nil :amount 0}]
              4 [{:capacity 5 :good nil :amount 0}
                 {:capacity 6 :good nil :amount 0}
                 {:capacity 7 :good nil :amount 0}]
              5 [{:capacity 6 :good nil :amount 0}
                 {:capacity 7 :good nil :amount 0}
                 {:capacity 8 :good nil :amount 0}]
                        ;; Default to 4-player setup
              [{:capacity 5 :good nil :amount 0}
               {:capacity 6 :good nil :amount 0}
               {:capacity 7 :good nil :amount 0}])
     :trading-house []
     :game-over false
     :winner nil}))

;; Game state accessors
(defn current-player [game-state]
  (get-in game-state [:players (:current-player-idx game-state)]))

(defn current-governor [game-state]
  (get-in game-state [:players (:governor-idx game-state)]))

(defn player-by-id
  "Optimized: zero allocation loop instead of lazy filter."
  [game-state player-id]
  (let [players (:players game-state)
        n (count players)]
    (loop [i 0]
      (when (< i n)
        (let [p (nth players i)]
          (if (= (:id p) player-id)
            p
            (recur (inc i))))))))

(defn player-index
  "Index of the player with the given id in the players vector, or nil.
   Optimized: O(1) vector lookups, zero allocation loop."
  [game-state player-id]
  (let [players (:players game-state)
        n (count players)]
    (loop [i 0]
      (when (< i n)
        (if (= (:id (nth players i)) player-id)
          i
          (recur (inc i)))))))

(defn city-slots-used
  "City spaces occupied by a player's buildings; large buildings take 2 spaces.
   Optimized: Eliminated get-in array allocation overhead."
  [player]
  (reduce (fn [slots bldg]
            (+ slots (if (= :large (:type (buildings (:type bldg)))) 2 1)))
          0
          (:buildings player)))

(defn tiebreaker-value
  "Rulebook tiebreaker: doubloons plus goods (1 good = 1 doubloon)
   Optimized: Removed lazy (vals) sequence. Direct map lookup."
  [player]
  (let [g (:goods player)]
    (+ (:money player)
       (:corn g 0) (:indigo g 0) (:sugar g 0) (:tobacco g 0) (:coffee g 0))))

(defn next-player-idx [game-state]
  (mod (inc (:current-player-idx game-state))
       (count (:players game-state))))

(defn advance-to-next-player [game-state]
  (assoc game-state :current-player-idx (next-player-idx game-state)))

(defn has-occupied-building?
  "Check if player has an occupied building of the given type
   Optimized: Loop with early exit instead of lazy (some) closure."
  [player building-type]
  (let [bldgs (:buildings player)
        n (count bldgs)]
    (loop [i 0]
      (if (< i n)
        (let [b (nth bldgs i)]
          (if (and (= (:type b) building-type) (pos? (:colonists b)))
            true
            (recur (inc i))))
        false))))

(defn count-production-buildings
  "Count small and large production buildings in player's city
   Optimized: Single-pass zero-allocation loop."
  [player]
  (let [bldgs (:buildings player)
        n (count bldgs)]
    (loop [i 0 small 0 large 0]
      (if (< i n)
        (let [t (:type (nth bldgs i))]
          (case t
            (:small-indigo-maker :small-sugar-maker)
            (recur (inc i) (inc small) large)

            (:large-indigo-maker :large-sugar-maker :tobacco-maker :coffee-maker)
            (recur (inc i) small (inc large))

            ;; default
            (recur (inc i) small large)))
        {:small small :large large}))))

(defn count-filled-island-spaces
  "Count total plantations and quarries placed on player's island"
  [player]
  (count (:plantations player)))

(defn count-total-colonists
  "Count total colonists on player's board
   Optimized: Eager reduce instead of lazy map+reduce."
  [player]
  (+ (:san-juan-colonists player)
     (reduce (fn [sum p] (+ sum (:colonists p 0))) 0 (:plantations player))
     (reduce (fn [sum b] (+ sum (:colonists b 0))) 0 (:buildings player))))

(defn count-violet-buildings
  "Count violet (non-production) buildings in player's city
   Optimized: Single-pass zero-allocation loop."
  [player]
  (let [bldgs (:buildings player)
        n (count bldgs)]
    (loop [i 0 violet 0]
      (if (< i n)
        (let [t (:type (nth bldgs i))]
          (case t
            ;; If it's a production building, do nothing
            (:small-indigo-maker :small-sugar-maker
                                 :large-indigo-maker :large-sugar-maker
                                 :tobacco-maker :coffee-maker)
            (recur (inc i) violet)

            ;; Else, it's a violet building
            (recur (inc i) (inc violet))))
        violet))))

(defn calculate-large-building-bonuses
  "Calculate bonus victory points from occupied large buildings"
  [player]
  (let [guild-hall-bonus (if (has-occupied-building? player :guild-hall)
                           (let [{:keys [small large]} (count-production-buildings player)]
                             (+ (* small 1) (* large 2)))
                           0)
        residence-bonus (if (has-occupied-building? player :residence)
                          (let [filled-spaces (count-filled-island-spaces player)]
                            (cond
                              (>= filled-spaces 12) 7
                              (>= filled-spaces 11) 6
                              (>= filled-spaces 10) 5
                              ;; Rulebook: "up to nine filled island spaces" earns 4 VP
                              :else 4))
                          0)
        fortress-bonus (if (has-occupied-building? player :fortress)
                         (quot (count-total-colonists player) 3)
                         0)
        customs-house-bonus (if (has-occupied-building? player :customs-house)
                              (quot (:victory-points player) 4)
                              0)
        city-hall-bonus (if (has-occupied-building? player :city-hall)
                          (count-violet-buildings player)
                          0)]
    (+ guild-hall-bonus residence-bonus fortress-bonus customs-house-bonus city-hall-bonus)))

(defn special-vp-for-building
  "End-game bonus VP a large (violet) building is currently worth for this
   player, computed from the present board state. Occupation is ignored so an
   as-yet-unstaffed large building still shows the potential it would score.
   Ordinary buildings return 0."
  [player building-type]
  (case building-type
    :guild-hall    (let [{:keys [small large]} (count-production-buildings player)]
                     (+ (* small 1) (* large 2)))
    :residence     (let [filled (count-filled-island-spaces player)]
                     (cond (>= filled 12) 7
                           (>= filled 11) 6
                           (>= filled 10) 5
                           :else 4))
    :fortress      (quot (count-total-colonists player) 3)
    :customs-house (quot (:victory-points player) 4)
    :city-hall     (count-violet-buildings player)
    0))

;; Victory condition checks
(defn check-victory-conditions
  "Game ends at the end of the round in which: a player fills all 12 city spaces,
   the mayor could not fully refill the colonist ship, or the VP supply is exhausted."
  [game-state]
  (let [has-full-city (some #(>= (city-slots-used %) 12) (:players game-state))
        colonist-shortfall (:colonist-ship-shortfall game-state)
        vp-exhausted (<= (:victory-point-supply game-state) 0)]
    (or has-full-city colonist-shortfall vp-exhausted)))

(defn calculate-victory-points-breakdown [player]
  "Calculate victory points with detailed breakdown for game over screen"
  (let [building-vps (reduce + (map #(get-in buildings [(:type %) :vp] 0) (:buildings player)))
        shipping-vps (:victory-points player)

        ;; Calculate tiebreaker value (money + goods)
        total-goods (reduce + (vals (:goods player)))
        tiebreaker-value (+ (:money player) total-goods)

        ;; Large building bonuses breakdown
        guild-hall-bonus (if (has-occupied-building? player :guild-hall)
                           (let [{:keys [small large]} (count-production-buildings player)]
                             (+ (* small 1) (* large 2)))
                           0)
        residence-bonus (if (has-occupied-building? player :residence)
                          (let [filled-spaces (count-filled-island-spaces player)]
                            (cond
                              (>= filled-spaces 12) 7
                              (>= filled-spaces 11) 6
                              (>= filled-spaces 10) 5
                              ;; Rulebook: "up to nine filled island spaces" earns 4 VP
                              :else 4))
                          0)
        fortress-bonus (if (has-occupied-building? player :fortress)
                         (quot (count-total-colonists player) 3)
                         0)
        customs-house-bonus (if (has-occupied-building? player :customs-house)
                              (quot (:victory-points player) 4)
                              0)
        city-hall-bonus (if (has-occupied-building? player :city-hall)
                          (count-violet-buildings player)
                          0)

        large-building-bonuses (+ guild-hall-bonus residence-bonus fortress-bonus
                                  customs-house-bonus city-hall-bonus)
        total-vps (+ shipping-vps building-vps large-building-bonuses)]

    {:shipping-vps shipping-vps
     :building-vps building-vps
     :tiebreaker-value tiebreaker-value
     :large-building-bonuses large-building-bonuses
     :guild-hall-bonus guild-hall-bonus
     :residence-bonus residence-bonus
     :fortress-bonus fortress-bonus
     :customs-house-bonus customs-house-bonus
     :city-hall-bonus city-hall-bonus
     :total-vps total-vps}))

;; Calculate final victory points
(defn calculate-victory-points [player]
  (let [building-vps (reduce + (map #(get-in buildings [(:type %) :vp] 0) (:buildings player)))
        large-building-bonuses (calculate-large-building-bonuses player)]
    (+ (:victory-points player) building-vps large-building-bonuses)))
