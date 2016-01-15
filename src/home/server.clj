(ns home.server
  (:require [org.httpkit.server :as server]
            [polaris.core :as polaris]
            [clojure.java.io :as io]
            [ring.middleware.resource :refer [wrap-resource]]
            [ring.middleware.content-type :refer [wrap-content-type]]
            [ring.middleware.params :refer [wrap-params]]
            [clojure.data.json :as json]
            [home.server.wordcloud :as cloud]))

(defonce server (atom nil))

(defn client
  [_]
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body (slurp (io/resource "public/index.html"))})

(defn words
  [{{:strs [url]} :params}]
  {:status 200
   :headers {"Content-Type" "application/json"}
   :body (json/write-str (cloud/frequencies url))})

(def routes
  [["/words.json" :words words]
   ["/" :client client]])

(def router
  (-> routes
      polaris/build-routes
      polaris/router
      (wrap-resource "public")
      wrap-content-type
      wrap-params))

(defn stop-server
  []
  (when-not (nil? @server)
    (@server :timeout 100)
    (reset! server nil)))

(defn start-server
  []
  (stop-server)
  (let [port (Integer/parseInt (get (System/getenv) "PORT" "5000"))
        server* (server/run-server #'router {:port port})]
    (reset! server server*)))

(defn -main
  []
  (start-server))
