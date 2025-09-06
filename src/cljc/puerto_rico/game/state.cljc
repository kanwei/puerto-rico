(ns puerto-rico.game.state
  "Core game state definitions for Puerto Rico board game")

;; Game constants
(def goods [:corn :indigo :sugar :tobacco :coffee])
(def buildings
  ;; Production Buildings - Column 1
  {:small-indigo-maker {:type :production :cost 1 :good :indigo :vp 1 :worker 1 :count 4 :column 1}
   :small-sugar-maker {:type :production :cost 2 :good :sugar :vp 1 :worker 1 :count 4 :column 1}

   ;; Production Buildings - Column 2  
   :large-indigo-maker {:type :production :cost 3 :good :indigo :vp 2 :worker 3 :count 3 :column 2}
   :large-sugar-maker {:type :production :cost 4 :good :sugar :vp 2 :worker 3 :count 3 :column 2}

   ;; Production Buildings - Column 3
   :tobacco-maker {:type :production :cost 5 :good :tobacco :vp 3 :worker 3 :count 3 :column 3}
   :coffee-maker {:type :production :cost 6 :good :coffee :vp 3 :worker 2 :count 3 :column 3}

   ;; Trade Buildings - Column 1
   :small-market {:type :trade :cost 1 :vp 1 :worker 1 :count 2 :column 1
                  :description "When the owner of an occupied small market sells a barrel in the trader phase, he gets an extra doubloon from the bank for it."}
   :hacienda {:type :plantation :cost 2 :vp 1 :worker 1 :count 2 :column 1
              :description "On his turn in the settler phase, the owner of an occupied hacienda may, before he takes a face-up plantation tile, take an additional tile chosen at random from the rest of the supply."}
   :construction-hut {:type :building :cost 2 :vp 1 :worker 1 :count 2 :column 1
                      :description "In the settler phase, the owner of an occupied construction hut, can place a quarry on his island of one of the face-up plantation tiles."}
   :small-warehouse {:type :storage :cost 3 :vp 1 :worker 1 :count 2 :column 1
                     :description "The owner of an occupied small warehouse may store, at the end of the captain phase, in addition to the single goods barrel he is allowed to store on his windrose, all the barrels of one kind of goods that he chooses."}

   ;; Trade Buildings - Column 2
   :large-market {:type :trade :cost 5 :vp 2 :worker 1 :count 2 :column 2
                  :description "When the owner of an occupied large market sells a good in the trader phase, he gets an extra 2 doubloons from the bank for it."}
   :hospice {:type :plantation :cost 4 :vp 2 :worker 1 :count 2 :column 2
             :description "During the settler phase, when the owner of an occupied hospice places a plantation or quarry tile on his island, he may take a colonist from the colonist supply and place it on this tile."}
   :office {:type :trade :cost 5 :vp 2 :worker 1 :count 2 :column 2
            :description "When the owner of an occupied office sells a good to the trading house in the trader phase, it need not be different than the goods already there. If the trading house is full, the player cannot sell a good there!"}
   :large-warehouse {:type :storage :cost 6 :vp 2 :worker 1 :count 2 :column 2
                     :description "The owner of an occupied large warehouse may store, at the end of the captain phase, in addition to the single goods barrel he is allowed to store on his wind rose, all the barrels of two kinds of goods that he chooses."}

   ;; Trade Buildings - Column 3
   :factory {:type :production :cost 8 :vp 3 :worker 1 :count 2 :column 3
             :description "If the owner of an occupied factory produces goods of more than one kind in the craftsman phase, he earns money from the bank: for two kinds of goods, he earns 1 doubloon, for three kinds of goods, he earns 2 doubloons, for four kinds of goods, he earns 3 doubloons, and for all five kinds of goods, he earns 5 doubloons."}
   :university {:type :building :cost 7 :vp 3 :worker 1 :count 2 :column 3
                :description "During the builder phase, when the owner of an occupied university builds a building in his city, he may take a colonist from the colonist supply and place it on this tile."}
   :harbor {:type :shipping :cost 8 :vp 3 :worker 1 :count 2 :column 3
            :description "Each time, during the captain phase, the owner of an occupied harbour loads goods on a cargo ship, he earns one extra victory point."}
   :wharf {:type :shipping :cost 9 :vp 3 :worker 1 :count 2 :column 3
           :description "During the captain phase, when a player with an occupied wharf must load goods, instead of loading them on a cargo ship, he may place all goods of one kind in the goods supply and score the appropriate victory points as though he has loaded them on a cargo ship."}

   ;; Large Buildings - Column 4
   :guild-hall {:type :large :cost 10 :vp 4 :worker 1 :count 1 :column 4
                :description "The owner of the occupied guild hall earns, at game end, an additional 1 VP for each small production building (occupied or unoccupied) in his city, and an additional 2 VP for each large production building."}
   :residence {:type :large :cost 10 :vp 4 :worker 1 :count 1 :column 4
               :description "The owner of the occupied residence earns, at game end, additional victory points for the plantations and quarries he has placed on his island. For up to nine filled island spaces, he earns 4 VP, for ten filled island spaces, he earns 5 VP, for eleven filled island spaces, he earns 6 VP, and for all twelve spaces filled, he earns 7 VP."}
   :customs-house {:type :large :cost 10 :vp 4 :worker 1 :count 1 :column 4
                   :description "The owner of the occupied customs house earns, at game end, one additional victory point for every four victory points he acquired during the game."}
   :city-hall {:type :large :cost 10 :vp 4 :worker 1 :count 1 :column 4
               :description "The owner of the occupied city hall earns, at game end, one additional victory point for each violet building and each green building(occupied or unoccupied) in his city."}
   :fortress {:type :large :cost 10 :vp 4 :worker 1 :count 1 :column 4
              :description "The owner of the occupied fortress earns, at game end, one additional victory point for every three colonists on his player board."}})

(def roles [:settler :mayor :builder :craftsman :trader :captain :prospector])

(def plantation-tiles
  {:corn 10 :indigo 12 :sugar 11 :tobacco 9 :coffee 8 :quarry 8})

(defn create-building-supply
  "Create building supply from building definitions with counts"
  [buildings]
  (reduce (fn [supply [building-key building-info]]
            (assoc supply building-key (:count building-info)))
          {}
          buildings))

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
   :building-supply (create-building-supply buildings)
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
