(ns home.core
  (:require [reagent.core :as reagent]
            [secretary.core :as secretary]
            [goog.events :as events]
            [goog.history.EventType :as EventType]
            [home.page.tests :as test-pages]
            [home.routes :as routes])
  (:import goog.History))

(enable-console-print!)

(secretary/set-config! :prefix "#")

(defonce app-state
  (reagent/atom {}))

(defn nav-counter
  [state]
  [:h5 "nav count: "(:nav-count @state 0)])

(defn app
  [state]
  [:div
   [nav-counter state]
   [(:current-page @state) state]])

(defn initialize-state!
  [state]
  (reset! 
   state 
   {:initialized true
    :nav-count 0
    :current-page test-pages/blank-page}))

(defn initialize-secretary!
  [state]
  (let [h (History.)]
    (goog.events/listen
     h EventType/NAVIGATE 
     (fn [e]
       (swap! 
        state 
        update 
        :nav-count inc)
       (secretary/dispatch! (.-token e))))
    (doto h (.setEnabled true))))

(defn initialize-reagent!
  [state]
  (reagent/render-component
   [app state]
   (.getElementById js/document "app")))

(defn setup!
  [state]
  (initialize-state! state)
  (routes/define-routes! state)
  (initialize-secretary! state)
  (initialize-reagent! state))

(when-not (:initialized @app-state)
  (setup! app-state))
