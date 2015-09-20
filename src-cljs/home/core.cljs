(ns home.core
  (:require [reagent.core :as reagent]
            [secretary.core :as secretary]
            [goog.events :as events]
            [goog.history.EventType :as EventType]
            [home.routes :as routes]
            [home.component :as component])
  (:import goog.History))

(defn navigate
  [state token]
  (swap! state update :nav-count inc)
  (secretary/dispatch! token))

(defn initialize-state!
  [state]
  (reset! 
   state 
   {:show-inspector? false
    :initialized true
    :nav-count 0
    :current-page (constantly [:div])}))

(defn initialize-secretary!
  [state history]
  (goog.events/listen
   history EventType/NAVIGATE 
   #(navigate state (.-token %)))
  (doto history (.setEnabled true)))

(defn initialize-reagent!
  [state]
  (reagent/render-component
   [component/app state]
   (.getElementById js/document "app")))

(defonce load
  (let [state (reagent/atom {})
        history (History.)]
    (enable-console-print!)
    (secretary/set-config! :prefix "#")
    (initialize-state! state)
    (routes/define-routes! state)
    (initialize-secretary! state history)
    (initialize-reagent! state)
    (fn figwheel-reload-fn
      []
      (navigate state (.getToken history)))))
