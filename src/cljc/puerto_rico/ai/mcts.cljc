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
;;
;; On the JVM `rand`/`rand-nth`/`shuffle` all funnel through one synchronized
;; global generator, which becomes a lock-contention bottleneck when many
;; self-play games run in parallel. These helpers use a per-thread RNG so each
;; worker thread draws without contending. CLJS falls back to the built-ins.
;; --------------------------------------------------------------------------

(defn drand
  "Uniform double in [0,1), contention-free per thread on the JVM"
  []
  #?(:clj (.nextDouble (java.util.concurrent.ThreadLocalRandom/current))
     :cljs (rand)))

(defn drand-int [n]
  #?(:clj (.nextInt (java.util.concurrent.ThreadLocalRandom/current) (int n))
     :cljs (rand-int n)))

(defn drand-nth [coll]
  (nth coll (drand-int (count coll))))

(defn dshuffle [coll]
  #?(:clj (let [a (java.util.ArrayList. ^java.util.Collection coll)]
            (java.util.Collections/shuffle a (java.util.concurrent.ThreadLocalRandom/current))
            (vec a))
     :cljs (shuffle coll)))

(defn- rand-normal []
  (let [u (max 1e-12 (drand))
        v (drand)]
    (* (Math/sqrt (* -2.0 (Math/log u)))
       (Math/cos (* 2.0 Math/PI v)))))

