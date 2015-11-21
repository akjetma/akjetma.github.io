(ns home.routes
  (:require [secretary.core :as secretary :refer-macros [defroute]]
            [home.page.home :as home]
            [home.page.sorter :as sorter]
            [home.page.matrix :as matrix]
            [home.page.cube :as cube]
            [home.page.signal :as signal]
            [home.page.camera :as camera]
            [home.page.shader :as shader]
            [home.page.shader-2 :as shader-2]
            [home.page.unsupported :as unsupported]))

(def current-things
  [:sorter :matrix :cube :camera :shader :shader-2])

(def page-map
  {:home home/page
   :sorter sorter/page
   :matrix matrix/page
   :cube cube/page
   :signal signal/page
   :camera camera/page
   :shader shader/page
   :shader-2 shader-2/page
   :unsupported unsupported/page})

(defn nav
  [state page]
  (if (some #{page} (:things @state))
    (swap! state assoc :current-page page)
    (swap! state assoc :current-page :unsupported)))

(defn define-routes!
  [state]
  (defroute landing-path "/" [] (nav state (rand-nth (remove #{:home :camera} (:things @state)))))
  (defroute home-path "/home" [] (nav state :home))
  (defroute sorter-path "/sorter" [] (nav state :sorter))
  (defroute matrix-path "/matrix" [] (nav state :matrix))
  (defroute cube-path "/cube" [] (nav state :cube))
  (defroute signal-path "/signal" [] (nav state :signal))
  (defroute camera-path "/camera" [] (nav state :camera))
  (defroute shader-path "/shader" [] (nav state :shader))
  (defroute shader-2-path "/shader-2" [] (nav state :shader-2))
  (defroute etc-path "*" [] (nav state :home)))
