(ns home.routes
  (:require [secretary.core :as secretary :refer-macros [defroute]]
            [home.page.home :as home]
            [home.page.sorter :as sorter]
            [home.page.matrix :as matrix]
            [home.page.cube :as cube]))

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

  (defroute matrix-path
    "/matrix"
    []
    (swap! state assoc
           :current-page matrix/page))

  (defroute cube-path
    "/cube"
    []
    (swap! state assoc
           :current-page cube/page))
 
  (defroute etc-path 
    "*" 
    []
    (swap! state assoc 
           :current-page home/page)))
