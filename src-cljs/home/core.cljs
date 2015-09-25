(ns home.core
  (:require [reagent.core :as reagent]
            [secretary.core :as secretary]
            [goog.events :as events]
            [goog.history.EventType :as EventType]
            [home.routes :as routes]
            [home.component :as component]
            [home.support :as support])
  (:import goog.History))

(defn navigate
  [state token]
  (swap! state update :nav-count inc)
  (secretary/dispatch! token))

(defn initialize-state!
  [state]
  (let [browser (support/browser-type)]
    (reset! 
     state 
     {:browser browser
      :things (get support/browser-things browser)
      :show-inspector? false
      :initialized true
      :nav-count 0})))

(defn initialize-secretary!
  [state history]
  (secretary/set-config! :prefix "#")  
  (goog.events/listen
   history EventType/NAVIGATE 
   #(navigate state (.-token %)))
  (doto history (.setEnabled true)))

(defn initialize-reagent!
  [state]
  (reagent/render-component
   [component/app state]
   (.-body js/document)))

(defonce load
  (let [state (reagent/atom {})
        history (History.)]
    (enable-console-print!)
    (initialize-state! state)
    (routes/define-routes! state)
    (initialize-secretary! state history)
    (initialize-reagent! state)
    (fn figwheel-reload-fn
      []
      (navigate state (.getToken history)))))
