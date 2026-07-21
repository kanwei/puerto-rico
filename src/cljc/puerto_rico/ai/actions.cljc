(ns puerto-rico.ai.actions
  "Fixed, enumerable action space for MCTS / neural-network policies.

   Every decision in the game is one of 54 atomic actions. The policy head of
   a network outputs one probability per action id; illegal ids are masked out
   by `legal-action-ids`. Compound turns (hacienda draw + regular take, the
   looping captain phase) decompose into sequences of these atomic actions."
  (:require [puerto-rico.game.state :as state]
            [puerto-rico.game.rules :as rules]))

;; Stable orderings (map iteration order is not stable across platforms)
(def role-order
  [:settler :mayor :builder :craftsman :trader :captain :prospector :prospector-2])

(def good-order [:corn :indigo :sugar :tobacco :coffee])

(def good-order-index (into {} (map-indexed (fn [i g] [g i]) good-order)))

(def plantation-order [:corn :indigo :sugar :tobacco :coffee :quarry])

(def building-order
  [:small-indigo-maker :small-sugar-maker :small-market :hacienda
   :construction-hut :small-warehouse :large-indigo-maker :large-sugar-maker
   :hospice :office :large-market :large-warehouse :tobacco-maker :coffee-maker
   :factory :university :harbor :wharf
   :guild-hall :residence :customs-house :city-hall :fortress])

(def action-table
  "Index -> action descriptor. 54 actions total."
  (vec
   (concat
    ;; 0-7: select a role placard
    (map (fn [r] {:kind :select-role :role r}) role-order)
    ;; 8-13: settler - take a face-up plantation or a quarry
    (map (fn [p] {:kind :settler-take :plantation p}) plantation-order)
    ;; 14: settler - hacienda bonus draw from the face-down stack
    [{:kind :settler-hacienda}]
    ;; 15-37: builder - build one of the 23 buildings
    (map (fn [b] {:kind :build :building b}) building-order)
    ;; 38-42: trader - sell one good
    (map (fn [g] {:kind :trade :good g}) good-order)
    ;; 43-47: captain - load one kind onto a cargo ship
    (map (fn [g] {:kind :ship :good g}) good-order)
    ;; 48-52: captain - wharf all goods of one kind
    (map (fn [g] {:kind :wharf :good g}) good-order)
    ;; 53: pass / done / execute-with-no-choice
    [{:kind :pass}]
    ;; 54-59: mayor - place a colonist on a plantation/quarry of a type
    (map (fn [p] {:kind :place-plantation :plantation p}) plantation-order)
    ;; 60-82: mayor - place a colonist on a building
    (map (fn [b] {:kind :place-building :building b}) building-order)
    ;; 83-87: storage - keep all goods of one kind (warehouse slot)
    (map (fn [g] {:kind :store-kind :good g}) good-order)
    ;; 88-92: storage - keep one single good on the windrose
    (map (fn [g] {:kind :store-single :good g}) good-order)
    ;; 93-97: craftsman privilege - which produced kind to take
    (map (fn [g] {:kind :privilege-good :good g}) good-order))))

(def num-actions (count action-table))

(def action-id
  "Action descriptor -> index"
  (into {} (map-indexed (fn [i a] [a i]) action-table)))

(defn actor-index
  "Seat index of the player who must act now"
  [game-state]
  (if (= (:phase game-state) :role-execution)
    (:role-execution-current-idx game-state)
    (:current-player-idx game-state)))

(defn actor-player [game-state]
  (nth (:players game-state) (actor-index game-state)))

(def ^:private pass-id (action-id {:kind :pass}))

