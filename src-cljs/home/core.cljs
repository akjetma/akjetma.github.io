(ns home.core
  (:require [reagent.core :as reagent]
            [secretary.core :as secretary]
            [goog.events :as events]
            [goog.history.EventType :as EventType]
            [home.routes :as routes])
  (:import goog.History))

(enable-console-print!)

(secretary/set-config! :prefix "#")

(defonce app-state
  (reagent/atom {}))

(defn blank-page
  [state]
  [:div])

(defn inspector-toggle
  [state]
  [:div#inspector-toggle
   {:on-click #(swap! state update :show-inspector? not)}
   "\u2603"])

(defn state-inspector
  [state]
  (let [shown? (:show-inspector? @state)]
    [:div#state-inspector
     {:style {:height (if shown? "100px" "0px")}}
     [inspector-toggle state]
     (when shown?
       [:div.code-container
        [:code.edn-map 
         (for [[k v] (dissoc @state :current-page)]
           ^{:key k}
           [:div.pair
            [:span.key (str k)]
            [:span.val (str v)]])]])]))

(defn app
  [state]
  [:div
   [state-inspector state]
   [:div#page
    [(:current-page @state) state]]])

(defn initialize-state!
  [state]
  (reset! 
   state 
   {:show-inspector? false
    :initialized true
    :nav-count 0
    :current-page blank-page}))

(defn update-multi
  [m & kfs]
  (reduce
   (fn 
     [result [k f]]
     (update result k f))
   m
   (partition 2 kfs)))

(defn initialize-secretary!
  [state]
  (let [h (History.)]
    (goog.events/listen
     h EventType/NAVIGATE 
     (fn [e]
       (swap! 
        state 
        update-multi
        :nav-count inc
        :page (constantly {}))
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
