(ns home.core
  (:require [reagent.core :as reagent]
            [secretary.core :as secretary]
            [goog.events :as events]
            [goog.history.EventType :as EventType]
            [home.routes :as routes])
  (:import goog.History))

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

(defn update-multi
  [m & kfs]
  (reduce
   (fn [result [k f]] (update result k f))
   m
   (partition 2 kfs)))

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
    :current-page blank-page}))

(defn initialize-secretary!
  [state history]
  (goog.events/listen
   history EventType/NAVIGATE 
   #(navigate state (.-token %)))
  (doto history (.setEnabled true)))

(defn initialize-reagent!
  [state]
  (reagent/render-component
   [app state]
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
