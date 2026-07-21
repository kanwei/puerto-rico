(ns puerto-rico.ai-test
  "Tests for the AlphaZero-style AI stack: action space, encoder, MCTS."
  (:require [clojure.test :refer [deftest is testing]]
            [puerto-rico.game.state :as state]
            [puerto-rico.game.rules :as rules]
            [puerto-rico.ai.actions :as actions]
            [puerto-rico.ai.encoder :as encoder]
            [puerto-rico.ai.mcts :as mcts]))

(defn mk-game []
  (state/new-game-state (mapv #(state/new-player (inc %) (str "P" (inc %))) (range 3))))

;; --------------------------------------------------------------------------
;; Action space
;; --------------------------------------------------------------------------

(deftest action-space-is-fixed
  (is (= 98 actions/num-actions))
  (is (= 23 (count actions/building-order)))
  (is (= (set actions/building-order) (set (keys state/buildings)))))

(deftest mayor-placement-actions
  ;; Give P1 a large building so hand (2) < circles (4): a real placement choice
  (let [g (-> (mk-game)
              (update-in [:players 0 :buildings] conj {:type :large-indigo-maker :colonists 0})
              (assoc :colonist-ship 3))
        g2 (rules/select-role g 1 :mayor)
        ids (set (actions/legal-action-ids g2))]
    ;; placement is mandatory, so no pass action while a colonist can be placed
    (is (contains? ids (actions/action-id {:kind :place-plantation :plantation :indigo})))
    (is (contains? ids (actions/action-id {:kind :place-building :building :large-indigo-maker})))
    (is (not (contains? ids (actions/action-id {:kind :pass}))))
    ;; placing keeps the turn with the same player
    (let [g3 (actions/apply-action g2 (actions/action-id {:kind :place-plantation :plantation :indigo}))]
      (is (= (:role-execution-current-idx g2) (:role-execution-current-idx g3))))))

(deftest craftsman-privilege-actions
  (let [g (-> (mk-game)
              (assoc-in [:players 0 :plantations] [{:type :corn :colonists 1}
                                                   {:type :indigo :colonists 1}])
              (assoc-in [:players 0 :buildings] [{:type :small-indigo-maker :colonists 1}])
              (assoc :current-player-idx 0))
        g1 (rules/select-role g 1 :craftsman)
        ;; produce via the pass action
        g2 (actions/apply-action g1 (actions/action-id {:kind :pass}))
        ids (set (actions/legal-action-ids g2))]
    ;; selector produced corn + indigo -> a privilege choice between them
    (is (contains? ids (actions/action-id {:kind :privilege-good :good :corn})))
    (is (contains? ids (actions/action-id {:kind :privilege-good :good :indigo})))
    (let [g3 (actions/apply-action g2 (actions/action-id {:kind :privilege-good :good :indigo}))]
      (is (= 2 (get-in g3 [:players 0 :goods :indigo])))
      (is (not= :role-execution (:phase g3))))))

(deftest legal-actions-at-game-start
  (let [g (mk-game)
        ids (actions/legal-action-ids g)]
    ;; 3-player game: the 6 role-selection actions, prospectors excluded
    (is (= 6 (count ids)))
    (is (every? #(= :select-role (:kind (nth actions/action-table %))) ids))))

(deftest action-roundtrip
  (testing "select a role, take a plantation via action ids"
    (let [g (-> (mk-game) (assoc :face-up-plantations [:coffee :sugar :tobacco :corn]))
          settler-id (actions/action-id {:kind :select-role :role :settler})
          g2 (actions/apply-action g settler-id)]
      (is (= :role-execution (:phase g2)))
      (let [ids (set (actions/legal-action-ids g2))
            coffee-id (actions/action-id {:kind :settler-take :plantation :coffee})
            quarry-id (actions/action-id {:kind :settler-take :plantation :quarry})]
        ;; selector may take a face-up tile, the quarry, or pass
        (is (contains? ids coffee-id))
        (is (contains? ids quarry-id))
        (is (contains? ids (actions/action-id {:kind :pass})))
        (let [g3 (actions/apply-action g2 coffee-id)]
          (is (some #(= (:type %) :coffee) (get-in g3 [:players 0 :plantations])))
          ;; next player may NOT take the quarry
          (is (not (contains? (set (actions/legal-action-ids g3)) quarry-id))))))))

(deftest hacienda-draw-does-not-consume-the-turn
  (let [g (-> (mk-game)
              (assoc :face-up-plantations [:coffee :sugar :tobacco :corn])
              (update-in [:players 0 :buildings] conj {:type :hacienda :colonists 1}))
        g2 (actions/apply-action g (actions/action-id {:kind :select-role :role :settler}))
        hacienda-id (actions/action-id {:kind :settler-hacienda})]
    (is (contains? (set (actions/legal-action-ids g2)) hacienda-id))
    (let [g3 (actions/apply-action g2 hacienda-id)]
      ;; still the same player's turn, with one extra tile
      (is (= 0 (:role-execution-current-idx g3)))
      (is (= 2 (count (get-in g3 [:players 0 :plantations]))))
      ;; the draw can't be repeated this phase
      (is (not (contains? (set (actions/legal-action-ids g3)) hacienda-id)))
      ;; the regular take still works
      (let [g4 (actions/apply-action g3 (actions/action-id {:kind :settler-take :plantation :coffee}))]
        (is (= 3 (count (get-in g4 [:players 0 :plantations]))))
        (is (= 1 (:role-execution-current-idx g4)))))))

(deftest captain-actions-mandatory-load
  (let [g (-> (mk-game)
              (assoc-in [:players 0 :goods :corn] 3)
              (assoc :selected-role :captain
                     :role-selector-idx 0
                     :role-execution-order [0 1 2]
                     :role-execution-current-idx 0
                     :phase :role-execution))
        ids (set (actions/legal-action-ids g))]
    ;; loading is mandatory: the pass action must not be offered
    (is (contains? ids (actions/action-id {:kind :ship :good :corn})))
    (is (not (contains? ids (actions/action-id {:kind :pass}))))))

;; --------------------------------------------------------------------------
;; Encoder
;; --------------------------------------------------------------------------

(deftest encoder-fixed-size-and-range
  (let [g (mk-game)]
    (is (= (encoder/encoded-size 3) (count (encoder/encode-state g))))
    ;; size and range stay stable through random play
    (loop [gs g, steps 0]
      (when (and (not (:game-over gs)) (< steps 120))
        (let [v (encoder/encode-state gs)]
          (is (= (encoder/encoded-size 3) (count v)))
          (is (every? #(<= 0.0 % 1.0) v)))
        (recur (actions/apply-action gs (rand-nth (actions/legal-action-ids gs)))
               (inc steps))))))

(deftest encoder-is-egocentric
  (let [g (mk-game)
        ;; give player 2 (seat 1) distinctive money
        g (assoc-in g [:players 1 :money] 17)
        ;; seat 1 to act
        g-p2 (assoc g :current-player-idx 1)
        v (encoder/encode-state g-p2)]
    ;; seat 0 of the encoding is the actor: money is the first feature
    (is (= (/ 17.0 20.0) (first v)))))

;; --------------------------------------------------------------------------
;; MCTS
;; --------------------------------------------------------------------------

(deftest outcome-vector-shapes
  (let [g (mk-game)]
    (is (= 3 (count (mcts/outcome-vector g))))
    (is (< 0.999 (reduce + (mcts/outcome-vector g)) 1.001))))

(deftest mcts-returns-legal-moves
  (let [g (mk-game)
        move (mcts/ai-select-move g 1 {:simulations 30})]
    (is (some? move))
    (is (rules/valid-move? g 1 move))))

(deftest mcts-plays-full-games
  (testing "a 3-player all-MCTS game reaches game over"
    (loop [gs (mk-game), steps 0]
      (cond
        (:game-over gs)
        (is true)

        (> steps 1200)
        (is false "MCTS game did not finish within 1200 decisions")

        :else
        (let [actor-id (:id (actions/actor-player gs))
              move (mcts/ai-select-move gs actor-id {:simulations 8})]
          (recur (rules/apply-move gs move) (inc steps)))))))

;; --------------------------------------------------------------------------
;; Heuristic action scoring (settlement/production matching)
;; --------------------------------------------------------------------------

(defn- mk-mayor-gs
  "Build a game state in mayor execution for P0 with the given plantations and
   buildings. `colonists-in-hand` defaults to 2."
  [plantations buildings & {:keys [colonists-in-hand] :or {colonists-in-hand 2}}]
  (-> (mk-game)
      (assoc :phase :role-execution
             :selected-role :mayor
             :role-execution-current-idx 0
             :role-execution-order [0 1 2])
      (assoc-in [:players 0 :plantations] plantations)
      (assoc-in [:players 0 :buildings] buildings)
      (assoc-in [:players 0 :colonists-in-hand] colonists-in-hand)))

(defn- plant-score
  "Heuristic score for placing a colonist on a plantation of `type`."
  [scores type]
  (get scores (actions/action-id {:kind :place-plantation :plantation type})))

(defn- bldg-score
  "Heuristic score for placing a colonist on a building of `type`."
  [scores type]
  (get scores (actions/action-id {:kind :place-building :building type})))

(deftest heuristic-mayor-prefers-paired-production
  "A production building with matching plantations scores highest."
  (testing "paired sugar maker scores highest"
    (let [gs (mk-mayor-gs [{:type :sugar :colonists 0}]
                          [{:type :small-sugar-maker :colonists 0}])
          scores (actions/heuristic-action-scores gs)]
      (is scores "heuristic should apply for mayor with placements")
      (is (> (bldg-score scores :small-sugar-maker)
             (plant-score scores :sugar))
          "paired production building should score higher than paired plantation"))))

(deftest heuristic-mayor-unpaired-production-scores-low
  "A production building WITHOUT matching plantations scores low (0.25),
   the same as unmatched plantations."
  (testing "sugar maker with no sugar plantations scores low"
    (let [gs (mk-mayor-gs [{:type :indigo :colonists 0}]
                          [{:type :small-sugar-maker :colonists 0}
                           {:type :small-indigo-maker :colonists 0}]
                          :colonists-in-hand 1)
          scores (actions/heuristic-action-scores gs)]
      (is (< (bldg-score scores :small-sugar-maker)
             (plant-score scores :indigo))
          "unmatched production building should score below paired plantation")
      (is (<= (bldg-score scores :small-sugar-maker) 0.3)
          "unmatched production building should score near bottom"))))

(deftest heuristic-mayor-prefers-paired-plantations
  "A plantation matching an owned production building should score higher
   than an unmatched plantation."
  (testing "paired sugar plantation scores higher than unmatched indigo"
    (let [gs (mk-mayor-gs [{:type :sugar :colonists 0}
                           {:type :indigo :colonists 0}]
                          [{:type :small-sugar-maker :colonists 0}])
          scores (actions/heuristic-action-scores gs)]
      (is (> (plant-score scores :sugar)
             (plant-score scores :indigo))
          "paired plantation should score higher than unmatched plantation"))))

(deftest heuristic-mayor-corn-over-unmatched
  "Corn plantations should score higher than unmatched plantations and
   slightly higher than non-production buildings."
  (testing "corn scores higher than unmatched indigo"
    (let [gs (mk-mayor-gs [{:type :corn :colonists 0}
                           {:type :indigo :colonists 0}]
                          [])
          scores (actions/heuristic-action-scores gs)]
      (is (> (plant-score scores :corn)
             (plant-score scores :indigo))
          "corn should score higher than unmatched plantation")))

  (testing "corn scores slightly above non-production buildings"
    (let [gs (mk-mayor-gs [{:type :corn :colonists 0}]
                          [{:type :small-market :colonists 0}])
          scores (actions/heuristic-action-scores gs)]
      (is (> (plant-score scores :corn)
             (bldg-score scores :small-market))
          "corn should score slightly above utility buildings"))))

(deftest heuristic-mayor-full-priority-chain
  "Test the complete priority chain: paired-bldg > paired-plantation >
   corn > non-production > unmatched-everything."
  (testing "all tiers present in one state"
    (let [gs (mk-mayor-gs [{:type :sugar :colonists 0}
                           {:type :corn :colonists 0}
                           {:type :indigo :colonists 0}]
                          [{:type :small-sugar-maker :colonists 0}
                           {:type :small-market :colonists 0}])
          scores (actions/heuristic-action-scores gs)]
      (is (> (bldg-score scores :small-sugar-maker)
             (plant-score scores :sugar))
          "paired production building > paired plantation")
      (is (> (plant-score scores :sugar)
             (plant-score scores :corn))
          "paired plantation > corn plantation")
      (is (> (plant-score scores :corn)
             (bldg-score scores :small-market))
          "corn > non-production building")
      (is (> (bldg-score scores :small-market)
             (plant-score scores :indigo))
          "non-production building > unmatched plantation"))))

(deftest heuristic-mayor-symmetric-situation
  "When both good types have identical unfilled plantations and buildings,
   the heuristic should score them equally."
  (testing "2 empty sugar, 2 empty indigo; 1 unfilled sugar bldg, 1 unfilled indigo bldg"
    (let [gs (mk-mayor-gs [{:type :sugar :colonists 0}
                           {:type :sugar :colonists 0}
                           {:type :indigo :colonists 0}
                           {:type :indigo :colonists 0}]
                          [{:type :small-sugar-maker :colonists 0}
                           {:type :small-indigo-maker :colonists 0}])
          scores (actions/heuristic-action-scores gs)
          sugar-plant-score (plant-score scores :sugar)
          indigo-plant-score (plant-score scores :indigo)
          sugar-bldg-score (bldg-score scores :small-sugar-maker)
          indigo-bldg-score (bldg-score scores :small-indigo-maker)]
      (is (some? sugar-plant-score) "sugar plantation score should exist")
      (is (some? indigo-plant-score) "indigo plantation score should exist")
      (is (= sugar-plant-score indigo-plant-score)
          "symmetric situations should produce equal plantation scores")
      (is (= sugar-bldg-score indigo-bldg-score)
          "symmetric situations should produce equal building scores"))))

(deftest heuristic-decays-in-endgame
  "When most table buildings are placed, the heuristic should push scores toward
   0.5 (neutral), reducing its influence."
  (testing "many filled buildings across table => scores near neutral"
    (let [gs (mk-mayor-gs [{:type :sugar :colonists 0}
                           {:type :corn :colonists 1}]
                          [{:type :small-sugar-maker :colonists 0}
                           {:type :small-indigo-maker :colonists 1}
                           {:type :large-sugar-maker :colonists 3}
                           {:type :tobacco-maker :colonists 1}
                           {:type :coffee-maker :colonists 1}
                           {:type :small-market :colonists 1}
                           {:type :hacienda :colonists 1}
                           {:type :construction-hut :colonists 1}]
                          :colonists-in-hand 1)
          gs (-> gs
                 (assoc-in [:players 1 :buildings]
                           [{:type :large-indigo-maker :colonists 3}
                            {:type :small-warehouse :colonists 1}
                            {:type :hospice :colonists 1}
                            {:type :office :colonists 1}
                            {:type :factory :colonists 1}
                            {:type :large-market :colonists 1}])
                 (assoc-in [:players 2 :buildings]
                           [{:type :large-warehouse :colonists 1}
                            {:type :university :colonists 1}
                            {:type :harbor :colonists 1}
                            {:type :wharf :colonists 1}
                            {:type :guild-hall :colonists 1}
                            {:type :residence :colonists 1}]))
          scores (actions/heuristic-action-scores gs)
          sugar-score (plant-score scores :sugar)]
      (is (some? sugar-score) "score should exist")
      (is (<= (Math/abs (- sugar-score 0.5)) 0.15)
          "in saturated endgame, scores should be near neutral (0.5)"))))

;; --------------------------------------------------------------------------
;; Heuristic action scoring (settler plantation-taking)
;; --------------------------------------------------------------------------

(defn- mk-settler-gs
  "Build a game state in settler execution for P0 with the given plantations,
   buildings, and face-up plantations."
  [plantations buildings face-up]
  (-> (mk-game)
      (assoc :phase :role-execution
             :selected-role :settler
             :role-execution-current-idx 0
             :role-execution-order [0 1 2])
      (assoc-in [:players 0 :plantations] plantations)
      (assoc-in [:players 0 :buildings] buildings)
      (assoc :face-up-plantations face-up)))

(defn- take-score
  "Heuristic score for taking a plantation of `type`."
  [scores type]
  (get scores (actions/action-id {:kind :settler-take :plantation type})))

(deftest heuristic-settler-favors-plantation-for-owned-production
  "When the player owns a large-indigo-maker (3 slots) and has no indigo
   plantations, taking an indigo plantation should score highest."
  (testing "large-indigo-maker + 0 indigo plantations, indigo face-up"
    (let [gs (mk-settler-gs []
                            [{:type :large-indigo-maker :colonists 0}]
                            [:indigo :sugar :corn])
          scores (actions/heuristic-action-scores gs)]
      (is scores "heuristic should apply for settler with takes")
      (is (> (take-score scores :indigo)
             (take-score scores :sugar))
          "indigo plantation should score higher than sugar
             when player owns indigo production")
      (is (> (take-score scores :indigo)
             (take-score scores :corn))
          "indigo plantation should score higher than corn
             when player owns indigo production"))))

(deftest heuristic-settler-prefers-largest-gap
  "When the player owns both a large-indigo-maker (3 slots, 0 plantations)
   and a small-sugar-maker (1 slot, 0 plantations), indigo should score
   higher because the shortfall is larger."
  (testing "large-indigo-maker + small-sugar-maker, both face-up"
    (let [gs (mk-settler-gs []
                            [{:type :large-indigo-maker :colonists 0}
                             {:type :small-sugar-maker :colonists 0}]
                            [:indigo :sugar :tobacco])
          scores (actions/heuristic-action-scores gs)]
      (is (> (take-score scores :indigo)
             (take-score scores :sugar))
          "indigo should score higher than sugar
             when shortfall is 3 vs 1"))))

(deftest heuristic-settler-reduces-score-when-plantations-match
  "When the player already has plantations matching production capacity,
   taking another plantation of that type should score lower than one with
   an unmatched good type (which scores 0.2)."
  (testing "large-sugar-maker + 3 sugar plantations, sugar face-up"
    ;; 3 sugar plantations vs 3 sugar production slots => shortfall is 0
    ;; sugar scores 0.3 (matched, no shortfall), corn scores 0.2 (no match)
    (let [gs (mk-settler-gs [{:type :sugar :colonists 0}
                             {:type :sugar :colonists 0}
                             {:type :sugar :colonists 0}]
                            [{:type :large-sugar-maker :colonists 0}]
                            [:sugar :corn])
          scores (actions/heuristic-action-scores gs)]
      (is (< (take-score scores :sugar)
             0.35)
          "sugar should score near neutral when shortfall is 0")
      (is (> (take-score scores :sugar)
             (take-score scores :corn))
          "sugar (matched, no shortfall) should still beat corn (no production)"))))

(deftest heuristic-settler-ignores-plantations-without-production
  "When tobacco is face-up but the player has no tobacco production,
   taking tobacco should score lower than taking a matching good."
  (testing "large-indigo-maker face-up, tobacco face-up but no tobacco production"
    (let [gs (mk-settler-gs []
                            [{:type :large-indigo-maker :colonists 0}]
                            [:indigo :tobacco])
          scores (actions/heuristic-action-scores gs)]
      (is (> (take-score scores :indigo)
             (take-score scores :tobacco))
          "indigo should score higher than tobacco
             when only indigo production exists"))))

(deftest heuristic-settler-partial-fill-increases-need
  "When the player owns a large-sugar-maker but only 1 sugar plantation,
   the shortfall (3 - 1 = 2) should make taking sugar score high."
  (testing "large-sugar-maker + 1 sugar plantation, sugar face-up"
    (let [gs (mk-settler-gs [{:type :sugar :colonists 0}]
                            [{:type :large-sugar-maker :colonists 0}]
                            [:sugar :corn])
          scores (actions/heuristic-action-scores gs)]
      (is (> (take-score scores :sugar)
             (take-score scores :corn))
          "sugar should score higher than corn
             when sugar shortfall is 2")
      (is (>= (take-score scores :sugar) 0.6)
          "sugar plantation score should be high"))))