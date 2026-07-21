(ns puerto-rico.ai.nn
  "ONNX inference bridge: load a network trained by train/train.py and expose
   it as an MCTS evaluator (JVM only, in-process, no Python needed at play
   time).

   Usage:
     (def model (load-model \"models/pr.onnx\"))
     (mcts/mcts-search game-state {:evaluate (evaluator model)})"
  (:require [puerto-rico.ai.actions :as actions]
            [puerto-rico.ai.encoder :as encoder])
  (:import (ai.onnxruntime OnnxTensor OrtEnvironment OrtSession$Result TensorInfo)))

(defn load-model [onnx-path]
  (let [env (OrtEnvironment/getEnvironment)
        session (.createSession env ^String onnx-path)
        ;; the "state" input's fixed feature dimension (last axis; axis 0 is batch)
        info ^TensorInfo (.getInfo (val (first (.getInputInfo session))))
        shape (.getShape info)
        input-dim (int (aget shape (dec (alength shape))))]
    {:env env :session session :path onnx-path :input-dim input-dim}))

(defn- masked-softmax
  "Softmax over the logits at legal-ids only; returns {id prob}"
  [logits legal-ids]
  (let [selected (map #(nth logits %) legal-ids)
        m (apply max selected)
        exps (map #(Math/exp (- % m)) selected)
        total (reduce + exps)]
    (into {} (map (fn [id e] [id (/ e total)]) legal-ids exps))))

(defn- softmax [xs]
  (let [m (apply max xs)
        exps (mapv #(Math/exp (- % m)) xs)
        total (reduce + exps)]
    (mapv #(/ % total) exps)))

(def default-utility-c
  "Weight of the score-margin term in the MCTS utility blend U = Vwin + c*margin.
   The network's margin output is already in tens-of-points, so a small c keeps
   the win term dominant while still favoring bigger point leads among branches
   of equal win probability (sharp endgames, underdog clawback)."
  0.05)

(defn- rotate-ego->abs
  "Map an egocentric per-seat vector (index 0 = actor) back to absolute seats"
  [ego actor n]
  (reduce (fn [v k] (assoc v (mod (+ actor k) n) (nth ego k)))
          (vec (repeat n 0.0))
          (range n)))

(defn evaluator
  "MCTS evaluator backed by an ONNX network. Predicts, egocentrically (index
   0 = the acting player):
     - policy priors over the action space
     - win probability per seat (value head)
     - score margin per seat (score-margin head, in tens of points)
   The value MCTS backs up is the UTILITY blend U = Vwin + c*margin, so the
   search keeps maximizing point margin even when the win/loss is decided.
   Everything is rotated from egocentric to absolute seat order for the tree."
  [{:keys [env session input-dim path]} & [{:keys [utility-c] :or {utility-c default-utility-c}}]]
  (fn [game-state legal-ids]
    (let [n-players (count (:players game-state))
          state-vec (encoder/encode-state game-state)]
      ;; A model's input width is fixed to the player count it was trained on
      ;; (the egocentric encoding grows with players). Fail clearly instead of
      ;; letting ONNX throw a cryptic "invalid dimensions for input: state".
      (when (and input-dim (not= (count state-vec) input-dim))
        (throw (ex-info (str "Model " (or path "?") " expects " input-dim
                             " inputs but this " n-players "-player state encodes to "
                             (count state-vec) ". A model only works for the player "
                             "count it was trained on (and the encoder version). "
                             "Use a matching " (cond (= input-dim 249) "2" (= input-dim 327) "3"
                                                     (= input-dim 397) "4" (= input-dim 467) "5"
                                                     :else "N")
                             "-player game, or retrain for this player count.")
                        {:input-dim input-dim :got (count state-vec) :players n-players})))
      (let [input ^"[[F" (into-array [(float-array state-vec)])]
        (with-open [tensor (OnnxTensor/createTensor env input)
                    ^OrtSession$Result result (.run session {"state" tensor})]
          (let [outputs (into {} (map (fn [e] [(.getKey e) (.getValue e)]) result))
                policy-logits (vec (first ^"[[F" (.getValue (outputs "policy_logits"))))
                value-logits (vec (first ^"[[F" (.getValue (outputs "value_logits"))))
                margins-ego (vec (first ^"[[F" (.getValue (outputs "score_margins"))))
                win-ego (softmax value-logits)
                n (count win-ego)
                ;; blend: win probability plus the margin term (margins sum ~0,
                ;; so utility still sums ~1 across seats)
                utility-ego (mapv (fn [w m] (+ w (* utility-c m))) win-ego margins-ego)
                actor (actions/actor-index game-state)]
            {:priors (masked-softmax policy-logits legal-ids)
             :value (rotate-ego->abs utility-ego actor n)}))))))