;; Precomputed key -> action-id lookups so the hot path (legal-action-ids,
;; called on every MCTS node and every random-playout step) does a cheap
;; keyword lookup instead of constructing a descriptor map and hashing it.
(defn- id-map [f keys] (into {} (map (fn [k] [k (action-id (f k))]) keys)))
(def ^:private select-role->id (id-map (fn [r] {:kind :select-role :role r}) role-order))
(def ^:private settler-take->id (id-map (fn [p] {:kind :settler-take :plantation p}) plantation-order))
(def ^:private hacienda-id (action-id {:kind :settler-hacienda}))
(def ^:private build->id (id-map (fn [b] {:kind :build :building b}) building-order))
(def ^:private trade->id (id-map (fn [g] {:kind :trade :good g}) good-order))
(def ^:private ship->id (id-map (fn [g] {:kind :ship :good g}) good-order))
(def ^:private wharf->id (id-map (fn [g] {:kind :wharf :good g}) good-order))
(def ^:private place-plantation->id (id-map (fn [p] {:kind :place-plantation :plantation p}) plantation-order))
(def ^:private place-building->id (id-map (fn [b] {:kind :place-building :building b}) building-order))
(def ^:private store-kind->id (id-map (fn [g] {:kind :store-kind :good g}) good-order))
(def ^:private store-single->id (id-map (fn [g] {:kind :store-single :good g}) good-order))
(def ^:private privilege->id (id-map (fn [g] {:kind :privilege-good :good g}) good-order))

