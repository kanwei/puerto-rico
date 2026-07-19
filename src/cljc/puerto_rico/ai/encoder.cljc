(ns puerto-rico.ai.encoder
  "Flatten a game state into a fixed-size vector of floats in [0, 1] for the
   neural network.

   Egocentric perspective: seat 0 of the encoding is always the player who must
   act now, seat 1 the next player clockwise, and so on. This gives the network
   weight sharing across seats - it learns 'my coffee roaster' once instead of
   once per seat. The value head is trained against outcomes rotated the same
   way (index 0 = the acting player's result)."
  (:require [puerto-rico.game.state :as state]
            [puerto-rico.ai.actions :as actions]))

(defn- clamp01 [x] (max 0.0 (min 1.0 (double x))))

(defn- norm [x by] (clamp01 (/ (double (or x 0)) (double by))))

(defn- one-hot [n i]
  (mapv #(if (= % i) 1.0 0.0) (range n)))

(defn- flag [b] (if b 1.0 0.0))

(def ^:private good-index
  (into {} (map-indexed (fn [i g] [g i]) actions/good-order)))

;; --------------------------------------------------------------------------
;; Per-player block: 66 floats
;; --------------------------------------------------------------------------

(defn- encode-player [player]
  (let [plantations (:plantations player)
        buildings (:buildings player)
        building-by-type (into {} (map (juxt :type identity) buildings))]
    (-> []
        (conj (norm (:money player) 20))
        (conj (norm (:victory-points player) 60))
        (into (map #(norm (get-in player [:goods %]) 8) actions/good-order))
        (conj (norm (:san-juan-colonists player) 10))
        (conj (norm (:colonists-in-hand player) 15))
        ;; 6 plantation types x [count owned, count manned]
        (into (mapcat (fn [ptype]
                        (let [tiles (filter #(= (:type %) ptype) plantations)]
                          [(norm (count tiles) 6)
                           (norm (count (filter #(pos? (:colonists %)) tiles)) 6)]))
                      actions/plantation-order))
        ;; 23 building types x [owned?, colonists/capacity]
        (into (mapcat (fn [btype]
                        (if-let [b (building-by-type btype)]
                          [1.0 (norm (:colonists b)
                                     (get-in state/buildings [btype :worker] 1))]
                          [0.0 0.0]))
                      actions/building-order)))))

;; --------------------------------------------------------------------------
;; Global block
;; --------------------------------------------------------------------------

(defn- seat-offset
  "Offset of seat idx clockwise from the acting player (0 = the actor)"
  [actor-idx idx n]
  (mod (- idx actor-idx) n))

(defn- encode-global [game-state actor-idx n]
  (let [phase-key (cond
                    (= (:phase game-state) :role-selection) :selecting
                    (:storage-phase game-state) :storage
                    (:craftsman-privilege-pending game-state) :privilege
                    :else (case (:selected-role game-state)
                            :settler :settler
                            :builder :builder
                            :trader :trader
                            :captain :captain
                            :mayor :mayor
                            :other))
        selector-offset (when-let [s (:role-selector-idx game-state)]
                          (seat-offset actor-idx s n))
        actor (nth (:players game-state) actor-idx)
        storage-picks (get-in game-state [:storage-picks actor-idx])]
    (-> []
        (conj (norm (:round game-state) 20))
        ;; decision type one-hot (9)
        (into (one-hot 9 ({:selecting 0 :settler 1 :builder 2 :trader 3
                           :captain 4 :mayor 5 :storage 6 :privilege 7 :other 8}
                          phase-key)))
        ;; 8 role slots x [in this game?, available?, gold]
        (into (mapcat (fn [r]
                        [(flag (some #{r} (:roles game-state)))
                         (flag (contains? (:available-roles game-state) r))
                         (norm (get-in game-state [:role-gold r] 0) 3)])
                      actions/role-order))
        ;; governor / selector seats relative to the actor
        (into (one-hot n (seat-offset actor-idx (:governor-idx game-state) n)))
        (into (one-hot (inc n) (or selector-offset n)))
        ;; plantation display
        (into (map (fn [p] (norm (count (filter #{p} (:face-up-plantations game-state))) 4))
                   actions/good-order))
        (conj (norm (:quarry-supply game-state) 8))
        (conj (norm (count (:plantation-supply game-state)) 24))
        (conj (norm (count (:plantation-discard game-state)) 24))
        ;; colonists and VP
        (conj (norm (:colonist-ship game-state) 12))
        (conj (norm (:colonist-supply game-state) 95))
        (conj (norm (:victory-point-supply game-state) 126))
        ;; building supply per type
        (into (map (fn [b] (norm (get-in game-state [:building-supply b] 0)
                                 (get-in state/buildings [b :count] 1)))
                   actions/building-order))
        ;; trading house: count per good + fill level
        (into (map (fn [g] (norm (count (filter #(= (:good %) g) (:trading-house game-state))) 2))
                   actions/good-order))
        (conj (norm (count (:trading-house game-state)) 4))
        ;; 3 cargo ships x [capacity, fill, good one-hot(6: empty+5)]
        (into (mapcat (fn [ship]
                        (concat [(norm (:capacity ship) 8)
                                 (norm (:amount ship) (max 1 (:capacity ship)))]
                                (one-hot 6 (if-let [g (:good ship)]
                                             (inc (good-index g))
                                             0))))
                      (:ships game-state)))
        ;; captain-phase context
        (conj (flag (:captain-bonus-awarded game-state)))
        (conj (norm (:captain-passes game-state) n))
        (into (map (fn [k] (flag (contains? (get game-state :wharf-used #{})
                                            (mod (+ actor-idx k) n))))
                   (range n)))
        ;; actor's hacienda flag
        (conj (flag (get-in game-state [:hacienda-used actor-idx])))
        ;; actor's storage picks so far (kinds kept + windrose single)
        (into (map (fn [g] (flag (contains? (:kinds storage-picks) g)))
                   actions/good-order))
        (into (one-hot 6 (if-let [s (:single storage-picks)]
                           (inc (good-index s))
                           0)))
        ;; craftsman privilege candidates (kinds the selector produced)
        (into (map (fn [g] (flag (contains? (get game-state :craftsman-privilege-pending #{}) g)))
                   actions/good-order)))))

;; --------------------------------------------------------------------------
;; Public API
;; --------------------------------------------------------------------------

(defn encode-state
  "Encode the game state as a flat vector of floats from the perspective of the
   player who must act now."
  [game-state]
  (let [n (count (:players game-state))
        actor-idx (actions/actor-index game-state)]
    (-> []
        (into (mapcat (fn [k]
                        (encode-player (nth (:players game-state)
                                            (mod (+ actor-idx k) n))))
                      (range n)))
        (into (encode-global game-state actor-idx n)))))

(defn encoded-size
  "Input width for a game with n players (3 players: 328)"
  [n]
  (+ (* 67 n)          ;; per-player blocks
     1 9 24            ;; round, decision type, role slots
     n (inc n)         ;; governor + selector offsets
     5 1 1 1           ;; plantation display
     3                 ;; colonist ship/supply, VP supply
     23                ;; building supply
     6                 ;; trading house
     24                ;; ships
     1 1 n             ;; captain flags
     1                 ;; hacienda flag
     5 6               ;; storage picks (kinds + single)
     5))               ;; craftsman privilege candidates
