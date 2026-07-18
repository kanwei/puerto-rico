(ns puerto-rico.ai.mcts
  "Open-loop Monte Carlo Tree Search with PUCT selection and per-player value
   vectors (max^n backup), in the AlphaZero style.

   Open-loop: tree nodes are keyed by ACTION SEQUENCES, not states. Every
   simulation re-derives the state by replaying the selected actions from the
   root, with the face-down plantation deck freshly shuffled first. That makes
   the deck's hidden order a chance event the search averages over instead of
   something it can peek at, and it handles all other engine stochasticity
   (discard reshuffles) for free.

   The evaluator is pluggable: `rollout-evaluator` (random playout, works today
   with no network) or a neural-network evaluator returning {:priors, :value}.
   Values are vectors with one entry per seat (absolute seat order); each node
   selects with the component of the player to move at that node."
  (:require [puerto-rico.game.state :as state]
            [puerto-rico.ai.actions :as actions]))

;; --------------------------------------------------------------------------
;; Random sampling helpers (Box-Muller normal, Marsaglia-Tsang gamma)
;; --------------------------------------------------------------------------

(defn- rand-normal []
  (let [u (max 1e-12 (rand))
        v (rand)]
    (* (Math/sqrt (* -2.0 (Math/log u)))
       (Math/cos (* 2.0 Math/PI v)))))

(defn- rand-gamma [alpha]
  (if (< alpha 1.0)
    ;; boost: gamma(a) = gamma(a+1) * U^(1/a)
    (* (rand-gamma (inc alpha))
       (Math/pow (max 1e-12 (rand)) (/ 1.0 alpha)))
    (let [d (- alpha (/ 1.0 3.0))
          c (/ 1.0 (Math/sqrt (* 9.0 d)))]
      (loop []
        (let [x (rand-normal)
              t (+ 1.0 (* c x))]
          (if (<= t 0.0)
            (recur)
            (let [v (* t t t)
                  u (max 1e-12 (rand))]
              (cond
                (< u (- 1.0 (* 0.0331 x x x x))) (* d v)
                (< (Math/log u)
                   (+ (* 0.5 x x) (* d (+ (- 1.0 v) (Math/log v))))) (* d v)
                :else (recur)))))))))

