(ns home.client
  (:require [reagent.core :as reagent]
            [secretary.core :as secretary]
            [goog.events :as events]
            [goog.history.EventType :as EventType]
            [home.routes :as routes]
            [home.component.app :as app]
            [home.support :as support]
            [devtools.core :as devtools])
  (:import goog.History
           goog.Uri))

(enable-console-print!)
(devtools/install!)

(extend-type js/NodeList
  ISeqable
  (-seq [array] (array-seq array 0)))

(defn navigate
  [state token]
  (swap! state update :nav-count inc)
  (secretary/dispatch! token))

(defn asset-prefix []
  (or (aget js/window "ASSET_PREFIX") ""))

(defn initialize-state!
  [state]
  (let [browser (support/browser-type)]
    (reset! 
     state 
     {:browser browser
      :server "https://akjetma.herokuapp.com"
      :asset-prefix (asset-prefix)
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
   [app/app state]
   (.-body js/document)))

(defn load*
  []
  (let [state (reagent/atom {})
        history (History.)]
    (initialize-state! state)
    (routes/define-routes! state)
    (initialize-secretary! state history)
    (initialize-reagent! state)
    (fn figwheel-reload-fn
      []
      (navigate state (.getToken history)))))

(defn reload-https
  [uri]
  (set! (-> js/window .-location .-href) 
        (.setScheme uri "https")))

(defonce load
  (let [uri (Uri. (-> js/window .-location .-href))
        domain (.getDomain uri)
        scheme (.getScheme uri)
        reset? (and (not= "localhost" domain) (not= "https" scheme))]
    (if reset?
      (reload-https uri)
      (load*))))

 
