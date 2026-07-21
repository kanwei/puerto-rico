(ns puerto-rico.ai.encoder
  "Flatten a game state into a fixed-size vector of floats in [0, 1] for the
   neural network.

   Egocentric perspective: seat 0 of the encoding is always the player who must
   act now, seat 1 the next player clockwise, and so on. This gives the network
   weight sharing across seats - it learns 'my coffee roaster' once instead of
   once per seat. The value head is trained against outcomes rotated the same
   way (index 0 = the acting player's result).

   Hot path: called on every neural-network leaf evaluation. It accumulates into
   a single transient vector and derives per-type plantation/trading-house
   counts in one pass each, instead of the repeated filter/count scans and
   per-call map rebuilds an idiomatic version would do."
  (:require [puerto-rico.game.state :as state]
            [puerto-rico.ai.actions :as actions]))

(defn- clamp01 [x] (max 0.0 (min 1.0 (double x))))

(def ^:private good-index
  (into {} (map-indexed (fn [i g] [g i]) actions/good-order)))

;; --------------------------------------------------------------------------
;; Transient-vector push helpers. Each takes the transient accumulator first
;; and returns it, so they thread through `as->` / `reduce`.
;; --------------------------------------------------------------------------

(defn- pv! [acc x] (conj! acc (double x)))
(defn- pn! [acc x by] (conj! acc (clamp01 (/ (double (or x 0)) (double by)))))
(defn- pf! [acc b] (conj! acc (if b 1.0 0.0)))
(defn- poh!
  "Push a one-hot of length n with a 1.0 at index i."
  [acc n i]
  (loop [k 0, a acc]
    (if (< k n) (recur (inc k) (conj! a (if (= k i) 1.0 0.0))) a)))

;; --------------------------------------------------------------------------
;; Per-player block: 67 floats
;; --------------------------------------------------------------------------

(defn- encode-player! [acc player]
  (let [;; one pass over the plantation tiles -> {type [total worked]}
        pcounts (reduce (fn [m t]
                          (let [ty (:type t)
                                [n w] (get m ty [0 0])]
                            (assoc m ty [(inc n) (if (pos? (:colonists t 0)) (inc w) w)])))
                        {} (:plantations player))
        ;; one map of the owned buildings by type (avoids scanning per lookup)
        bbt (reduce (fn [m b] (assoc m (:type b) b)) {} (:buildings player))
        goods (:goods player)]
    (as-> acc a
      ;; Money ceiling 40: players can hoard well past 20 in slow games, and
      ;; clamping there hid the difference between a rich and a very rich player.
      (pn! a (:money player) 40)
      (pn! a (:victory-points player) 60)
      (reduce (fn [a g] (pn! a (get goods g) 8)) a actions/good-order)
      (pn! a (:san-juan-colonists player) 10)
      (pn! a (:colonists-in-hand player) 15)
      ;; 6 plantation types x [count owned, count manned]
      (reduce (fn [a ptype]
                (let [[n w] (get pcounts ptype [0 0])]
                  (pn! (pn! a n 6) w 6)))
              a actions/plantation-order)
      ;; 23 building types x [owned?, colonists/capacity]
      (reduce (fn [a btype]
                (if-let [b (bbt btype)]
                  (pn! (pv! a 1.0) (:colonists b) (get-in state/buildings [btype :worker] 1))
                  (pv! (pv! a 0.0) 0.0)))
              a actions/building-order))))

;; --------------------------------------------------------------------------
;; Global block
;; --------------------------------------------------------------------------

(defn- seat-offset
  "Offset of seat idx clockwise from the acting player (0 = the actor)"
  [actor-idx idx n]
  (mod (- idx actor-idx) n))

(defn- encode-global! [acc game-state actor-idx n]
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
        storage-picks (get-in game-state [:storage-picks actor-idx])
        kinds (:kinds storage-picks)
        roles (:roles game-state)
        available (:available-roles game-state)
        ;; single-pass frequency maps replace the per-good filter/count scans
        face-freq (frequencies (:face-up-plantations game-state))
        th (:trading-house game-state)
        th-freq (reduce (fn [m item] (update m (:good item) (fnil inc 0))) {} th)
        wharf-used (get game-state :wharf-used #{})
        priv (get game-state :craftsman-privilege-pending #{})]
    (as-> acc a
      ;; decision type one-hot (9). (The round number is deliberately NOT encoded:
      ;; the game-ending triggers are already fed in below - VP/colonist supply,
      ;; building slots, and the final-round flag - so the net reads the real
      ;; clock instead of overfitting to a round counter.)
      (poh! a 9 ({:selecting 0 :settler 1 :builder 2 :trader 3
                  :captain 4 :mayor 5 :storage 6 :privilege 7 :other 8}
                 phase-key))
      ;; 8 role slots x [in this game?, available?, gold]. Gold ceiling 5: a
      ;; long-ignored prospector/role can stack more than 3 doubloons.
      (reduce (fn [a r]
                (-> a
                    (pf! (some #{r} roles))
                    (pf! (contains? available r))
                    (pn! (get-in game-state [:role-gold r] 0) 5)))
              a actions/role-order)
      ;; governor / selector seats relative to the actor
      (poh! a n (seat-offset actor-idx (:governor-idx game-state) n))
      (poh! a (inc n) (or selector-offset n))
      ;; plantation display. (The discard-pile size is deliberately NOT encoded:
      ;; its magnitude says nothing actionable about what plantations remain, so
      ;; it was pure noise for a Markov-state network.)
      (reduce (fn [a p] (pn! a (get face-freq p 0) 4)) a actions/good-order)
      (pn! a (:quarry-supply game-state) 8)
      (pn! a (count (:plantation-supply game-state)) 24)
      ;; colonists and VP, each scaled by this player count's starting pool so
      ;; that 0.0 means "the pool is empty" (a real game-ending trigger).
      (pn! a (:colonist-ship game-state) 12)
      (pn! a (:colonist-supply game-state) (state/starting-colonist-supply n))
      (pn! a (:victory-point-supply game-state) (state/starting-victory-point-supply n))
      ;; final-round flag: a game-ending condition (full city, colonist-ship
      ;; shortfall, or VP supply exhausted) has already been met, so the current
      ;; round is the last one.
      (pf! a (state/check-victory-conditions game-state))
      ;; building supply per type
      (reduce (fn [a b] (pn! a (get-in game-state [:building-supply b] 0)
                             (get-in state/buildings [b :count] 1)))
              a actions/building-order)
      ;; trading house: count per good + fill level
      (reduce (fn [a g] (pn! a (get th-freq g 0) 2)) a actions/good-order)
      (pn! a (count th) 4)
      ;; 3 cargo ships x [capacity, fill, good one-hot(6: empty+5)]
      (reduce (fn [a ship]
                (-> a
                    (pn! (:capacity ship) 8)
                    (pn! (:amount ship) (max 1 (:capacity ship)))
                    (poh! 6 (if-let [g (:good ship)] (inc (good-index g)) 0))))
              a (:ships game-state))
      ;; captain-phase context
      (pf! a (:captain-bonus-awarded game-state))
      (pn! a (:captain-passes game-state) n)
      (reduce (fn [a k] (pf! a (contains? wharf-used (mod (+ actor-idx k) n)))) a (range n))
      ;; actor's hacienda flag
      (pf! a (get-in game-state [:hacienda-used actor-idx]))
      ;; actor's storage picks so far (kinds kept + windrose single)
      (reduce (fn [a g] (pf! a (contains? kinds g))) a actions/good-order)
      (poh! a 6 (if-let [s (:single storage-picks)] (inc (good-index s)) 0))
      ;; craftsman privilege candidates (kinds the selector produced)
      (reduce (fn [a g] (pf! a (contains? priv g))) a actions/good-order))))

;; --------------------------------------------------------------------------
;; Public API
;; --------------------------------------------------------------------------

(defn encode-state
  "Encode the game state as a flat vector of floats from the perspective of the
   player who must act now."
  [game-state]
  (let [n (count (:players game-state))
        actor-idx (actions/actor-index game-state)
        players (:players game-state)]
    (persistent!
     (as-> (transient []) acc
       (reduce (fn [a k] (encode-player! a (nth players (mod (+ actor-idx k) n))))
               acc (range n))
       (encode-global! acc game-state actor-idx n)))))

(defn encoded-size
  "Input width for a game with n players (2 players: 249, 3 players: 327)"
  [n]
  (+ (* 67 n)          ;; per-player blocks
     9 24              ;; decision type, role slots
     n (inc n)         ;; governor + selector offsets
     5 1 1             ;; plantation display (per-good, quarry, deck size)
     3 1               ;; colonist ship/supply, VP supply, final-round flag
     23                ;; building supply
     6                 ;; trading house
     (* 8 (state/num-cargo-ships n)) ;; ships (2p: 2 ships, otherwise 3)
     1 1 n             ;; captain flags
     1                 ;; hacienda flag
     5 6               ;; storage picks (kinds + single)
     5))               ;; craftsman privilege candidates