(defn- rand-dirichlet [alpha n]
  (let [xs (repeatedly n #(rand-gamma alpha))
        total (reduce + xs)]
    (mapv #(/ % (max 1e-12 total)) xs)))

;; --------------------------------------------------------------------------
;; Outcomes and rollouts
;; --------------------------------------------------------------------------

(defn outcome-vector
  "Per-seat value of a state. Finished game: 1.0 for the winner (ties split).
   Unfinished (rollout cap): each player's share of total victory points."
  [game-state]
  (let [players (:players game-state)
        n (count players)]
    (if (:game-over game-state)
      (let [winner-id (get-in game-state [:winner :id])]
        (mapv #(if (= (:id %) winner-id) 1.0 0.0) players))
      (let [scores (mapv #(double (state/calculate-victory-points %)) players)
            total (reduce + scores)]
        (if (pos? total)
          (mapv #(/ % total) scores)
          (vec (repeat n (/ 1.0 n))))))))

(defn- random-playout [game-state max-steps]
  (loop [gs game-state, steps 0]
    (if (or (:game-over gs) (>= steps max-steps))
      gs
      (let [ids (actions/legal-action-ids gs)]
        (if (empty? ids)
          gs
          (recur (actions/apply-action gs (rand-nth ids)) (inc steps)))))))

(defn rollout-evaluator
  "Evaluator that plays the game out with uniformly random actions.
   Uniform priors; value from the playout's outcome."
  [& {:keys [max-steps] :or {max-steps 600}}]
  (fn [game-state legal-ids]
    {:priors (let [p (/ 1.0 (max 1 (count legal-ids)))]
               (into {} (map #(vector % p) legal-ids)))
     :value (outcome-vector (random-playout game-state max-steps))}))

;; --------------------------------------------------------------------------
;; PUCT search
;; --------------------------------------------------------------------------
;; Node: {:P {id prior} :N {id visits} :W {id value-sum-vector} :children {id node}}

(defn- select-action-puct [node legal-ids mover c-puct]
  (let [total-n (reduce + 0 (map #(get-in node [:N %] 0) legal-ids))
        sqrt-n (Math/sqrt (+ 1.0 total-n))
        uniform (/ 1.0 (count legal-ids))]
    (apply max-key
           (fn [id]
             (let [n (get-in node [:N id] 0)
                   q (if (pos? n)
                       (/ (nth (get-in node [:W id]) mover) n)
                       0.0)
                   p (get-in node [:P id] uniform)]
               (+ q (* c-puct p (/ sqrt-n (inc n))))))
           legal-ids)))

(defn- simulate
  "One simulation: descend the tree replaying actions on gs, expand a leaf,
   return [value-vector updated-node]."
  [node gs {:keys [evaluate c-puct] :as opts}]
  (cond
    (:game-over gs)
    [(outcome-vector gs) node]

    ;; unexpanded leaf: evaluate and stop
    (nil? (:P node))
    (let [ids (actions/legal-action-ids gs)
          {:keys [priors value]} (evaluate gs ids)]
      [value (assoc node :P priors :N {} :W {} :children {})])

    :else
    (let [ids (actions/legal-action-ids gs)]
      (if (empty? ids)
        [(outcome-vector gs) node]
        (let [mover (actions/actor-index gs)
              id (select-action-puct node ids mover c-puct)
              gs' (actions/apply-action gs id)
              [value child'] (simulate (get-in node [:children id] {}) gs' opts)]
          [value
           (-> node
               (assoc-in [:children id] child')
               (update-in [:N id] (fnil inc 0))
               (update-in [:W id] (fnil #(mapv + % value)
                                        (vec (repeat (count value) 0.0)))))])))))

(defn- add-root-noise [node legal-ids alpha frac]
  (let [noise (rand-dirichlet alpha (count legal-ids))
        uniform (/ 1.0 (count legal-ids))]
    (update node :P
            (fn [priors]
              (into {} (map (fn [id eta]
                              [id (+ (* (- 1.0 frac) (get priors id uniform))
                                     (* frac eta))])
                            legal-ids noise))))))

(def default-opts
  {:simulations 200
   :c-puct 1.5
   :dirichlet-alpha 1.0
   :dirichlet-frac 0.0    ;; set to ~0.25 during self-play
   :evaluate nil})        ;; nil -> rollout-evaluator

(defn mcts-search
  "Run MCTS from game-state for the player who must act now.
   Returns {:visits {id n} :policy {id prob} :root node}."
  [game-state opts]
  (let [{:keys [simulations c-puct dirichlet-alpha dirichlet-frac evaluate]}
        (merge default-opts opts)
        evaluate (or evaluate (rollout-evaluator))
        opts* {:evaluate evaluate :c-puct c-puct}
        legal-ids (actions/legal-action-ids game-state)
        determinize #(update % :plantation-supply (comp vec shuffle))
        ;; expand the root once, then optionally mix in Dirichlet noise
        [_ root] (simulate {} (determinize game-state) opts*)
        root (if (pos? dirichlet-frac)
               (add-root-noise root legal-ids dirichlet-alpha dirichlet-frac)
               root)
        root (reduce (fn [node _]
                       (second (simulate node (determinize game-state) opts*)))
                     root
                     (range simulations))
        visits (into {} (map #(vector % (get-in root [:N %] 0)) legal-ids))
        total (reduce + 0 (vals visits))]
    {:visits visits
     :policy (if (pos? total)
               (into {} (map (fn [[id n]] [id (/ (double n) total)]) visits))
               (into {} (map #(vector % (/ 1.0 (count legal-ids))) legal-ids)))
     :root root}))

(defn sample-action
  "Pick an action from visit counts. temperature 0 = argmax; higher values
   sample proportionally to n^(1/temperature)."
  [visits temperature]
  (if (or (nil? temperature) (< temperature 1e-6))
    (key (apply max-key val visits))
    (let [weighted (map (fn [[id n]]
                          [id (Math/pow (double n) (/ 1.0 temperature))])
                        visits)
          total (reduce + (map second weighted))
          r (* (rand) total)]
      (loop [[[id w] & more] weighted, acc 0.0]
        (let [acc (+ acc w)]
          (if (or (>= acc r) (empty? more))
            id
            (recur more acc)))))))

(defn ai-select-move
  "Drop-in AI interface: pick a move via MCTS and return it in the engine's
   move format. Returns nil when there is nothing to decide."
  [game-state player-id & [opts]]
  (let [legal-ids (actions/legal-action-ids game-state)]
    (when (and (seq legal-ids)
               (= player-id (:id (actions/actor-player game-state))))
      (let [id (if (= 1 (count legal-ids))
                 (first legal-ids)   ;; forced move - skip the search
                 (-> (mcts-search game-state opts)
                     :visits
                     (sample-action (:temperature opts))))]
        (actions/action->move game-state id)))))
