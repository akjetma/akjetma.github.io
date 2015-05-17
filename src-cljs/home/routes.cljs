(ns home.routes
  (:require [secretary.core :as secretary :refer-macros [defroute]]
            [home.page.tests :as tp]))

(defn define-routes!
  [state]
  (defroute home-path 
    "/" 
    []
    (swap! state assoc 
           :current-page tp/home-page))

  (defroute test-path 
    "/test" 
    []
    (swap! state assoc 
           :current-page tp/test-page))

  (defroute etc-path 
    "*" 
    []
    (swap! state assoc 
           :current-page tp/blank-page)))
