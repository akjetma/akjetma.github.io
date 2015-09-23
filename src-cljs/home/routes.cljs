(ns home.routes
  (:require [secretary.core :as secretary :refer-macros [defroute]]
            [home.page.home :as home]
            [home.page.sorter :as sorter]
            [home.page.matrix :as matrix]
            [home.page.cube :as cube]
            [home.page.signal :as signal]
            [home.page.camera :as camera]))

(def page-map
  {:home home/page
   :sorter sorter/page
   :matrix matrix/page
   :cube cube/page
   :signal signal/page
   :camera camera/page})

(defn nav
  [state page]
  (swap! state assoc :current-page (get page-map page)))

(defn define-routes!
  [state]
  (defroute landing-path "/" [] (nav state (rand-nth [:sorter :matrix :cube :camera])))
  (defroute home-path "/home" [] (nav state :home))
  (defroute sorter-path "/sorter" [] (nav state :sorter))
  (defroute matrix-path "/matrix" [] (nav state :matrix))
  (defroute cube-path "/cube" [] (nav state :cube))
  (defroute signal-path "/signal" [] (nav state :signal))
  (defroute camera-path "/camera" [] (nav state :camera))
  (defroute etc-path "*" [] (nav state :home)))
