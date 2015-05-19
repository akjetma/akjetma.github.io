(ns home.routes
  (:require [secretary.core :as secretary :refer-macros [defroute]]
            [home.page.home :as home]
            [home.page.sorter :as sorter]))

(defn define-routes!
  [state]
  (defroute home-path 
    "/" 
    []
    (swap! state assoc 
           :current-page home/page))

  (defroute sorter-path
    "/sorter"
    []
    (swap! state assoc
           :current-page sorter/page))
 
  (defroute etc-path 
    "*" 
    []
    (swap! state assoc 
           :current-page tp/blank-page)))
