(ns puerto-rico.game.state
  "Core game state definitions for Puerto Rico board game")

;; Game constants
(def goods [:corn :indigo :sugar :tobacco :coffee])
(def buildings
  {:small-indigo-plant {:type :production :cost 1 :good :indigo :vp 1}
   :small-sugar-mill {:type :production :cost 2 :good :sugar :vp 1}
   :small-market {:type :trade :cost 1 :vp 1}
   :hacienda {:type :plantation :cost 2 :vp 1}
   :construction-hut {:type :building :cost 2 :vp 1}
   :small-warehouse {:type :storage :cost 3 :vp 1}
   :indigo-plant {:type :production :cost 3 :good :indigo :vp 2}
   :sugar-mill {:type :production :cost 4 :good :sugar :vp 2}
   :hospice {:type :plantation :cost 4 :vp 2}
   :office {:type :trade :cost 5 :vp 2}
   :large-market {:type :trade :cost 5 :vp 2}
   :large-warehouse {:type :storage :cost 6 :vp 2}
   :university {:type :building :cost 8 :vp 3}
   :factory {:type :production :cost 7 :vp 3}
   :tobacco-storage {:type :production :cost 5 :good :tobacco :vp 3}
   :coffee-roaster {:type :production :cost 6 :good :coffee :vp 3}
   :large-building-1 {:type :large :cost 10 :vp 4}
   :large-building-2 {:type :large :cost 10 :vp 4}
   :large-building-3 {:type :large :cost 10 :vp 4}
   :large-building-4 {:type :large :cost 12 :vp 5}
   :large-building-5 {:type :large :cost 16 :vp 6}})

(def roles [:settler :mayor :builder :craftsman :trader :captain :prospector])

(def plantation-tiles
  {:corn 10 :indigo 12 :sugar 11 :tobacco 9 :coffee 8 :quarry 8})

;; Player state structure
(defn new-player
  [id name]
  {:id id
   :name name
   :money 2
   :goods {:corn 0 :indigo 0 :sugar 0 :tobacco 0 :coffee 0}
   :plantations []
   :buildings []
   :colonists []
   :victory-points 0
   :is-ai false})

;; Game state structure
(defn new-game-state
  [players]
  {:players players
   :current-player-idx 0
   :round 1
   :phase :role-selection
   :selected-role nil
   :role-player-idx nil
   :available-roles (set roles)
   :used-roles #{}
   :plantation-supply plantation-tiles
   :goods-supply {:corn 10 :indigo 11 :sugar 11 :tobacco 9 :coffee 9}
   :colonist-supply 95
   :building-supply buildings
   :victory-point-supply 122
   :ships [{:capacity 4 :good nil :amount 0}
           {:capacity 5 :good nil :amount 0}
           {:capacity 6 :good nil :amount 0}]
   :trading-house []
   :game-over false
   :winner nil})

;; Game state accessors
(defn current-player [game-state]
  (get-in game-state [:players (:current-player-idx game-state)]))

(defn player-by-id [game-state player-id]
  (->> (:players game-state)
       (filter #(= (:id %) player-id))
       first))

(defn next-player-idx [game-state]
  (mod (inc (:current-player-idx game-state))
       (count (:players game-state))))

(defn advance-to-next-player [game-state]
  (assoc game-state :current-player-idx (next-player-idx game-state)))

;; Victory condition checks
(defn check-victory-conditions [game-state]
  (let [players (:players game-state)]
    (or
      ;; Check if any player has 12 building spaces filled
     (some #(>= (count (:buildings %)) 12) players)
      ;; Check if colonist supply is exhausted  
     (<= (:colonist-supply game-state) 0)
      ;; Check if victory point supply is exhausted
     (<= (:victory-point-supply game-state) 0))))

;; Calculate final victory points
(defn calculate-victory-points [player]
  (let [building-vps (reduce + (map #(get-in buildings [% :vp] 0) (:buildings player)))
        goods-vps (quot (reduce + (vals (:goods player))) 1)
        bonus-vps 0] ; TODO: Add bonus VP calculations from large buildings
    (+ (:victory-points player) building-vps goods-vps bonus-vps)))
