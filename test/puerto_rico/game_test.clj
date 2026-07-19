(ns puerto-rico.game-test
  "Rule tests pinned to the Rio Grande deluxe rulebook."
  (:require [clojure.test :refer [deftest is testing]]
            [puerto-rico.game.state :as state]
            [puerto-rico.game.rules :as rules]
            [puerto-rico.ai.heuristic :as ai]))

;; ================================================================================
;; Helpers
;; ================================================================================

(defn mk-players [n]
  (mapv #(state/new-player (inc %) (str "P" (inc %))) (range n)))

;; Default game = all AI seats (matches self-play; selects the swept mayor path)
(defn mk-game [n]
  (state/new-game-state (mapv #(assoc % :is-ai true) (mk-players n))))

;; Human game = no :is-ai (selects the keep-board interactive mayor path)
(defn human-game [n]
  (state/new-game-state (mk-players n)))

(defn set-player [game idx player-updates]
  (update-in game [:players idx] merge player-updates))

(def empty-goods {:corn 0 :indigo 0 :sugar 0 :tobacco 0 :coffee 0})

(defn goods [m] (merge empty-goods m))

;; ================================================================================
;; Setup
;; ================================================================================

(deftest setup-by-player-count
  (testing "3 players: 6 role placards, no prospector, 2 doubloons, 75 VP"
    (let [g (mk-game 3)]
      (is (= 6 (count (:roles g))))
      (is (not-any? #{:prospector :prospector-2} (:roles g)))
      (is (every? #(= 2 (:money %)) (:players g)))
      (is (= 75 (:victory-point-supply g)))
      (is (= [4 5 6] (map :capacity (:ships g))))
      (is (= 3 (:colonist-ship g)))))
  (testing "4 players: 7 placards with one prospector, 3 doubloons, 100 VP"
    (let [g (mk-game 4)]
      (is (= 7 (count (:roles g))))
      (is (some #{:prospector} (:roles g)))
      (is (not-any? #{:prospector-2} (:roles g)))
      (is (every? #(= 3 (:money %)) (:players g)))
      (is (= 100 (:victory-point-supply g)))))
  (testing "5 players: 8 placards with two prospectors, 4 doubloons, 126 VP"
    (let [g (mk-game 5)]
      (is (= 8 (count (:roles g))))
      (is (some #{:prospector} (:roles g)))
      (is (some #{:prospector-2} (:roles g)))
      (is (every? #(= 4 (:money %)) (:players g)))
      (is (= 126 (:victory-point-supply g))))))

(deftest building-costs
  (is (= 7 (get-in state/buildings [:factory :cost])))
  (is (= 8 (get-in state/buildings [:university :cost]))))

;; ================================================================================
;; Builder
;; ================================================================================

(deftest builder-discounts
  (testing "quarry discount plus builder privilege"
    (let [g (-> (mk-game 3)
                (set-player 0 {:money 5
                               :plantations [{:type :quarry :colonists 1}
                                             {:type :quarry :colonists 1}]})
                (assoc :selected-role :builder :role-selector-idx 0))
          g2 (rules/execute-builder g 1 :tobacco-maker)]
      ;; tobacco storage costs 5, column 3: -2 quarries, -1 privilege = 2
      (is (= 3 (get-in g2 [:players 0 :money])))
      (is (= 1 (count (get-in g2 [:players 0 :buildings]))))))
  (testing "quarry discount is capped by the building's column"
    (let [g (-> (mk-game 3)
                (set-player 0 {:money 10
                               :plantations (vec (repeat 4 {:type :quarry :colonists 1}))})
                (assoc :selected-role :builder :role-selector-idx 1))
          cost (rules/building-cost g (get-in g [:players 0]) :small-market)]
      ;; small market (cost 1, column 1): only 1 quarry counts, no privilege
      (is (= 0 cost))))
  (testing "cost floor is 0"
    (let [g (-> (mk-game 3)
                (set-player 0 {:money 10
                               :plantations (vec (repeat 3 {:type :quarry :colonists 1}))})
                (assoc :selected-role :builder :role-selector-idx 0))]
      ;; construction hut (cost 2, column 1): -1 quarry -1 privilege = 0
      (is (= 0 (rules/building-cost g (get-in g [:players 0]) :construction-hut)))))
  (testing "building supply is decremented"
    (let [g (-> (mk-game 3)
                (set-player 0 {:money 10})
                (assoc :selected-role :builder :role-selector-idx 1))
          g2 (rules/execute-builder g 1 :hospice)]
      (is (= 1 (get-in g2 [:building-supply :hospice])))))
  (testing "cannot build a duplicate"
    (let [g (-> (mk-game 3)
                (set-player 0 {:money 10 :buildings [{:type :hospice :colonists 0}]}))]
      (is (not (rules/can-build-building? g (get-in g [:players 0]) :hospice)))))
  (testing "large buildings need 2 city spaces"
    (let [eleven-buildings (vec (repeat 11 {:type :small-market :colonists 0}))
          g (-> (mk-game 3)
                (set-player 0 {:money 20 :buildings eleven-buildings}))]
      (is (not (rules/can-build-building? g (get-in g [:players 0]) :guild-hall)))
      (is (rules/can-build-building? g (get-in g [:players 0]) :office))))
  (testing "university grants a colonist on the new building"
    (let [g (-> (mk-game 3)
                (set-player 0 {:money 10
                               :buildings [{:type :university :colonists 1}]})
                (assoc :selected-role :builder :role-selector-idx 1))
          g2 (rules/execute-builder g 1 :hospice)
          new-building (last (get-in g2 [:players 0 :buildings]))]
      (is (= :hospice (:type new-building)))
      (is (= 1 (:colonists new-building))))))

;; ================================================================================
;; Craftsman
;; ================================================================================

(deftest craftsman-production
  (testing "multiple production buildings for one good sum their capacity"
    (let [g (-> (mk-game 3)
                (set-player 0 {:plantations (vec (repeat 3 {:type :indigo :colonists 1}))
                               :buildings [{:type :small-indigo-maker :colonists 1}
                                           {:type :large-indigo-maker :colonists 2}]})
                (assoc :role-selector-idx 1))
          g2 (rules/execute-craftsman g)]
      (is (= 3 (get-in g2 [:players 0 :goods :indigo])))))
  (testing "privilege is a separate choice when 2+ kinds were produced"
    (let [g (-> (mk-game 3)
                (set-player 0 {:plantations [{:type :corn :colonists 1}
                                             {:type :coffee :colonists 1}]
                               :buildings [{:type :coffee-maker :colonists 1}]})
                (assoc :role-selector-idx 0))
          ;; production alone: 1 corn + 1 coffee, then a pending privilege choice
          g2 (rules/execute-craftsman g)]
      (is (= 1 (get-in g2 [:players 0 :goods :corn])))
      (is (= 1 (get-in g2 [:players 0 :goods :coffee])))
      (is (= #{:corn :coffee} (:craftsman-privilege-pending g2)))
      ;; selector picks coffee as the extra good
      (let [g3 (rules/execute-craftsman-privilege g2 1 :coffee)]
        (is (= 2 (get-in g3 [:players 0 :goods :coffee])))
        (is (nil? (:craftsman-privilege-pending g3))))))
  (testing "privilege auto-resolves when only one kind was produced"
    (let [g (-> (mk-game 3)
                (set-player 0 {:plantations [{:type :corn :colonists 1}]})
                (assoc :role-selector-idx 0))
          g2 (rules/execute-craftsman g)]
      ;; 1 produced + 1 privilege, no pending decision
      (is (= 2 (get-in g2 [:players 0 :goods :corn])))
      (is (nil? (:craftsman-privilege-pending g2)))))
  (testing "factory pays for kinds produced"
    (let [g (-> (mk-game 3)
                (set-player 0 {:money 0
                               :plantations [{:type :corn :colonists 1}
                                             {:type :indigo :colonists 1}]
                               :buildings [{:type :small-indigo-maker :colonists 1}
                                           {:type :factory :colonists 1}]})
                (assoc :role-selector-idx 1))
          g2 (rules/execute-craftsman g)]
      ;; 2 kinds (corn + indigo) = 1 doubloon
      (is (= 1 (get-in g2 [:players 0 :money]))))))

;; ================================================================================
;; Trader
;; ================================================================================

(deftest trader-rules
  (testing "market bonuses stack and the selector gets the privilege"
    (let [g (-> (mk-game 3)
                (set-player 0 {:money 0
                               :goods (goods {:coffee 1})
                               :buildings [{:type :small-market :colonists 1}
                                           {:type :large-market :colonists 1}]})
                (assoc :role-selector-idx 0))
          g2 (rules/execute-trader g 1 :coffee)]
      ;; coffee 4 + small market 1 + large market 2 + privilege 1 = 8
      (is (= 8 (get-in g2 [:players 0 :money])))))
  (testing "trading house only buys different kinds, except with an occupied office"
    (let [base (-> (mk-game 3)
                   (assoc :trading-house [{:good :indigo :player-id 9}])
                   (assoc :role-selector-idx 2))
          without-office (set-player base 0 {:goods (goods {:indigo 1})})
          with-office (set-player base 0 {:goods (goods {:indigo 1})
                                          :buildings [{:type :office :colonists 1}]})]
      (is (not (rules/can-trade-good? without-office (get-in without-office [:players 0]) :indigo)))
      (is (rules/can-trade-good? with-office (get-in with-office [:players 0]) :indigo))))
  (testing "a full trading house blocks sales until the END of the trader phase"
    (let [g (-> (mk-game 3)
                (set-player 0 {:goods (goods {:tobacco 1})})
                (set-player 1 {:goods (goods {:indigo 1})})
                (assoc :trading-house [{:good :corn :player-id 9}
                                       {:good :sugar :player-id 9}
                                       {:good :coffee :player-id 9}])
                (assoc :selected-role :trader
                       :role-selector-idx 0
                       :role-execution-order [0 1 2]
                       :role-execution-current-idx 0
                       :phase :role-execution))
          ;; P1 fills the house to 4
          g2 (rules/execute-trader g 1 :tobacco)]
      (is (= 4 (count (:trading-house g2))))
      ;; P2 cannot sell into the full house mid-phase
      (is (not (rules/can-trade-good? g2 (get-in g2 [:players 1]) :indigo)))
      (is (= (get-in g2 [:players 1 :money])
             (get-in (rules/execute-trader g2 2 :indigo) [:players 1 :money])))
      ;; At the end of the phase the full house is emptied back to the supply
      (let [g3 (rules/end-role-execution g2)]
        (is (empty? (:trading-house g3))))))
  (testing "an office may sell a duplicate kind; a full house with a duplicate still clears"
    (let [base (-> (mk-game 3)
                   (assoc :trading-house [{:good :indigo :player-id 9}])
                   (assoc :role-selector-idx 2))]
      ;; without an office a duplicate is illegal
      (is (not (rules/can-trade-good? base (get-in (set-player base 0 {:goods (goods {:indigo 1})})
                                                   [:players 0]) :indigo)))
      ;; with an occupied office it is allowed
      (let [g (set-player base 0 {:goods (goods {:indigo 1})
                                  :buildings [{:type :office :colonists 1}]})]
        (is (rules/can-trade-good? g (get-in g [:players 0]) :indigo)))
      ;; a full house that is full via a duplicate (3 kinds, 4 goods) must still clear
      (let [g (-> (mk-game 3)
                  (assoc :trading-house [{:good :sugar :player-id 9} {:good :coffee :player-id 9}
                                         {:good :indigo :player-id 9} {:good :indigo :player-id 9}]
                         :selected-role :trader :role-selector-idx 0
                         :role-execution-order [0 1 2] :role-execution-current-idx 2
                         :phase :role-execution :players-selected-this-round 1)
                  (set-player 1 {:goods (goods {:corn 1})}))
            supply-before (get-in g [:goods-supply :indigo])
            g2 (rules/end-role-execution g)]
        (is (empty? (:trading-house g2)))
        (is (= (+ supply-before 2) (get-in g2 [:goods-supply :indigo])))))))

;; ================================================================================
;; Captain
;; ================================================================================

(defn captain-game
  "3-player game in captain phase; selector is player index 0."
  [& {:keys [ships] :or {ships [{:capacity 4 :good nil :amount 0}
                                {:capacity 5 :good nil :amount 0}
                                {:capacity 6 :good nil :amount 0}]}}]
  (-> (mk-game 3)
      (assoc :ships ships
             :selected-role :captain
             :role-selector-idx 0
             :role-execution-order [0 1 2]
             :role-execution-current-idx 0
             :phase :role-execution)))

(deftest captain-ship-selection
  (testing "partial load on the biggest empty ship when nothing fits everything"
    (let [g (-> (captain-game)
                (set-player 0 {:goods (goods {:corn 8})}))
          g2 (rules/execute-captain g 1 :corn)]
      ;; 8 corn, biggest empty ship holds 6: load 6, keep 2
      (is (= 2 (get-in g2 [:players 0 :goods :corn])))
      (is (= {:capacity 6 :good :corn :amount 6} (nth (:ships g2) 2)))
      ;; 6 base VP + 1 captain privilege
      (is (= 7 (get-in g2 [:players 0 :victory-points])))))
  (testing "smallest empty ship that fits everything"
    (let [g (-> (captain-game)
                (set-player 0 {:goods (goods {:corn 4})}))
          g2 (rules/execute-captain g 1 :corn)]
      (is (= {:capacity 4 :good :corn :amount 4} (nth (:ships g2) 0)))))
  (testing "a ship already carrying the good MUST be used"
    (let [g (-> (captain-game :ships [{:capacity 4 :good :corn :amount 2}
                                      {:capacity 5 :good nil :amount 0}
                                      {:capacity 6 :good nil :amount 0}])
                (set-player 0 {:goods (goods {:corn 5})}))
          g2 (rules/execute-captain g 1 :corn)]
      ;; only 2 spaces left on the corn ship
      (is (= 3 (get-in g2 [:players 0 :goods :corn])))
      (is (= 4 (get-in (:ships g2) [0 :amount])))))
  (testing "a full ship carrying the good means the good cannot be shipped"
    (let [ships [{:capacity 4 :good :corn :amount 4}
                 {:capacity 5 :good nil :amount 0}
                 {:capacity 6 :good nil :amount 0}]
          g (-> (captain-game :ships ships)
                (set-player 0 {:goods (goods {:corn 3})
                               :buildings [{:type :harbor :colonists 1}]}))
          g2 (rules/execute-captain g 1 :corn)]
      (is (nil? (rules/find-ship-for-good ships :corn 3)))
      ;; no load happened: no goods moved, no VP (not even harbor/privilege)
      (is (= 3 (get-in g2 [:players 0 :goods :corn])))
      (is (= 0 (get-in g2 [:players 0 :victory-points])))))
  (testing "captain privilege is +1 VP once; harbor is +1 VP per load"
    (let [g (-> (captain-game)
                (set-player 0 {:goods (goods {:corn 2 :indigo 3})
                               :buildings [{:type :harbor :colonists 1}]}))
          g2 (rules/execute-captain g 1 :corn)   ;; 2 + harbor 1 + privilege 1
          g3 (rules/execute-captain g2 1 :indigo)] ;; 3 + harbor 1
      (is (= 4 (get-in g2 [:players 0 :victory-points])))
      (is (= 8 (get-in g3 [:players 0 :victory-points]))))))

(deftest captain-wharf
  (testing "wharf ships all goods of one kind to the supply, once per phase"
    (let [g (-> (captain-game :ships [{:capacity 4 :good :corn :amount 4}
                                      {:capacity 5 :good :indigo :amount 5}
                                      {:capacity 6 :good :sugar :amount 6}])
                (set-player 1 {:goods (goods {:tobacco 5 :coffee 2})
                               :buildings [{:type :wharf :colonists 1}]}))
          supply-before (get-in g [:goods-supply :tobacco])
          g2 (rules/execute-captain g 2 :tobacco :wharf)]
      (is (= 0 (get-in g2 [:players 1 :goods :tobacco])))
      (is (= 5 (get-in g2 [:players 1 :victory-points])))
      (is (= (+ supply-before 5) (get-in g2 [:goods-supply :tobacco])))
      ;; second use in the same phase is rejected
      (is (identical? g2 (rules/execute-captain g2 2 :coffee :wharf))))))

(deftest captain-phase-loops
  (testing "players get multiple loading turns until nobody can load"
    (let [g (-> (captain-game)
                (set-player 0 {:goods (goods {:corn 2 :indigo 3})}))
          ;; P1 loads corn (P2 and P3 have nothing, get skipped), P1 loads again
          g2 (rules/apply-move g {:type :role-action :role :captain :player-id 1 :args [:corn]})]
      (is (= :role-execution (:phase g2)))
      (is (= 0 (:role-execution-current-idx g2)))
      (let [g3 (rules/apply-move g2 {:type :role-action :role :captain :player-id 1 :args [:indigo]})]
        ;; after the second load nobody can act - phase over
        (is (not= :role-execution (:phase g3)))
        (is (= 0 (get-in g3 [:players 0 :goods :corn])))
        (is (= 0 (get-in g3 [:players 0 :goods :indigo])))
        ;; 2 + privilege 1 + 3
        (is (= 6 (get-in g3 [:players 0 :victory-points])))))))

(defn full-ships
  "Ships completely filled with other goods so nothing can be loaded"
  []
  [{:capacity 4 :good :indigo :amount 4}
   {:capacity 5 :good :sugar :amount 5}
   {:capacity 6 :good :tobacco :amount 6}])

(defn- end-loading
  "Current player cannot load - passing ends the loading phase (everyone else
   is skipped) and begins storage"
  [g]
  (rules/apply-move g {:type :role-action :role :captain
                       :player-id (:id (nth (:players g) (:role-execution-current-idx g)))
                       :args []}))

(defn- storage-move [g args]
  (rules/apply-move g {:type :role-action :role :captain
                       :player-id (:id (nth (:players g) (:role-execution-current-idx g)))
                       :args args}))

(deftest captain-storage
  (testing "no warehouse: the player chooses which single good to keep"
    (let [g (-> (captain-game :ships (full-ships))
                (set-player 0 {:goods (goods {:corn 3 :coffee 2})}))
          g2 (end-loading g)]
      ;; storage sub-phase reached, P1 must decide
      (is (:storage-phase g2))
      (is (= 0 (:role-execution-current-idx g2)))
      ;; keep a single corn (against value order - it's the player's choice)
      (let [g3 (storage-move g2 [:store-single :corn])]
        (is (= (goods {:corn 1}) (get-in g3 [:players 0 :goods])))
        ;; trivial players auto-resolved, role over
        (is (not (:storage-phase g3)))
        (is (= :role-selection (:phase g3))))))
  (testing "small warehouse: all of ONE chosen kind plus one single good"
    (let [g (-> (captain-game :ships (full-ships))
                (set-player 0 {:goods (goods {:corn 3 :coffee 2 :indigo 1})
                               :buildings [{:type :small-warehouse :colonists 1}]}))
          g2 (end-loading g)
          g3 (-> g2
                 (storage-move [:store-kind :corn])
                 (storage-move [:store-single :coffee]))]
      (is (= (goods {:corn 3 :coffee 1}) (get-in g3 [:players 0 :goods])))))
  (testing "large warehouse: two kinds fully plus a single keeps everything"
    (let [g (-> (captain-game :ships (full-ships))
                (set-player 0 {:goods (goods {:corn 3 :coffee 2 :indigo 1})
                               :buildings [{:type :large-warehouse :colonists 1}]}))
          g2 (end-loading g)
          g3 (-> g2
                 (storage-move [:store-kind :corn])
                 (storage-move [:store-kind :coffee])
                 (storage-move [:store-single :indigo]))]
      (is (= (goods {:corn 3 :coffee 2 :indigo 1}) (get-in g3 [:players 0 :goods])))))
  (testing "a single kind with no warehouse auto-resolves to one kept good"
    (let [g (-> (captain-game :ships (full-ships))
                (set-player 0 {:goods (goods {:corn 3})}))
          supply-before (get-in g [:goods-supply :corn])
          g2 (end-loading g)]
      ;; no decision needed anywhere: storage resolved automatically
      (is (= :role-selection (:phase g2)))
      (is (= (goods {:corn 1}) (get-in g2 [:players 0 :goods])))
      (is (= (+ supply-before 2) (get-in g2 [:goods-supply :corn]))))))

;; ================================================================================
;; Mayor
;; ================================================================================

(defn- drive-role
  "Let the heuristic AI play out the current role execution to completion"
  [g]
  (loop [gs g, n 0]
    (if (or (not= :role-execution (:phase gs)) (> n 100))
      gs
      (let [idx (:role-execution-current-idx gs)
            pid (:id (nth (:players gs) idx))]
        (recur (rules/apply-move gs (ai/ai-select-move gs pid)) (inc n))))))

(deftest mayor-distribution
  (testing "ship colonists are dealt starting with the mayor, plus supply privilege"
    (let [g (-> (mk-game 3)
                (assoc :governor-idx 0 :colonist-ship 4 :current-player-idx 2)
                ;; give the mayor open circles so placement stops for a choice
                ;; (otherwise the forced-fill shortcut would auto-resolve it)
                (set-player 2 {:buildings [{:type :large-indigo-maker :colonists 0}]}))
          ;; P3 (seat 2) selects mayor: distribution happens immediately
          g2 (rules/select-role g 3 :mayor)]
      ;; deal order: P3 P1 P2 P3 -> P3 has 2 + 1 privilege in hand, P1 1, P2 1
      (is (= 1 (get-in g2 [:players 0 :colonists-in-hand])))
      (is (= 1 (get-in g2 [:players 1 :colonists-in-hand])))
      (is (= 3 (get-in g2 [:players 2 :colonists-in-hand])))
      (is (zero? (:colonist-ship g2)))
      ;; the mayor (open circles, real choice) places first
      (is (= 2 (:role-execution-current-idx g2)))))
  (testing "forced-fill shortcut: hand >= circles auto-fills, no decision needed"
    (let [g (-> (mk-game 3) (assoc :colonist-ship 3))
          ;; each player has exactly 1 circle and >=1 colonist -> all auto-filled
          g2 (rules/select-role g 1 :mayor)]
      (is (= :role-selection (:phase g2)))                       ;; role fully resolved
      (is (= 1 (:colonists (first (get-in g2 [:players 0 :plantations])))))
      (is (= 1 (get-in g2 [:players 0 :san-juan-colonists])))))  ;; 2 in hand, 1 circle -> 1 left over
  (testing "placement is one at a time; done only when nothing is placeable"
    (let [g (-> (mk-game 3)
                ;; large building gives P1 4 circles for 2 colonists -> real choice
                (set-player 0 {:buildings [{:type :large-indigo-maker :colonists 0}]})
                (assoc :colonist-ship 3))
          g2 (rules/select-role g 1 :mayor)   ;; P1: 1 dealt + 1 privilege = 2 in hand
          place {:type :role-action :role :mayor :player-id 1
                 :args [:place-colonist :plantation :indigo]}
          done {:type :role-action :role :mayor :player-id 1 :args []}
          ;; done is refused while the player can still place
          g-refused (rules/apply-move g2 done)
          g3 (rules/apply-move g2 place)]
      (is (= 2 (get-in g2 [:players 0 :colonists-in-hand])))
      (is (identical? g2 g-refused))
      (is (= 1 (get-in g3 [:players 0 :colonists-in-hand])))
      (is (= 1 (:colonists (first (get-in g3 [:players 0 :plantations])))))
      ;; still a colonist and open circles: same player keeps placing
      (is (= 0 (:role-execution-current-idx g3)))))
  (testing "placement can rearrange: the board is swept into hand at turn start"
    (let [g (-> (mk-game 3)
                (set-player 0 {:plantations [{:type :corn :colonists 1}
                                             {:type :indigo :colonists 0}
                                             {:type :sugar :colonists 0}]})
                (assoc :colonist-ship 0 :colonist-supply 0))
          g2 (rules/select-role g 1 :mayor)]
      ;; 1 colonist swept from the board into hand, 3 open circles -> a choice
      (is (= 1 (get-in g2 [:players 0 :colonists-in-hand])))
      (is (zero? (:colonists (first (get-in g2 [:players 0 :plantations])))))
      (is (= 0 (:role-execution-current-idx g2)))))
  (testing "ship refill at phase end: empty building circles, minimum player count"
    (let [g (-> (mk-game 3)
                (set-player 0 {:buildings [{:type :large-indigo-maker :colonists 0}
                                           {:type :coffee-maker :colonists 0}]})
                (assoc :colonist-ship 0))
          g2 (drive-role (rules/select-role g 1 :mayor))
          empty-circles (reduce + (for [p (:players g2), b (:buildings p)]
                                    (- (get-in state/buildings [(:type b) :worker] 1)
                                       (:colonists b))))]
      (is (not= :role-execution (:phase g2)))
      (is (= (max 3 empty-circles) (:colonist-ship g2)))))
  (testing "a refill shortfall triggers the game-end condition"
    (let [g (-> (mk-game 3)
                (assoc :colonist-ship 0 :colonist-supply 0))
          g2 (drive-role (rules/select-role g 1 :mayor))]
      (is (:colonist-ship-shortfall g2))
      (is (state/check-victory-conditions g2)))))

(deftest mayor-human-keeps-board
  (testing "a human's board is NOT swept; they place/remove new colonists by index"
    (let [g (-> (human-game 3)
                (set-player 0 {:plantations [{:type :corn :colonists 1}
                                             {:type :indigo :colonists 0}]})
                (assoc :colonist-ship 0 :colonist-supply 3))
          g2 (rules/select-role g 1 :mayor)]      ;; P1 human, privilege = 1 from supply
      ;; existing corn stays manned, new colonist is in hand, P1 must decide
      (is (= 1 (:colonists (first (get-in g2 [:players 0 :plantations])))))
      (is (= 1 (get-in g2 [:players 0 :colonists-in-hand])))
      (is (= 0 (:role-execution-current-idx g2)))
      ;; place on the empty indigo (index 1)
      (let [g3 (rules/apply-move g2 {:type :role-action :role :mayor :player-id 1
                                     :args [:place-at :plantation 1]})]
        (is (= 1 (:colonists (second (get-in g3 [:players 0 :plantations])))))
        (is (= 0 (get-in g3 [:players 0 :colonists-in-hand])))
        ;; remove the corn colonist back to hand (rearrange)
        (let [g4 (rules/apply-move g3 {:type :role-action :role :mayor :player-id 1
                                       :args [:remove-at :plantation 0]})]
          (is (= 0 (:colonists (first (get-in g4 [:players 0 :plantations])))))
          (is (= 1 (get-in g4 [:players 0 :colonists-in-hand])))
          ;; done is refused while a colonist can still be placed
          (is (identical? g4 (rules/apply-move g4 {:type :role-action :role :mayor
                                                   :player-id 1 :args []}))))))))

;; ================================================================================
;; Settler
;; ================================================================================

(deftest settler-rules
  (let [base (-> (mk-game 3)
                 (assoc :face-up-plantations [:coffee :sugar :tobacco :corn]
                        :selected-role :settler
                        :role-selector-idx 0
                        :role-execution-order [0 1 2]
                        :role-execution-current-idx 0
                        :phase :role-execution))]
    (testing "only the settler may take a quarry"
      (let [g2 (rules/execute-settler base 2 :quarry)]
        (is (= 1 (count (get-in g2 [:players 1 :plantations]))))) ;; unchanged (initial indigo)
      (let [g2 (rules/execute-settler base 1 :quarry)]
        (is (some #(= (:type %) :quarry) (get-in g2 [:players 0 :plantations])))))
    (testing "an occupied construction hut allows a quarry"
      (let [g (set-player base 1 {:buildings [{:type :construction-hut :colonists 1}]})
            g2 (rules/execute-settler g 2 :quarry)]
        (is (some #(= (:type %) :quarry) (get-in g2 [:players 1 :plantations])))))
    (testing "a full island takes no more tiles"
      (let [g (set-player base 0 {:plantations (vec (repeat 12 {:type :corn :colonists 0}))})
            g2 (rules/execute-settler g 1 :coffee)]
        (is (= 12 (count (get-in g2 [:players 0 :plantations]))))))
    (testing "hospice grants a colonist for the regular take but NOT the hacienda draw"
      (let [g (set-player base 0 {:buildings [{:type :hospice :colonists 1}
                                              {:type :hacienda :colonists 1}]})
            regular (rules/execute-settler g 1 :coffee)
            hacienda-draw (rules/execute-settler g 1 :random-from-deck)]
        (is (= 1 (:colonists (last (get-in regular [:players 0 :plantations])))))
        (is (= 0 (:colonists (last (get-in hacienda-draw [:players 0 :plantations])))))))))

;; ================================================================================
;; Scoring and round flow
;; ================================================================================

(deftest residence-scores-four-below-nine-spaces
  (let [player (-> (state/new-player 1 "P1")
                   (assoc :plantations (vec (repeat 5 {:type :corn :colonists 0}))
                          :buildings [{:type :residence :colonists 1}]))]
    ;; residence 4 VP + bonus 4 VP for <= 9 filled spaces
    (is (= 8 (state/calculate-victory-points player)))))

(deftest tiebreaker-uses-money-and-goods
  (let [poor (-> (state/new-player 1 "poor") (assoc :money 1))
        rich (-> (state/new-player 2 "rich") (assoc :money 4 :goods (goods {:corn 2})))]
    (is (= 1 (state/tiebreaker-value poor)))
    (is (= 6 (state/tiebreaker-value rich)))))

(deftest round-flow
  (testing "unpicked roles gain gold and the governor rotates"
    (let [g (mk-game 3)
          ;; each player picks a role and finishes it (prospector-free 3P set)
          play-role (fn [game role]
                      (let [pid (:id (state/current-player game))
                            game (rules/select-role game pid role)]
                        ;; skip through every player's execution turn
                        (loop [gs game safety 0]
                          (if (or (not= :role-execution (:phase gs)) (> safety 20))
                            gs
                            (recur (rules/apply-move gs {:type :role-action
                                                         :role role
                                                         :player-id (:id (nth (:players gs) (:role-execution-current-idx gs)))
                                                         :args []})
                                   (inc safety))))))
          g2 (-> g (play-role :settler) (play-role :trader) (play-role :captain))]
      (is (= 2 (:round g2)))
      (is (= 1 (:governor-idx g2)))
      (is (= 1 (:current-player-idx g2)))
      ;; exactly the 3 unpicked roles carry a doubloon
      (is (= {:settler 0 :mayor 1 :builder 1 :craftsman 1 :trader 0 :captain 0}
             (:role-gold g2))))))

;; ================================================================================
;; Full-game smoke test: heuristic AI vs itself must reach game over
;; ================================================================================

(deftest full-game-smoke-test
  (dotimes [_ 3]
    (let [result
          (with-out-str
            (loop [g (mk-game 3) steps 0]
              (cond
                (:game-over g) (println "DONE")
                (> steps 5000) (println "STALLED")
                :else
                (let [actor-idx (if (= :role-execution (:phase g))
                                  (:role-execution-current-idx g)
                                  (:current-player-idx g))
                      actor-id (:id (nth (:players g) actor-idx))
                      move (ai/ai-select-move g actor-id)]
                  (if (nil? move)
                    (println "NO-MOVE at" (:phase g))
                    (recur (rules/apply-move g move) (inc steps)))))))]
      (is (.contains ^String result "DONE")
          (str "game did not finish: " result)))))
