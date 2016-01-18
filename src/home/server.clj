(ns home.server
  (:require [org.httpkit.server :as server]
            [polaris.core :as polaris]
            [clojure.java.io :as io]
            [ring.middleware.resource :refer [wrap-resource]]
            [ring.middleware.content-type :refer [wrap-content-type]]
            [ring.middleware.params :refer [wrap-params]]
            [ring.middleware.cors :refer [wrap-cors]]
            [clojure.data.json :as json]
            [home.server.wordcloud :as cloud]))

(defonce server (atom nil))

(defn html
  [resource]
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body (slurp (io/resource resource))})

(defn client
  [_]
  (html "public/index.html"))

(defn resume
  [_]
  (html "static/resume.html"))

(defn words
  [{{:strs [url]} :params}]
  {:status 200
   :headers {"Content-Type" "application/json"}
   :body (json/write-str (cloud/frequencies url))})

(def routes
  [["/words.json" :words words]
   ["/resume" :resume resume]
   ["/" :client client]])

(def router
  (-> routes
      polaris/build-routes
      polaris/router
      (wrap-resource "public")
      wrap-content-type
      wrap-params
      (wrap-cors :access-control-allow-origin [#"http://localhost:5000" #"https://akjetma.github.io"]
                 :access-control-allow-methods [:get :put :post :delete])))

(defn stop-server
  []
  (when-not (nil? @server)
    (@server :timeout 100)
    (println "server stopped")
    (reset! server nil)))

(defn start-server
  []
  (stop-server)
  (let [port (Integer/parseInt (get (System/getenv) "PORT" "5000"))
        server* (server/run-server #'router {:port port})]
    (println "server started on port" port)
    (reset! server server*)))

(defn -main
  []
  (start-server))
