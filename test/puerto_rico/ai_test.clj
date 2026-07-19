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
  (is (= 93 actions/num-actions))
  (is (= 23 (count actions/building-order)))
  (is (= (set actions/building-order) (set (keys state/buildings)))))

(deftest mayor-placement-actions
  (let [g (-> (mk-game) (assoc :colonist-ship 3))
        g2 (rules/select-role g 1 :mayor)
        ids (set (actions/legal-action-ids g2))]
    ;; P1 has colonists in hand and an unmanned indigo plantation:
    ;; placement is mandatory, so no pass action
    (is (contains? ids (actions/action-id {:kind :place-plantation :plantation :indigo})))
    (is (not (contains? ids (actions/action-id {:kind :pass}))))
    ;; placing keeps the turn with the same player
    (let [g3 (actions/apply-action g2 (actions/action-id {:kind :place-plantation :plantation :indigo}))]
      (is (= (:role-execution-current-idx g2) (:role-execution-current-idx g3)))
      ;; nothing else placeable: now done is the only option
      (is (= [(actions/action-id {:kind :pass})] (actions/legal-action-ids g3))))))

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
