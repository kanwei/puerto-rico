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

(defn legal-action-ids
  "Vector of legal action ids for the player who must act now.
   Empty when the game is over."
  [game-state]
  (cond
    (:game-over game-state) []

    (= (:phase game-state) :role-selection)
    (mapv #(action-id {:kind :select-role :role %})
          (filter (:available-roles game-state) role-order))

    (= (:phase game-state) :role-execution)
    (let [player-idx (actor-index game-state)
          player (nth (:players game-state) player-idx)]
      (case (:selected-role game-state)
        :settler
        (if (rules/island-full? player)
          [pass-id]
          (let [takes (map #(action-id {:kind :settler-take :plantation %})
                           (distinct (:face-up-plantations game-state)))
                quarry (when (and (pos? (:quarry-supply game-state))
                                  (rules/may-take-quarry? game-state player-idx))
                         [(action-id {:kind :settler-take :plantation :quarry})])
                hacienda (when (and (rules/has-occupied-building? player :hacienda)
                                    (not (get-in game-state [:hacienda-used player-idx]))
                                    (seq (:plantation-supply game-state)))
                           [(action-id {:kind :settler-hacienda})])]
            (vec (concat takes quarry hacienda [pass-id]))))

        :builder
        (let [builds (map #(action-id {:kind :build :building %})
                          (filter #(rules/can-build-building? game-state player %)
                                  building-order))]
          (vec (concat builds [pass-id])))

        :trader
        (let [trades (map #(action-id {:kind :trade :good %})
                          (filter #(rules/can-trade-good? game-state player %)
                                  good-order))]
          (vec (concat trades [pass-id])))

        :captain
        (if (:storage-phase game-state)
          ;; Storage sub-phase: keep kinds / a single, or done (always legal)
          (let [{:keys [kinds singles]} (rules/legal-storage-picks game-state player-idx)]
            (vec (concat (map #(action-id {:kind :store-kind :good %}) (sort-by good-order-index kinds))
                         (map #(action-id {:kind :store-single :good %}) (sort-by good-order-index singles))
                         [pass-id])))
          ;; Loading turns
          (let [ships (map #(action-id {:kind :ship :good %})
                           (filter #(and (pos? (get-in player [:goods %] 0))
                                         (rules/find-ship-for-good (:ships game-state) %
                                                                   (get-in player [:goods %] 0)))
                                   good-order))
                wharfs (when (rules/can-use-wharf? game-state player-idx)
                         (map #(action-id {:kind :wharf :good %})
                              (filter #(pos? (get-in player [:goods %] 0)) good-order)))]
            ;; Loading on a cargo ship is mandatory when possible; the wharf may
            ;; substitute. Passing is only legal when no cargo-ship load exists.
            (if (seq ships)
              (vec (concat ships wharfs))
              (vec (concat wharfs [pass-id])))))

        :mayor
        ;; Placement turns: place from hand until it's empty or nothing is
        ;; placeable; only then is "done" legal
        (let [{:keys [plantations buildings]} (rules/placement-destinations player)
              hand (:colonists-in-hand player)
              places (when (pos? hand)
                       (concat
                        (map #(action-id {:kind :place-plantation :plantation %})
                             (filter plantations plantation-order))
                        (map #(action-id {:kind :place-building :building %})
                             (filter buildings building-order))))]
          (if (seq places)
            (vec places)
            [pass-id]))

        :craftsman
        ;; After production the selector may face a privilege choice
        (if-let [candidates (seq (:craftsman-privilege-pending game-state))]
          (mapv #(action-id {:kind :privilege-good :good %})
                (sort-by good-order-index candidates))
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