(defn- rand-gamma [alpha]
  (if (< alpha 1.0)
    ;; boost: gamma(a) = gamma(a+1) * U^(1/a)
    (* (rand-gamma (inc alpha))
       (Math/pow (max 1e-12 (drand)) (/ 1.0 alpha)))
    (let [d (- alpha (/ 1.0 3.0))
          c (/ 1.0 (Math/sqrt (* 9.0 d)))]
      (loop []
        (let [x (rand-normal)
              t (+ 1.0 (* c x))]
          (if (<= t 0.0)
            (recur)
            (let [v (* t t t)
                  u (max 1e-12 (drand))]
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

(defn- vp-shares
  "Each seat's share of the total victory points (uniform split if nobody has
   scored yet)."
  [game-state]
  (let [players (:players game-state)
        n (count players)
        scores (mapv #(double (state/calculate-victory-points %)) players)
        total (reduce + scores)]
    (if (pos? total)
      (mapv #(/ % total) scores)
      (vec (repeat n (/ 1.0 n))))))

(defn outcome-vector
  "Canonical per-seat game result. Finished game: 1.0 for the winner, 0.0 for
   everyone else (the engine already broke ties, so exactly one seat is 1.0).
   Unfinished (rollout cap): each seat's share of total victory points.
   This stays a clean win/loss signal because it is the value-head training
   target AND how self-play identifies the winner - do NOT blend margin in here
   (doing so made `(= 1.0 outcome)` winner detection fail => 'winner seat null')."
  [game-state]
  (if (:game-over game-state)
    (let [winner-id (get-in game-state [:winner :id])]
      (mapv #(if (= (:id %) winner-id) 1.0 0.0) (:players game-state)))
    (vp-shares game-state)))

;; How much the MCTS search reward weights raw VP share vs. purely winning.
;; 0.2 => winning is 80% of the reward, relative points the other 20%.
(def ^:const vp-weight 0.2)

(defn outcome-utility
  "Per-seat reward that MCTS backs up. Identical to `outcome-vector` for an
   unfinished state, but for a finished game it blends win/loss with each seat's
   VP share (weight `vp-weight`), so the search keeps trying to shrink the gap
   (or grow the lead) even once the win/loss is decided - the point of 'when
   losing, still reduce the point difference'. Kept SEPARATE from
   `outcome-vector` so winner detection and the value-head target stay pure."
  [game-state]
  (if (:game-over game-state)
    (let [winner-id (get-in game-state [:winner :id])
          win-weight (- 1.0 vp-weight)
          shares (vp-shares game-state)]
      (mapv (fn [p v] (+ (* win-weight (if (= (:id p) winner-id) 1.0 0.0))
                         (* vp-weight v)))
            (:players game-state) shares))
    (vp-shares game-state)))

(defn- random-playout [game-state max-steps]
  (loop [gs game-state, steps 0]
    (if (or (:game-over gs) (>= steps max-steps))
      gs
      (let [ids (actions/legal-action-ids gs)]
        (if (empty? ids)
          gs
          (recur (actions/apply-action gs (drand-nth ids)) (inc steps)))))))

(defn rollout-evaluator
  "Evaluator that plays the game out with uniformly random actions.
   Uniform priors; value from the playout's outcome."
  [& {:keys [max-steps] :or {max-steps 600}}]
  (fn [game-state legal-ids]
    {:priors (let [p (/ 1.0 (max 1 (count legal-ids)))]
               (into {} (map #(vector % p) legal-ids)))
     :value (outcome-utility (random-playout game-state max-steps))}))

;; --------------------------------------------------------------------------
;; Heuristic prior blending
;; --------------------------------------------------------------------------

(defn blend-heuristic-priors
  "Wrap an evaluator to blend heuristic action scores into the priors it
   returns.  `heuristic-lambda` controls heuristic strength: 0.0 = pure
   evaluator, 1.0 = pure heuristic.

   Recommended: 0.15-0.35 for rollout MCTS (no network knowledge),
   0.05-0.15 when a trained network already encodes some matching logic.

   Blending happens in probability space:
     P_combined = (1 - lambda) * P_eval + lambda * P_heuristic

   This nudges MCTS to explore heuristic-good moves earlier while the
   evaluator still determines the overall shape.  As visits accumulate,
   Q-values dominate and the heuristic influence fades — the 'nudge but
   allow exploration' behavior."
  [evaluate {:keys [heuristic-lambda] :or {heuristic-lambda 0.2}}]
  (fn [game-state legal-ids]
    (let [result (evaluate game-state legal-ids)
          heuristic (actions/heuristic-action-scores game-state)]
      (if (nil? heuristic)
        result
        (let [eval-priors (:priors result)
              ;; Normalize heuristic scores to a proper distribution
              h-sum (reduce + 0 (vals heuristic))
              h-prior (when (pos? h-sum)
                        (into {} (map (fn [[id s]] [id (/ s h-sum)]) heuristic))
                        {})
              uniform (/ 1.0 (count legal-ids))]
          (assoc result :priors
                 (into {} (map (fn [id]
                                 [id (+ (* (- 1.0 heuristic-lambda)
                                           (get eval-priors id uniform))
                                        (* heuristic-lambda
                                           (get h-prior id uniform)))])
                               legal-ids))))))))

;; --------------------------------------------------------------------------
;; PUCT search
;; --------------------------------------------------------------------------
;; Node: {:P {id prior} :N {id visits} :W {id value-sum-vector} :children {id node}}

(defn- select-action-puct [node legal-ids mover c-puct]
  ;; total child visits is tracked on the node (:visits) - incremented once per
  ;; descent - so we avoid re-summing every child's N on every node visit
  (let [total-n (get node :visits 0)
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
    [(outcome-utility gs) node]

    ;; unexpanded leaf: evaluate and stop
    (nil? (:P node))
    (let [ids (actions/legal-action-ids gs)
          {:keys [priors value]} (evaluate gs ids)]
      [value (assoc node :P priors :N {} :W {} :children {})])

    :else
    (let [ids (actions/legal-action-ids gs)]
      (if (empty? ids)
        [(outcome-utility gs) node]
        (let [mover (actions/actor-index gs)
              id (select-action-puct node ids mover c-puct)
              gs' (actions/apply-action gs id)
              [value child'] (simulate (get-in node [:children id] {}) gs' opts)]
          [value
           (-> node
               (assoc-in [:children id] child')
               (update :visits (fnil inc 0))     ;; total child visits (for PUCT)
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
  {:simulations 300
   :c-puct 1.5
   :dirichlet-alpha 1.0
   :dirichlet-frac 0.0    ;; set to ~0.25 during self-play
   :evaluate nil})        ;; nil -> heuristic-blended rollout-evaluator

(defn mcts-search
  "Run MCTS from game-state for the player who must act now.
   Returns {:visits {id n} :policy {id prob} :root node}."
  [game-state opts]
  (let [{:keys [simulations c-puct dirichlet-alpha dirichlet-frac evaluate]}
        (merge default-opts opts)
        evaluate (or evaluate (blend-heuristic-priors (rollout-evaluator) {}))
        opts* {:evaluate evaluate :c-puct c-puct}
        legal-ids (actions/legal-action-ids game-state)
        determinize #(update % :plantation-supply dshuffle)
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
          r (* (drand) total)]
      (loop [[[id w] & more] weighted, acc 0.0]
        (let [acc (+ acc w)]
          (if (or (>= acc r) (empty? more))
            id
            (recur more acc)))))))

(defn search-stats
  "Compact, transport-friendly diagnostics for a finished search: the
   most-visited actions with their policy :weight (visit share). Cheap - derived
   entirely from visit counts the search already produced, with no extra
   evaluation."
  [game-state {:keys [visits]} & [n-top]]
  (let [total  (reduce + 0 (vals visits))
        ranked (->> visits (filter (comp pos? val)) (sort-by val >))]
    {:candidates (mapv (fn [[id n]]
                         {:move   (actions/action->move game-state id)
                          :weight (if (pos? total) (/ (double n) total) 0.0)})
                       (take (or n-top 4) ranked))}))

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

(defn ai-decide
  "Like `ai-select-move`, but also returns the search diagnostics so callers can
   surface how confident the AI is: {:move <move> :stats <search-stats>}.
   A forced move (single legal action) skips the search and carries no :stats.
   Returns nil when there is nothing to decide."
  [game-state player-id & [opts]]
  (let [legal-ids (actions/legal-action-ids game-state)]
    (when (and (seq legal-ids)
               (= player-id (:id (actions/actor-player game-state))))
      (if (= 1 (count legal-ids))
        {:move (actions/action->move game-state (first legal-ids))}
        (let [result (mcts-search game-state opts)
              id     (sample-action (:visits result) (:temperature opts))]
          {:move  (actions/action->move game-state id)
           :stats (search-stats game-state result)})))))
