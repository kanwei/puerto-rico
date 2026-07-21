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
      (let [idx (.indexOf ^java.util.List face-up-plantations plantation-choice)
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

;; --------------------------------------------------------------------------
;; Mayor phase
;;
;; Distribution (privilege colonist + dealing the ship) is automatic - the
;; rules leave no choice. PLACEMENT is a real decision: each player, mayor
;; first then clockwise, has their board swept into their hand and places
;; colonists one at a time. The rulebook allows full rearrangement, which the
;; sweep makes expressible one placement at a time. A player may only stop
;; placing when their hand is empty or no empty circle remains (leftovers go
;; to the windrose / San Juan).
;; --------------------------------------------------------------------------

(defn sweep-colonists-to-hand
  "Move all of a player's colonists (board + San Juan + hand) into their hand
   for re-placement during their mayor turn"
  [game-state player-idx]
  (update-in game-state [:players player-idx]
             (fn [player]
               (let [p-cols (reduce (fn [acc p] (+ acc (long (:colonists p 0)))) 0 (:plantations player))
                     b-cols (reduce (fn [acc b] (+ acc (long (:colonists b 0)))) 0 (:buildings player))
                     total (+ p-cols b-cols (:san-juan-colonists player) (:colonists-in-hand player))]
                 (-> player
                     ;; mapv is still okay here since we NEED a new vector, but reduce is faster for counting
                     (assoc :plantations (mapv #(assoc % :colonists 0) (:plantations player)))
                     (assoc :buildings (mapv #(assoc % :colonists 0) (:buildings player)))
                     (assoc :san-juan-colonists 0)
                     (assoc :colonists-in-hand total))))))

(defn placement-destinations
  "Destination types where the player can place a colonist from hand:
   {:plantations #{types with an unmanned tile} :buildings #{types with an open circle}}"
  [player]
  {:plantations (set (map :type (filter #(zero? (:colonists %)) (:plantations player))))
   :buildings (set (map :type (filter #(pos? (get-empty-spaces %)) (:buildings player))))})

(defn can-place-colonist? [player]
  (and (pos? (:colonists-in-hand player))
       (let [{:keys [plantations buildings]} (placement-destinations player)]
         (or (seq plantations) (seq buildings)))))

(defn mayor-turn-done?
  "A player may only end their placement turn when their hand is empty or
   there is nowhere left to place (all empty circles must be filled)"
  [player]
  (not (can-place-colonist? player)))

(declare mayor-next-turn)

(defn begin-mayor-phase
  "Runs when the mayor placard is selected: privilege colonist from the supply,
   deal the ship one at a time clockwise from the mayor, then start the
   placement turns (mayor first)."
  [game-state]
  (let [selector-idx (:role-selector-idx game-state)
        num-players (count (:players game-state))
        ;; privilege: 1 colonist from the supply (not the ship)
        game-after-privilege (if (and selector-idx (pos? (:colonist-supply game-state)))
                               (-> game-state
                                   (update-in [:players selector-idx :colonists-in-hand] inc)
                                   (update :colonist-supply dec))
                               game-state)
        ;; deal the ship starting with the mayor, clockwise
        colonists-on-ship (:colonist-ship game-after-privilege)
        game-after-ship (loop [game game-after-privilege
                               remaining colonists-on-ship
                               idx (or selector-idx 0)]
                          (if (<= remaining 0)
                            (assoc game :colonist-ship 0)
                            (recur (update-in game [:players idx :colonists-in-hand] inc)
                                   (dec remaining)
                                   (mod (inc idx) num-players))))]
    (mayor-next-turn game-after-ship 0)))

(defn- kind->tiles-key [dest-kind]
  (if (= dest-kind :plantation) :plantations :buildings))

(defn execute-mayor-placement
  "Place one colonist from the player's hand on a tile of the given TYPE (used
   by the AI, which fills a swept board by type). dest-kind is :plantation or
   :building."
  [game-state player-id dest-kind dest-key]
  (let [player-idx (state/player-index game-state player-id)
        player (when player-idx (get-in game-state [:players player-idx]))]
    (if (and player (pos? (:colonists-in-hand player)))
      (let [tiles-key (kind->tiles-key dest-kind)
            tile-idx (first (keep-indexed
                             (fn [i tile]
                               (when (and (= (:type tile) dest-key)
                                          (pos? (get-empty-spaces tile)))
                                 i))
                             (get player tiles-key)))]
        (if tile-idx
          (-> game-state
              (update-in [:players player-idx tiles-key tile-idx :colonists] inc)
              (update-in [:players player-idx :colonists-in-hand] dec))
          game-state))
      game-state)))

(defn execute-mayor-place-at
  "Place one colonist from hand onto a SPECIFIC tile (by index). Used by the
   human UI, which shows the real board and clicks individual circles."
  [game-state player-id dest-kind idx]
  (let [player-idx (state/player-index game-state player-id)
        player (when player-idx (get-in game-state [:players player-idx]))
        tiles-key (kind->tiles-key dest-kind)
        tile (get-in player [tiles-key idx])]
    (if (and player (pos? (:colonists-in-hand player))
             tile (pos? (get-empty-spaces tile)))
      (-> game-state
          (update-in [:players player-idx tiles-key idx :colonists] inc)
          (update-in [:players player-idx :colonists-in-hand] dec))
      game-state)))

(defn execute-mayor-remove-at
  "Take a colonist off a SPECIFIC tile (by index) back into hand, so the human
   can rearrange their existing colonists."
  [game-state player-id dest-kind idx]
  (let [player-idx (state/player-index game-state player-id)
        player (when player-idx (get-in game-state [:players player-idx]))
        tile (get-in player [(kind->tiles-key dest-kind) idx])]
    (if (and player tile (pos? (:colonists tile 0)))
      (-> game-state
          (update-in [:players player-idx (kind->tiles-key dest-kind) idx :colonists] dec)
          (update-in [:players player-idx :colonists-in-hand] inc))
      game-state)))

(defn- refill-colonist-ship
  "Mayor's last duty: 1 colonist per empty building circle across all players,
   minimum the player count. A shortfall triggers the game-end condition."
  [game-state]
  (let [num-players (count (:players game-state))
        ;; Eliminated the slow 'for' comprehension
        empty-circles (reduce (fn [acc player]
                                (+ acc (reduce (fn [acc2 b]
                                                 (+ acc2 (max 0 (- (get-tile-capacity b) (:colonists b 0)))))
                                               0 (:buildings player))))
                              0 (:players game-state))
        to-add (max num-players empty-circles)
        available (min to-add (:colonist-supply game-state))]
    (-> game-state
        (update :colonist-ship + available)
        (update :colonist-supply - available)
        (cond-> (< available to-add) (assoc :colonist-ship-shortfall true)))))

(defn- finish-mayor-turn
  "Any colonists left in hand (only possible when no circles remain) go to the
   windrose / San Juan"
  [game-state player-idx]
  (update-in game-state [:players player-idx]
             (fn [player]
               (-> player
                   (update :san-juan-colonists + (:colonists-in-hand player))
                   (assoc :colonists-in-hand 0)))))

(defn empty-circle-count [player]
  (let [p-empty (reduce (fn [acc p] (if (zero? (:colonists p 0)) (inc acc) acc)) 0 (:plantations player))
        b-empty (reduce (fn [acc b] (+ acc (get-empty-spaces b))) 0 (:buildings player))]
    (+ p-empty b-empty)))

(defn- fill-all-circles
  "Rulebook shortcut: all empty circles must be filled if possible, so when the
   hand covers every circle there is no decision - man everything and send the
   leftovers to San Juan"
  [game-state player-idx]
  (update-in game-state [:players player-idx]
             (fn [player]
               (let [placed (empty-circle-count player)]
                 (-> player
                     (update :plantations (fn [ps] (mapv #(assoc % :colonists 1) ps)))
                     (update :buildings (fn [bs] (mapv #(assoc % :colonists (get-tile-capacity %)) bs)))
                     (update :san-juan-colonists + (- (:colonists-in-hand player) placed))
                     (assoc :colonists-in-hand 0))))))

(declare finish-role-execution)

(defn- mayor-next-turn
  "Hand the placement turn to each player from order-position onward.

   AI players sweep their whole board into hand and re-fill it by type -
   simpler for the search, and it lets them freely re-arrange. Auto-resolved
   when there is no real choice (hand covers every circle, or nothing to do).

   Human players keep their existing board placement and receive the newly
   arrived colonists plus any leftover San Juan colonists in hand;
   they add/remove workers by clicking, so we stop for their decision
   whenever they have colonists to place.

   Refill the ship and end the role when nobody is left."
  [game-state order-position]
  (let [order (:role-execution-order game-state)]
    (loop [gs game-state, pos order-position]
      (if (>= pos (count order))
        (-> gs refill-colonist-ship finish-role-execution)
        (let [idx (nth order pos)
              ai? (get-in gs [:players idx :is-ai])]
          (if ai?
            ;; AI: sweep and auto-resolve where possible
            (let [gs (sweep-colonists-to-hand gs idx)
                  player (get-in gs [:players idx])
                  hand (:colonists-in-hand player)
                  circles (empty-circle-count player)]
              (cond
                (or (zero? hand) (zero? circles)) (recur (finish-mayor-turn gs idx) (inc pos))
                ;; hand covers every circle -> placement is forced (no decision).
                ;; Record it so the UI can report the auto-fill instead of the
                ;; player silently vanishing from the log.
                (>= hand circles) (recur (-> (fill-all-circles gs idx)
                                             (update :mayor-auto-fill-log (fnil conj [])
                                                     {:player (:name player) :spaces circles}))
                                         (inc pos))
                :else (assoc gs :role-execution-current-idx idx)))
            ;; Human: keep the board, move SJ colonists to hand so they become
            ;; available for re-placement, then stop for an interactive turn if
            ;; they have colonists (otherwise nothing to do - skip)
            ;; Human: keep the board, move SJ colonists to hand so they become
            ;; available for re-placement, then stop for an interactive turn if
            ;; they have colonists (otherwise nothing to do - skip)
            (let [player (get-in gs [:players idx])
                  total-hand (+ (:colonists-in-hand player 0)
                                (:san-juan-colonists player 0))]
              (if (pos? total-hand)
                (-> gs
                    (assoc-in [:players idx :colonists-in-hand] total-hand)
                    (assoc-in [:players idx :san-juan-colonists] 0)
                    (assoc :role-execution-current-idx idx))
                (recur (finish-mayor-turn gs idx) (inc pos))))))))))

(defn building-cost
  "Actual cost of a building for a player. Zero allocations."
  [game-state player building-key]
  (let [building-info (get state/buildings building-key)

        ;; Fast O(N) reduction over the vector array, zero lazy sequences
        occupied-quarries (reduce (fn [acc p]
                                    (if (and (= (:type p) :quarry) (pos? (:colonists p 0)))
                                      (inc acc)
                                      acc))
                                  0 (:plantations player))

        quarry-discount (min occupied-quarries (long (get building-info :column 0)))
        selector-idx (:role-selector-idx game-state)

        privilege-discount (if (and (= (:selected-role game-state) :builder)
                                    selector-idx
                                    (= (:id player) (:id (nth (:players game-state) selector-idx))))
                             1 0)]
    (max 0 (- (long (get building-info :cost 0)) quarry-discount privilege-discount))))

(defn can-build-building?
  "Check if the player may build this building. Zero allocations, Fail-Fast."
  [game-state player building-key]
  (let [building-info (get state/buildings building-key)]
    (and building-info
         ;; 1. Fast supply lookup (get is much faster than get-in)
         (pos? (get (:building-supply game-state) building-key 0))

         ;; 2. Fast check if already owned.
         ;; Returning `(reduced true)` instantly breaks the loop the moment we find it.
         (not (reduce (fn [_ b] (if (= (:type b) building-key) (reduced true) false))
                      false
                      (:buildings player)))

         ;; 3. Check affordability BEFORE city slots.
         ;; (Money is the most common blocker. If they can't afford it, fail instantly
         ;; without running the city-slots loop below!)
         (>= (:money player) (building-cost game-state player building-key))

         ;; 4. Fast inline city slots check.
         ;; Avoids calling external functions and allocates zero memory.
         (let [current-slots (reduce (fn [slots b]
                                       (+ slots (if (= (:type (get state/buildings (:type b))) :large) 2 1)))
                                     0
                                     (:buildings player))
               cost-slots (if (= (:type building-info) :large) 2 1)]
           (<= (+ current-slots cost-slots) 12)))))

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
                                         supply (:goods-supply current-game-state)

                                         ;; 1. Fast pass over plantations
                                         p-counts (reduce (fn [acc p]
                                                            (if (pos? (:colonists p 0))
                                                              (update acc (:type p) (fnil inc 0))
                                                              acc))
                                                          {} (:plantations player))

                                         ;; 2. Fast pass over buildings for capacity
                                         b-caps (reduce (fn [acc b]
                                                          (if (pos? (:colonists b 0))
                                                            (if-let [good-type (get-in state/buildings [(:type b) :good])]
                                                              (update acc good-type (fnil + 0) (:colonists b 0))
                                                              acc)
                                                            acc))
                                                        {} (:buildings player))

                                         ;; Calculate limits immediately
                                         corn-prod (min (get p-counts :corn 0) (get supply :corn 0))
                                         ind-prod  (min (get p-counts :indigo 0) (get b-caps :indigo 0) (get supply :indigo 0))
                                         sug-prod  (min (get p-counts :sugar 0) (get b-caps :sugar 0) (get supply :sugar 0))
                                         tob-prod  (min (get p-counts :tobacco 0) (get b-caps :tobacco 0) (get supply :tobacco 0))
                                         cof-prod  (min (get p-counts :coffee 0) (get b-caps :coffee 0) (get supply :coffee 0))

                                         produced {:corn corn-prod, :indigo ind-prod, :sugar sug-prod, :tobacco tob-prod, :coffee cof-prod}
                                         kinds-produced (reduce (fn [acc v] (if (pos? v) (inc acc) acc)) 0 (vals produced))

                                         factory-bonus (if (and (> kinds-produced 1)
                                                                (some #(and (= (:type %) :factory) (pos? (:colonists % 0)))
                                                                      (:buildings player)))
                                                         (case kinds-produced 2 1, 3 2, 4 3, 5 5, 0)
                                                         0)

                                         updated-player (-> player
                                                            (update-in [:goods :corn] + corn-prod)
                                                            (update-in [:goods :indigo] + ind-prod)
                                                            (update-in [:goods :sugar] + sug-prod)
                                                            (update-in [:goods :tobacco] + tob-prod)
                                                            (update-in [:goods :coffee] + cof-prod)
                                                            (update :money + factory-bonus))

                                         updated-supply (-> supply
                                                            (update :corn - corn-prod)
                                                            (update :indigo - ind-prod)
                                                            (update :sugar - sug-prod)
                                                            (update :tobacco - tob-prod)
                                                            (update :coffee - cof-prod))

                                         next-state (-> current-game-state
                                                        (assoc-in [:players player-idx] updated-player)
                                                        (assoc :goods-supply updated-supply))]
                                     (if is-role-selector?
                                       (assoc next-state :craftsman-produced
                                              (set (keep (fn [[k v]] (when (pos? v) k)) produced)))
                                       next-state)))

        ;; Process each player in turn order
        final-game-state (reduce (fn [current-game player-idx]
                                   (let [is-role-selector? (= player-idx role-selector-idx)]
                                     (produce-goods-for-player current-game player-idx is-role-selector?)))
                                 game-state
                                 turn-order)
        ;; Privilege is the craftsman's LAST duty: one extra good of a kind
        ;; they produced, taken after everyone has produced (supply allowing).
        ;; With a real choice (2+ kinds available) the selector must decide.
        produced (get final-game-state :craftsman-produced #{})
        candidates (set (filter #(pos? (get-in final-game-state [:goods-supply %])) produced))
        gs (dissoc final-game-state :craftsman-produced)]
    (cond
      (empty? candidates) gs

      (= 1 (count candidates))
      (let [good (first candidates)]
        (-> gs
            (update-in [:players role-selector-idx :goods good] inc)
            (update-in [:goods-supply good] dec)))

      :else (assoc gs :craftsman-privilege-pending candidates))))

(defn execute-craftsman-privilege
  "Selector's craftsman privilege: take one extra good of a kind they produced"
  [game-state player-id good]
  (let [player-idx (state/player-index game-state player-id)
        candidates (get game-state :craftsman-privilege-pending #{})]
    (if (and (contains? candidates good)
             (= player-idx (:role-selector-idx game-state)))
      (-> game-state
          (update-in [:players player-idx :goods good] inc)
          (update-in [:goods-supply good] dec)
          (dissoc :craftsman-privilege-pending))
      game-state)))

(defn has-occupied-building? [player building-type]
  "Check if player has an occupied building of the given type"
  (some #(and (= (:type %) building-type) (pos? (:colonists %)))
        (:buildings player)))

(defn can-trade-good? [game-state player good]
  "Check if player can trade a specific good. The trading house buys only
   different kinds (exception: occupied office) and holds at most 4 goods."
  (let [th (:trading-house game-state)]
    (and (pos? (get-in player [:goods good] 0))
         (< (count th) 4)
         (or (has-occupied-building? player :office)
             (not (or (= good (:good (nth th 0 nil)))
                      (= good (:good (nth th 1 nil)))
                      (= good (:good (nth th 2 nil)))
                      (= good (:good (nth th 3 nil)))))))))

(defn clear-full-trading-house [game-state]
  "Empty the trading house at the end of the trader phase if it is FULL.
   The house has four spaces, so 'full' means four goods total - NOT four
   distinct kinds. An occupied office can sell a duplicate kind, so the house
   can be full with fewer than four kinds; counting kinds here left it stuck
   and un-clearable, which blocked all future trading."
  (let [trading-house (:trading-house game-state)]
    (if (>= (count trading-house) 4)
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

(defn valid-ship-options
  "Return all valid ship options for loading `good` of `amount`, per Puerto Rico rules:
   1. If a ship already carries this good, it MUST be used (only option).
      If full, returns nil.
   2. Among empty ships: must pick one that loads the MOST goods.
      - If some ships can fit everything, all such ships are valid options.
      - If no ship fits everything, only ships that load the most (biggest) are valid.
   Returns a vector of [idx ship loadable-amount] sorted by capacity ascending,
   or nil if no ship can be used."
  [ships good amount]
  (loop [i 0
         same-good nil
         empty-ships []]
    (if (< i (count ships))
      (let [ship (nth ships i)
            s-good (:good ship)
            s-cap (:capacity ship)
            remaining (- s-cap (:amount ship))]
        (cond
          ;; Ship already carries this good
          (= s-good good)
          (recur (inc i)
                 (if (nil? same-good) [i ship remaining] same-good)
                 empty-ships)

          ;; Empty ship
          (nil? s-good)
          (recur (inc i) same-good
                 (conj empty-ships [i ship remaining]))

          :else
          (recur (inc i) same-good empty-ships)))
      ;; Done scanning
      (cond
        ;; Rule 1: must use same-good ship (if it has space)
        same-good
        (if (pos? (nth same-good 2))
          [(vector (first same-good) (second same-good) (nth same-good 2))]
          ;; Full ship carrying this good — cannot ship
          nil)

        ;; Rule 2: among empty ships, find max loadable
        (seq empty-ships)
        (let [max-load (apply max (map #(nth % 2) empty-ships))
              ;; If some ships can fit everything (loadable >= amount), only those count
              can-fit-all (some #(>= (nth % 2) amount) empty-ships)
              best (if can-fit-all
                     (filter #(>= (nth % 2) amount) empty-ships)
                     (filter #(= (nth % 2) max-load) empty-ships))]
          ;; Sort by capacity ascending (smallest first)
          (sort-by (comp :capacity second) best))

        :else
        nil))))

(defn find-ship-for-good [ships good amount]
  "Find the ship this good must be loaded on, per Puerto Rico rules.
   Returns [idx ship] or nil. Picks the smallest valid ship (for backward compat)."
  (when-let [options (valid-ship-options ships good amount)]
    (let [[idx ship _] (first options)]
      [idx ship])))

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
                 (valid-ship-options ships good-type amount)))
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
   ALL goods of the chosen kind to the supply via their wharf instead of a ship.
   Optional ship-idx can specify which ship to load onto (when multiple valid options exist).
   Calling: (execute-captain gs player-id good)          ; auto-pick ship
            (execute-captain gs player-id good ship-idx) ; pick specific ship
            (execute-captain gs player-id good :wharf)   ; use wharf"
  [game-state player-id good-choice & [ship-idx-or-wharf]]
  (let [wharf? (= ship-idx-or-wharf :wharf)
        ship-idx (when (and (not wharf?) (number? ship-idx-or-wharf))
                   ship-idx-or-wharf)
        player-idx (state/player-index game-state player-id)
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
      (if-let [options (valid-ship-options (:ships game-state) good-choice amount-to-ship)]
        (let [[target-idx ship ship-remaining]
              (if ship-idx
                ;; Use the specified ship if it's in the valid options
                (or (first (filter #(= (first %) ship-idx) options))
                    ;; Fallback to first valid option if specified ship is invalid
                    (first options))
                ;; Auto-pick smallest valid ship
                (first options))
              actual-amount (min amount-to-ship ship-remaining)]
          (if (pos? actual-amount)
            (-> (first (award-victory-points game-state player-idx actual-amount))
                (award-loading-bonuses player-idx)
                (update-in [:players player-idx :goods good-choice] - actual-amount)
                (assoc-in [:ships target-idx :good] good-choice)
                (update-in [:ships target-idx :amount] + actual-amount)
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
          (update :used-roles conj role)
          ;; Mayor: colonist distribution is automatic; placement turns follow
          (cond-> (= role :mayor) begin-mayor-phase)))
    game-state))

(defn warehouse-kinds-storable
  "How many complete KINDS of goods a player may keep at the end of the captain
   phase: small warehouse 1, large warehouse 2, both 3, none 0. Every player may
   additionally keep exactly one single good on their windrose."
  [player]
  (+ (if (has-occupied-building? player :small-warehouse) 1 0)
     (if (has-occupied-building? player :large-warehouse) 2 0)))

;; --------------------------------------------------------------------------
;; Storage sub-phase (end of the captain phase)
;;
;; Each player may keep: all goods of one kind per warehouse "slot" (small
;; warehouse 1, large 2, both 3) plus exactly one single good on the windrose.
;; Everything else returns to the supply. When a player has real options
;; (more kinds than slots), that's a decision; trivial cases auto-resolve.
;; During the sub-phase :storage-phase is true and :storage-picks tracks the
;; current player's selections {:kinds #{goods} :single good-or-nil}.
;; --------------------------------------------------------------------------

(defn- goods-kinds [player]
  (set (keep (fn [[g amount]] (when (pos? amount) g)) (:goods player))))

(defn needs-storage-decision?
  "A choice exists when the player holds more kinds than fit their warehouses
   and at least two kinds (with one kind the forced single is unambiguous)"
  [player]
  (let [kinds (count (goods-kinds player))]
    (and (>= kinds 2) (> kinds (warehouse-kinds-storable player)))))

(defn legal-storage-picks
  "Remaining storage choices for the player at player-idx:
   {:kinds #{goods eligible for a warehouse slot} :singles #{goods eligible for the windrose}}"
  [game-state player-idx]
  (let [player (get-in game-state [:players player-idx])
        picks (get-in game-state [:storage-picks player-idx])
        kinds-left (- (warehouse-kinds-storable player) (count (:kinds picks)))
        unkept (remove (:kinds picks #{}) (goods-kinds player))]
    {:kinds (if (pos? kinds-left) (set unkept) #{})
     :singles (if (:single picks) #{} (set unkept))}))

(defn execute-storage-pick
  "op is :store-kind (keep all goods of a kind - uses a warehouse slot) or
   :store-single (keep one good on the windrose)"
  [game-state player-id op good]
  (let [player-idx (state/player-index game-state player-id)
        {:keys [kinds singles]} (legal-storage-picks game-state player-idx)]
    (case op
      :store-kind (if (contains? kinds good)
                    (update-in game-state [:storage-picks player-idx :kinds]
                               (fnil conj #{}) good)
                    game-state)
      :store-single (if (contains? singles good)
                      (assoc-in game-state [:storage-picks player-idx :single] good)
                      game-state)
      game-state)))

(defn- finalize-player-storage
  "Apply the player's picks: keep chosen kinds in full plus the single;
   everything else goes back to the supply"
  [game-state player-idx]
  (let [player (get-in game-state [:players player-idx])
        {:keys [kinds single]} (get-in game-state [:storage-picks player-idx])
        current-goods (:goods player)
        new-goods (merge {:corn 0 :indigo 0 :sugar 0 :tobacco 0 :coffee 0}
                         (select-keys current-goods (vec (or kinds #{})))
                         (when (and single (not (contains? kinds single)))
                           {single 1}))
        discarded (merge-with - current-goods new-goods)]
    (-> game-state
        (assoc-in [:players player-idx :goods] new-goods)
        (update :goods-supply #(merge-with + % discarded)))))

(defn- auto-store
  "Resolve storage for a player with no real choice: keep everything when the
   kinds fit the warehouses; with one kind and no warehouse keep one single."
  [game-state player-idx]
  (let [player (get-in game-state [:players player-idx])
        kinds (goods-kinds player)
        slots (warehouse-kinds-storable player)]
    (cond
      (empty? kinds) game-state
      (<= (count kinds) slots) game-state          ;; everything fits, keep all
      :else ;; single kind, no warehouse: keep 1 of it
      (-> game-state
          (assoc-in [:storage-picks player-idx] {:kinds #{} :single (first kinds)})
          (finalize-player-storage player-idx)))))

(declare finish-role-execution)

(defn- begin-storage-phase
  "Runs when captain loading ends: resolve trivial storage automatically and
   stop at the first player with a real decision, or finish the role."
  [game-state]
  (let [order (:role-execution-order game-state)]
    (loop [gs (assoc game-state :storage-phase true :storage-picks {})
           [idx & more] order]
      (cond
        (nil? idx) (finish-role-execution (dissoc gs :storage-phase :storage-picks))
        (needs-storage-decision? (get-in gs [:players idx]))
        (-> gs
            (assoc :role-execution-current-idx idx)
            (assoc :storage-order (vec more)))
        :else (recur (auto-store gs idx) more)))))

(defn- advance-storage
  "Finalize the current player's storage and move to the next player who has a
   decision, auto-resolving the rest"
  [game-state]
  (let [current (:role-execution-current-idx game-state)]
    (loop [gs (finalize-player-storage game-state current)
           [idx & more] (:storage-order game-state)]
      (cond
        (nil? idx) (finish-role-execution
                    (dissoc gs :storage-phase :storage-picks :storage-order))
        (needs-storage-decision? (get-in gs [:players idx]))
        (-> gs
            (assoc :role-execution-current-idx idx)
            (assoc :storage-order (vec more)))
        :else (recur (auto-store gs idx) more)))))

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
                          ;; Captain: unload full ships (storage already resolved
                          ;; in the storage sub-phase)
                          :captain (-> base-game
                                       (return-full-ships-to-supply)
                                       (dissoc :captain-bonus-awarded :captain-passes :wharf-used))
                          :craftsman (dissoc base-game :craftsman-privilege-pending)
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
  "The captain loading phase loops clockwise as long as at least one player can
   load. Skips players who cannot act; when a full lap passes with no loading,
   the loading ends and the storage sub-phase begins."
  [game-state]
  (let [num-players (count (:players game-state))
        current-idx (:role-execution-current-idx game-state)]
    (loop [idx (mod (inc current-idx) num-players)
           passes (get game-state :captain-passes 0)
           steps 0]
      (cond
        (or (>= passes num-players) (> steps num-players))
        (begin-storage-phase game-state)

        (captain-can-act? game-state idx)
        (-> game-state
            (assoc :role-execution-current-idx idx)
            (assoc :captain-passes passes))

        :else
        (recur (mod (inc idx) num-players) (inc passes) (inc steps))))))

(defn- advance-mayor
  "Finish the current player's placement turn (leftovers to San Juan), then
   move on to the remaining players - or refill the ship and end the role"
  [game-state]
  (let [execution-order (:role-execution-order game-state)
        current-idx (:role-execution-current-idx game-state)]
    ;; All empty circles must be filled: refuse to end the turn while the
    ;; player can still place
    (if (can-place-colonist? (get-in game-state [:players current-idx]))
      game-state
      (-> game-state
          (finish-mayor-turn current-idx)
          (mayor-next-turn (inc (.indexOf ^java.util.List execution-order current-idx)))))))

(defn advance-role-execution [game-state]
  "Move to the next player in role execution order, or end the role when all
   players have acted. The captain and mayor phases have their own flows."
  (case (:selected-role game-state)
    :captain (if (:storage-phase game-state)
               (advance-storage game-state)
               (advance-captain game-state))
    :mayor (advance-mayor game-state)
    (let [execution-order (:role-execution-order game-state)
          current-idx (:role-execution-current-idx game-state)
          current-position (.indexOf ^java.util.List execution-order current-idx)
          next-position (inc current-position)]
      (if (>= next-position (count execution-order))
        (finish-role-execution game-state)
        (assoc game-state :role-execution-current-idx (nth execution-order next-position))))))

(defn execute-role [game-state role player-id & args]
  "Execute the selected role with given arguments"
  (case role
    :settler (execute-settler game-state player-id (first args))
    ;; Mayor placement ops (empty args = done, handled by the advance flow):
    ;;   [:place-colonist :plantation :corn]  place by type   (AI, swept board)
    ;;   [:place-at :building 2]              place on tile 2  (human)
    ;;   [:remove-at :plantation 0]           un-man tile 0    (human rearrange)
    :mayor (case (first args)
             :place-colonist (execute-mayor-placement game-state player-id (second args) (nth args 2))
             :place-at (execute-mayor-place-at game-state player-id (second args) (nth args 2))
             :remove-at (execute-mayor-remove-at game-state player-id (second args) (nth args 2))
             game-state)
    :builder (execute-builder game-state player-id (first args))
    ;; Craftsman: table-wide production, then possibly the selector's
    ;; privilege pick [:privilege good]
    :craftsman (cond
                 (= (first args) :privilege)
                 (execute-craftsman-privilege game-state player-id (second args))

                 ;; already produced - waiting on the privilege pick
                 (:craftsman-privilege-pending game-state) game-state

                 :else (execute-craftsman game-state))
    :trader (execute-trader game-state player-id (first args))
    ;; Captain: loading turns, or storage picks during the storage sub-phase
    :captain (if (:storage-phase game-state)
               (if (seq args)
                 (execute-storage-pick game-state player-id (first args) (second args))
                 game-state)
               (execute-captain game-state player-id (first args) (second args)))
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
          args (:args move)
          game-after (apply execute-role game-state role (:player-id move) args)]
      (cond
        ;; Hacienda bonus draw happens BEFORE the regular settler take:
        ;; it does not consume the player's turn
        (and (= role :settler) (= (first args) :random-from-deck))
        game-after

        ;; Mayor placement: the player keeps placing until done (empty args)
        (= role :mayor)
        (if (seq args)
          game-after
          (advance-role-execution game-after))

        ;; Craftsman: production runs once for the whole table, but the role
        ;; only ends once the selector's privilege pick is resolved
        (= role :craftsman)
        (if (:craftsman-privilege-pending game-after)
          game-after
          (end-role-execution game-after))

        ;; Prospector only affects the selector - end the role immediately
        (contains? #{:prospector :prospector-2} role)
        (end-role-execution game-after)

        ;; Captain storage: picks continue the turn; auto-finalize once
        ;; nothing pickable remains; empty args = done
        (and (= role :captain) (:storage-phase game-state))
        (let [player-idx (:role-execution-current-idx game-after)
              {:keys [kinds singles]} (legal-storage-picks game-after player-idx)]
          (if (and (seq args) (or (seq kinds) (seq singles)))
            game-after
            (advance-role-execution game-after)))

        ;; Captain loading: a turn that loaded nothing counts as a pass so
        ;; the looping phase always terminates
        (= role :captain)
        (advance-role-execution (if (identical? game-after game-state)
                                  (pass-captain-turn game-after)
                                  game-after))

        :else
        (advance-role-execution game-after)))
    game-state))
