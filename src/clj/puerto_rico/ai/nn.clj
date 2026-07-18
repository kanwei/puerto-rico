(ns puerto-rico.ai.nn
  "ONNX inference bridge: load a network trained by train/train.py and expose
   it as an MCTS evaluator (JVM only, in-process, no Python needed at play
   time).

   Usage:
     (def model (load-model \"models/pr.onnx\"))
     (mcts/mcts-search game-state {:evaluate (evaluator model)})"
  (:require [puerto-rico.ai.actions :as actions]
            [puerto-rico.ai.encoder :as encoder])
  (:import (ai.onnxruntime OnnxTensor OrtEnvironment OrtSession$Result)))

(defn load-model [onnx-path]
  (let [env (OrtEnvironment/getEnvironment)]
    {:env env
     :session (.createSession env ^String onnx-path)}))

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

(defn evaluator
  "MCTS evaluator backed by an ONNX network. The network sees the state and
   predicts values egocentrically (index 0 = the acting player); MCTS wants
   absolute seat order, so the value vector is rotated back."
  [{:keys [env session]}]
  (fn [game-state legal-ids]
    (let [encoded (float-array (encoder/encode-state game-state))
          input ^"[[F" (into-array [(float-array encoded)])]
      (with-open [tensor (OnnxTensor/createTensor env input)
                  ^OrtSession$Result result (.run session {"state" tensor})]
        (let [outputs (into {} (map (fn [e] [(.getKey e) (.getValue e)]) result))
              policy-logits (vec (first ^"[[F" (.getValue (outputs "policy_logits"))))
              value-logits (vec (first ^"[[F" (.getValue (outputs "value_logits"))))
              value-ego (softmax value-logits)
              n (count value-ego)
              actor (actions/actor-index game-state)
              ;; egocentric -> absolute seats: abs[(actor+k) mod n] = ego[k]
              value-abs (reduce (fn [v k]
                                  (assoc v (mod (+ actor k) n) (nth value-ego k)))
                                (vec (repeat n 0.0))
                                (range n))]
          {:priors (masked-softmax policy-logits legal-ids)
           :value value-abs})))))
