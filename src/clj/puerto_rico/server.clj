(ns puerto-rico.server
  "HTTP server for Puerto Rico game"
  (:require [ring.adapter.jetty :refer [run-jetty]]
            [ring.middleware.json :refer [wrap-json-response wrap-json-body]]
            [ring.middleware.cors :refer [wrap-cors]]
            [reitit.ring :as reitit-ring]
            [reitit.ring.middleware.parameters :as parameters]
            [reitit.coercion.spec :as spec-coercion]
            [reitit.ring.coercion :as coercion]
            [cheshire.core :as json]
            [puerto-rico.game.state :as state]
            [puerto-rico.game.rules :as rules]
            [puerto-rico.ai.mcts :as ai]))

;; Game session storage (in production, use a proper database)
(def game-sessions (atom {}))

;; Handler functions
(defn root-handler [_request]
  {:status 200
   :headers {"content-type" "text/plain"}
   :body "Puerto Rico Game Server"})

(defn create-game-handler [_request]
  (let [game-id (str (java.util.UUID/randomUUID))
        players [(state/new-player 1 "Player 1")
                 (assoc (state/new-player 2 "AI Easy") :is-ai true :difficulty :easy)
                 (assoc (state/new-player 3 "AI Hard") :is-ai true :difficulty :hard)]
        game-state (state/new-game-state players)]
    (swap! game-sessions assoc game-id game-state)
    {:status 200
     :body {:game-id game-id
            :game-state game-state}}))

(defn get-game-handler [{:keys [path-params]}]
  (let [game-id (:id path-params)
        game-state (get @game-sessions game-id)]
    (if game-state
      {:status 200 :body {:game-state game-state}}
      {:status 404 :body {:error "Game not found"}})))

(defn make-move-handler [{:keys [path-params body]}]
  (let [game-id (:id path-params)
        move body
        game-state (get @game-sessions game-id)]
    (if game-state
      (if (rules/valid-move? game-state (:player-id move) move)
        (let [new-game-state (rules/apply-move game-state move)]
          (swap! game-sessions assoc game-id new-game-state)
          {:status 200 :body {:game-state new-game-state}})
        {:status 400 :body {:error "Invalid move"}})
      {:status 404 :body {:error "Game not found"}})))

(defn ai-turn-handler [{:keys [path-params]}]
  (let [game-id (:id path-params)
        game-state (get @game-sessions game-id)
        current-player (state/current-player game-state)]
    (if (and game-state (:is-ai current-player))
      (let [ai-move (ai/ai-select-move game-state
                                       (:id current-player)
                                       (:difficulty current-player :medium))
            new-game-state (if ai-move
                             (rules/apply-move game-state ai-move)
                             game-state)]
        (swap! game-sessions assoc game-id new-game-state)
        {:status 200 :body {:game-state new-game-state :ai-move ai-move}})
      {:status 400 :body {:error "Current player is not AI"}})))

;; Route definitions
(def routes
  [["/" {:get root-handler}]
   ["/api"
    ["/games"
     ["" {:post create-game-handler}]
     ["/:id" {:get get-game-handler
              :parameters {:path {:id string?}}}]
     ["/:id/moves" {:post make-move-handler
                    :parameters {:path {:id string?}}}]
     ["/:id/ai-turn" {:post ai-turn-handler
                      :parameters {:path {:id string?}}}]]]])

;; Ring handler
(def app
  (reitit-ring/ring-handler
   (reitit-ring/router routes
                       {:data {:coercion spec-coercion/coercion
                               :middleware [parameters/parameters-middleware
                                            coercion/coerce-request-middleware
                                            coercion/coerce-response-middleware]}})
   (reitit-ring/routes
    (reitit-ring/create-resource-handler {:path "/"})
    (reitit-ring/create-default-handler))))

;; Wrap the app with JSON and CORS middleware
(def wrapped-app
  (-> app
      (wrap-json-body {:keywords? true})
      wrap-json-response
      (wrap-cors :access-control-allow-origin [#".*"]
                 :access-control-allow-methods [:get :post :put :delete]
                 :access-control-allow-headers ["Content-Type"])))

(defn start-server [& {:keys [port] :or {port 8080}}]
  (println (str "Starting Puerto Rico server on port " port))
  (run-jetty wrapped-app {:port port :join? false}))

(defn -main [& args]
  (let [port (if (seq args) (Integer/parseInt (first args)) 8080)]
    (start-server :port port)))
