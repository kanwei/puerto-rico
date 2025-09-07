(ns puerto-rico.game.rules
  "Puerto Rico game rules implementation"
  (:require [puerto-rico.game.state :as state]))

;; Forward declarations
(declare end-role-execution end-round execute-role)

;; Role execution functions

(defn execute-settler [game-state player-id plantation-choice]
  "Execute the settler role - player gets to take a plantation"
  (let [player-idx (->> (:players game-state)
                        (map-indexed vector)
                        (filter #(= (:id (second %)) player-id))
                        first
                        first)
        available-plantations (keys (filter #(pos? (val %)) (:plantation-supply game-state)))]
    (if (and plantation-choice
             (contains? (set available-plantations) plantation-choice)
             player-idx
             (>= player-idx 0)
             (< player-idx (count (:players game-state))))
      (-> game-state
          (update-in [:players player-idx :plantations] conj plantation-choice)
          (update-in [:plantation-supply plantation-choice] dec))
      game-state)))

(defn execute-mayor [game-state]
  "Execute the mayor role - distribute colonists"
  (let [total-colonists (:colonist-supply game-state)
        num-players (count (:players game-state))
        colonists-per-player (min 1 (quot total-colonists num-players))
        remaining-colonists (- total-colonists (* colonists-per-player num-players))]
    (-> game-state
        (update :players
                (fn [players]
                  (mapv #(update % :colonists
                                 (fn [current]
                                   (vec (concat current
                                                (repeat colonists-per-player :colonist)))))
                        players)))
        (update :colonist-supply - (* colonists-per-player num-players)))))

(defn can-build-building? [player building-key building-info]
  "Check if player can afford to build a building"
  (and (>= (:money player) (:cost building-info))
       (not (contains? (set (:buildings player)) building-key))
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
          (update-in [:players player-idx :buildings] conj building-choice)
          (update-in [:players player-idx :money] - (:cost building-info)))
      game-state)))

(defn execute-craftsman [game-state]
  "Execute the craftsman role - produce goods"
  ;; Simplified version to avoid null pointer issues for now
  (println "Craftsman role executed (simplified version)")
  game-state)

(defn can-trade-good? [game-state player good]
  "Check if player can trade a specific good"
  (and (pos? (get-in player [:goods good] 0))
       (not (contains? (set (map :good (:trading-house game-state))) good))
       (< (count (:trading-house game-state)) 4)))

(defn execute-trader [game-state player-id good-choice]
  "Execute the trader role - sell goods to trading house"
  (let [player-idx (->> (:players game-state)
                        (map-indexed vector)
                        (filter #(= (:id (second %)) player-id))
                        first
                        first)
        player (get-in game-state [:players player-idx])
        trade-value (case good-choice
                      :corn 0
                      :indigo 1
                      :sugar 2
                      :tobacco 3
                      :coffee 4
                      0)]
    (if (and good-choice (can-trade-good? game-state player good-choice))
      (-> game-state
          (update-in [:players player-idx :goods good-choice] dec)
          (update-in [:players player-idx :money] + trade-value)
          (update :trading-house conj {:good good-choice :player-id player-id}))
      game-state)))

(defn find-ship-for-good [ships good amount]
  "Find appropriate ship for shipping goods"
  (->> ships
       (map-indexed vector)
       (filter (fn [[idx ship]]
                 (or (nil? (:good ship))
                     (= (:good ship) good))))
       (filter (fn [[idx ship]]
                 (>= (- (:capacity ship) (:amount ship)) amount)))
       (sort-by (fn [[idx ship]] (:capacity ship)))
       first))

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
            actual-amount (min amount-to-ship (- (:capacity ship) (:amount ship)))]
        (-> game-state
            (update-in [:players player-idx :goods good-choice] - actual-amount)
            (update-in [:players player-idx :victory-points] + actual-amount)
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
          execution-order (state/create-role-execution-order game-state selector-idx)]
      (-> game-state
          (assoc :selected-role role)
          (assoc :role-selector-idx selector-idx)
          (assoc :role-execution-order execution-order)
          (assoc :role-execution-current-idx (first execution-order))
          (assoc :phase :role-execution)
          (update :available-roles disj role)
          (update :used-roles conj role)))
    game-state))

(defn advance-role-execution [game-state]
  "Move to the next player in role execution order, or end role if all players have executed"
  (let [execution-order (:role-execution-order game-state)
        current-idx (:role-execution-current-idx game-state)
        current-position (.indexOf execution-order current-idx)
        next-position (inc current-position)]
    (if (>= next-position (count execution-order))
      ;; All players have executed the role, move to next role selection
      (-> game-state
          (assoc :phase :role-selection)
          (assoc :selected-role nil)
          (assoc :role-selector-idx nil)
          (assoc :role-execution-order nil)
          (assoc :role-execution-current-idx nil)
          (assoc :current-player-idx (state/next-player-idx game-state)))
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
    :prospector (update-in game-state [:players (:role-player-idx game-state) :money] inc)
    game-state))

;; Game phase management
(defn end-role-execution [game-state]
  "End current role execution and advance game state"
  (-> game-state
      (assoc :selected-role nil)
      (assoc :role-player-idx nil)
      (assoc :phase :role-selection)))

(defn end-round [game-state]
  "End current round and prepare for next"
  (let [all-roles-used? (= (count (:used-roles game-state)) (count state/roles))]
    (if all-roles-used?
      (-> game-state
          (update :round inc)
          (assoc :available-roles (set state/roles))
          (assoc :used-roles #{})
          (assoc :current-player-idx 0)
          (assoc :phase :role-selection)
          ;; Clear trading house
          (assoc :trading-house [])
          ;; Reset ships if full
          (update :ships (fn [ships]
                           (mapv (fn [ship]
                                   (if (= (:amount ship) (:capacity ship))
                                     (assoc ship :good nil :amount 0)
                                     ship))
                                 ships))))
      (state/advance-to-next-player game-state))))

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
                     (end-role-execution)
                     (end-round))
    game-state))
