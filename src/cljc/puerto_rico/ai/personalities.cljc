(ns puerto-rico.ai.personalities
  "AI personality system for varied play styles"
  (:require [puerto-rico.game.state :as state]))

(defn random-score []
  "Generate a random score between 0 and 100"
  (rand-int 101))

;; Shipping-focused personality (Captain strategy)
(defn shipper-role-weights [game-state player-id]
  {:settler 30 ; Need plantations for production
   :mayor 40 ; Need colonists for production
   :builder 20 ; Buildings are secondary
   :craftsman 80 ; Production is key
   :trader 10 ; Trading competes with shipping
   :captain 100 ; Primary goal
   :prospector 25})

(defn shipper-plantation-score [game-state player plantation-type]
  (if (= plantation-type :quarry)
    10 ; Low priority on quarries
    (case plantation-type
      :corn 60 ; No building needed
      :indigo 50 ; Cheap to produce
      :sugar 40 ; Good production
      :tobacco 35 ; Expensive but valuable
      :coffee 35 ; Expensive but valuable
      20)))

(defn shipper-building-score [game-state player building-key building-info]
  (case (:type building-info)
    :production 80 ; Production buildings are essential
    :processing 30 ; Some processing helps
    :trade 10 ; Trading buildings not important
    :large 40 ; Late game consideration
    20))

;; Building-focused personality (Builder strategy)
(defn builder-role-weights [game-state player-id]
  {:settler 40 ; Need quarries for discounts
   :mayor 30 ; Need colonists for buildings
   :builder 100 ; Primary goal
   :craftsman 20 ; Production is secondary
   :trader 60 ; Money for buildings
   :captain 30 ; Some VPs from shipping
   :prospector 50}) ; Money helps

(defn builder-plantation-score [game-state player plantation-type]
  (if (= plantation-type :quarry)
    90 ; Quarries give building discounts
    (case plantation-type
      :corn 30 ; Basic production
      :indigo 40 ; Pairs with indigo plant
      :sugar 50 ; Pairs with sugar mill
      :tobacco 60 ; High value for trading
      :coffee 60 ; High value for trading
      20)))

(defn builder-building-score [game-state player building-key building-info]
  (let [vp (:vp building-info)]
    (+ (* vp 20) ; Prioritize VP value
       (case (:type building-info)
         :production 30
         :processing 40
         :trade 50 ; Trade buildings help get money
         :large 100 ; Large buildings are the goal
         10))))

;; Random personality
(defn random-role-weights [game-state player-id]
  {:settler (random-score)
   :mayor (random-score)
   :builder (random-score)
   :craftsman (random-score)
   :trader (random-score)
   :captain (random-score)
   :prospector (random-score)})

(defn random-plantation-score [game-state player plantation-type]
  (random-score))

(defn random-building-score [game-state player building-key building-info]
  (random-score))

;; Personality selection and dispatch
(defn get-personality-functions [personality]
  (case personality
    :shipper {:role-weights shipper-role-weights
              :plantation-score shipper-plantation-score
              :building-score shipper-building-score
              :trade-multiplier 0.5
              :ship-multiplier 2.0}
    :builder {:role-weights builder-role-weights
              :plantation-score builder-plantation-score
              :building-score builder-building-score
              :trade-multiplier 1.5
              :ship-multiplier 0.7}
    :random {:role-weights random-role-weights
             :plantation-score random-plantation-score
             :building-score random-building-score
             :trade-multiplier 1.0
             :ship-multiplier 1.0}
    ;; Default to random
    {:role-weights random-role-weights
     :plantation-score random-plantation-score
     :building-score random-building-score
     :trade-multiplier 1.0
     :ship-multiplier 1.0}))

(defn assign-random-personality []
  "Randomly assign a personality to an AI player"
  (rand-nth [:shipper :builder :random]))

(defn personality-name [personality]
  (case personality
    :shipper "Captain"
    :builder "Builder"
    :random "Chaos"
    "AI"))