(defn legal-action-ids
  "Vector of legal action ids for the player who must act now.
   Empty when the game is over."
  [game-state]
  (cond
    (:game-over game-state) []

    (= (:phase game-state) :role-selection)
    (let [avail (:available-roles game-state)]
      (into [] (comp (filter avail) (map select-role->id)) role-order))

    (= (:phase game-state) :role-execution)
    (let [player-idx (actor-index game-state)
          player (nth (:players game-state) player-idx)]
      (case (:selected-role game-state)
        :settler
        (if (rules/island-full? player)
          [pass-id]
          (let [takes (map settler-take->id (distinct (:face-up-plantations game-state)))
                quarry (when (and (pos? (:quarry-supply game-state))
                                  (rules/may-take-quarry? game-state player-idx))
                         [(settler-take->id :quarry)])
                hacienda (when (and (rules/has-occupied-building? player :hacienda)
                                    (not (get-in game-state [:hacienda-used player-idx]))
                                    (seq (:plantation-supply game-state)))
                           [hacienda-id])]
            (into [] cat [takes quarry hacienda [pass-id]])))

        :builder
        (conj (into [] (comp (filter #(rules/can-build-building? game-state player %))
                             (map build->id))
                    building-order)
              pass-id)

        :trader
        (conj (into [] (comp (filter #(rules/can-trade-good? game-state player %))
                             (map trade->id))
                    good-order)
              pass-id)

        :captain
        (if (:storage-phase game-state)
          ;; Storage sub-phase: keep kinds / a single, or done (always legal)
          (let [{:keys [kinds singles]} (rules/legal-storage-picks game-state player-idx)]
            (into [] cat [(map store-kind->id (sort-by good-order-index kinds))
                          (map store-single->id (sort-by good-order-index singles))
                          [pass-id]]))
          ;; Loading turns
          (let [ships (into [] (comp (filter #(and (pos? (get-in player [:goods %] 0))
                                                   (rules/find-ship-for-good (:ships game-state) %
                                                                             (get-in player [:goods %] 0))))
                                     (map ship->id))
                            good-order)
                wharfs (when (rules/can-use-wharf? game-state player-idx)
                         (into [] (comp (filter #(pos? (get-in player [:goods %] 0)))
                                        (map wharf->id))
                               good-order))]
            ;; Loading on a cargo ship is mandatory when possible; the wharf may
            ;; substitute. Passing is only legal when no cargo-ship load exists.
            (if (seq ships)
              (into ships wharfs)
              (conj (vec wharfs) pass-id))))

        :mayor
        ;; Placement turns: place from hand until it's empty or nothing is
        ;; placeable; only then is "done" legal. This is the hottest branch of
        ;; the whole search (mayor placement is one atomic action per colonist),
        ;; so walk the player's own tiles ONCE into a transient set of distinct
        ;; destination-type ids, instead of allocating the two type-sets that
        ;; placement-destinations builds and then re-scanning the 6+23 canonical
        ;; order vectors. Result order is irrelevant to callers.
        (if (pos? (long (:colonists-in-hand player)))
          (let [ids (as-> (transient #{}) acc
                      (reduce (fn [a t]
                                (if (zero? (long (:colonists t 0)))
                                  (conj! a (place-plantation->id (:type t)))
                                  a))
                              acc (:plantations player))
                      (reduce (fn [a b]
                                (if (pos? (rules/get-empty-spaces b))
                                  (conj! a (place-building->id (:type b)))
                                  a))
                              acc (:buildings player))
                      (persistent! acc))]
            (if (seq ids) (vec ids) [pass-id]))
          [pass-id])

        :craftsman
        ;; After production the selector may face a privilege choice
        (if-let [candidates (seq (:craftsman-privilege-pending game-state))]
          (mapv privilege->id (sort-by good-order-index candidates))
          [pass-id])

        ;; Prospector has no choices
        [pass-id]))

    :else []))

(defn action->move
  "Convert an action id into the engine move format for rules/apply-move"
  [game-state id]
  (let [{:keys [kind role plantation building good]} (nth action-table id)
        player-id (:id (actor-player game-state))]
    (case kind
      :select-role {:type :select-role :role role :player-id player-id}
      :settler-take {:type :role-action :role :settler :player-id player-id
                     :args [plantation]}
      :settler-hacienda {:type :role-action :role :settler :player-id player-id
                         :args [:random-from-deck]}
      :build {:type :role-action :role :builder :player-id player-id
              :args [building]}
      :trade {:type :role-action :role :trader :player-id player-id
              :args [good]}
      :ship {:type :role-action :role :captain :player-id player-id
             :args [good]}
      :wharf {:type :role-action :role :captain :player-id player-id
              :args [good :wharf]}
      :place-plantation {:type :role-action :role :mayor :player-id player-id
                         :args [:place-colonist :plantation plantation]}
      :place-building {:type :role-action :role :mayor :player-id player-id
                       :args [:place-colonist :building building]}
      :store-kind {:type :role-action :role :captain :player-id player-id
                   :args [:store-kind good]}
      :store-single {:type :role-action :role :captain :player-id player-id
                     :args [:store-single good]}
      :privilege-good {:type :role-action :role :craftsman :player-id player-id
                       :args [:privilege good]}
      :pass {:type :role-action :role (:selected-role game-state)
             :player-id player-id :args []})))

(defn apply-action
  "Apply an action id to the game state, returning the next state"
  [game-state id]
  (rules/apply-move game-state (action->move game-state id)))

;; --------------------------------------------------------------------------
;; Heuristic action scoring for prior blending
;; --------------------------------------------------------------------------

(defn- production-good
  "The good a production building outputs, or nil for non-production buildings."
  [building-key]
  (get-in state/buildings [building-key :good]))

(defn heuristic-action-scores
  "Return a {action-id score} map for the current actor, scoring how
   desirable each legal action is from a domain-knowledge perspective.

   Mayor priority tiers (pair-aware):
     1. Production building WITH matching plantations    → 0.85
     2. Plantation WITH matching production building      → 0.75
     3. Corn plantation (feeding value)                   → 0.55
     4. Non-production buildings (utility)                → 0.45
     5. Unmatched plantation (useless)                    → 0.25
     6. Unmatched production building (useless)           → 0.25

   A production building scores low when no plantations of that good exist,
   and vice versa — unpaired placements are dead weight.

   Scores are in [0.1, 0.9].  An endgame-saturation decay pushes all scores
   toward 0.5 (neutral) when most table buildings are already placed.

   Returns nil when no heuristic applies (role selection, pass-only, etc.)."
  [game-state]
  (let [player (actor-player game-state)
        phase (:phase game-state)]
    (when (= phase :role-execution)
      (case (:selected-role game-state)
        :mayor
        (let [;; Goods for which the player has production buildings
              prod-building-goods (set (keep production-good
                                             (map :type (:buildings player))))
              ;; Goods for which the player has plantations
              plantation-goods (set (map :type (:plantations player)))
              ;; Saturation: 0.0 = early game, 1.0 = all 12-slots per player full
              all-buildings (mapcat :buildings (:players game-state))
              total-slots (* (count (:players game-state)) 12)
              saturation (min 1.0 (/ (count all-buildings) total-slots))
              ;; Decay: early game = 1.0 (full heuristic), saturated = 0.3
              decay (+ 0.3 (* 0.7 (max 0 (- 1.0 saturation))))
              legal (legal-action-ids game-state)]
          (when-not (= legal [pass-id])
            (into {}
                  (map (fn [id]
                         (let [{:keys [kind building plantation]} (nth action-table id)
                               raw (case kind
                                     :place-building
                                     (let [good (production-good building)]
                                       (if good
                                         ;; Production building: only scores high
                                         ;; if matching plantations exist
                                         (if (contains? plantation-goods good)
                                           (* decay 0.85)
                                           (* decay 0.25))
                                         ;; Non-production building → utility
                                         (* decay 0.45)))
                                     :place-plantation
                                     (if (contains? prod-building-goods plantation)
                                       ;; Paired plantation → feeds production
                                       (* decay 0.75)
                                       (if (= plantation :corn)
                                         ;; Corn → feeding value
                                         (* decay 0.55)
                                         ;; Unmatched plantation → useless
                                         (* decay 0.25))))]
                           [id (max 0.1 (min 0.9 raw))])))
                  legal)))

        ;; Settler: prefer taking plantations that match owned production
        ;; buildings.  Score is based on the shortfall between production
        ;; capacity (sum of :worker slots) and owned plantation count per
        ;; good type.
        :settler
        (let [;; Total production-worker slots per good type
              production-slots (reduce (fn [m b]
                                         (if-let [good (production-good (:type b))]
                                           (update m good (fnil + 0)
                                                   (get-in state/buildings [(:type b) :worker] 1))
                                           m))
                                       {}
                                       (:buildings player))
              ;; Plantation count per type
              plantation-count (reduce (fn [m p]
                                         (update m (:type p) (fnil + 0) 1))
                                       {}
                                       (:plantations player))
              ;; Shortfall per good: max(0, capacity - count)
              shortfall (fn [good]
                          (let [cap (get production-slots good 0)
                                cnt (get plantation-count good 0)]
                            (max 0 (- cap cnt))))
              legal (legal-action-ids game-state)]
          (when-not (= legal [pass-id])
            (into {}
                  (map (fn [id]
                         (let [{:keys [kind plantation]} (nth action-table id)]
                           [id (case kind
                                 :settler-take
                                 (let [good (or plantation :quarry)
                                       cap (get production-slots good 0)
                                       sf (shortfall good)]
                                   (if (pos? cap)
                                     ;; Matching production exists: score by shortfall
                                     ;; using sigmoid curve so large absolute
                                     ;; shortfalls score higher than small ones
                                     (if (pos? sf)
                                       (+ 0.5 (* 0.35 (/ (double sf)
                                                         (+ 1.0 (double sf)))))
                                       0.3)
                                     ;; No matching production: low score
                                     0.2))
                                 :settler-hacienda
                                 0.5
                                 ;; pass
                                 0.5)])))
                  legal)))

        ;; Builder: prefer production buildings that match owned plantations.
        :builder
        (let [owned-plantation-types (set (map :type (:plantations player)))
              legal (legal-action-ids game-state)]
          (when-not (= legal [pass-id])
            (into {}
                  (map (fn [id]
                         (let [{:keys [kind building]} (nth action-table id)]
                           (if (= kind :build)
                             (let [good (production-good building)]
                               [id (if (and good (contains? owned-plantation-types good))
                                     0.8
                                     0.2)])
                             [id 0.5])))) ;; pass or non-build
                  legal)))

        ;; Other roles: no heuristic priors yet.
        nil))))
