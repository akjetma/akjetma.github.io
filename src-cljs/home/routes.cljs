(ns home.routes
  (:require [secretary.core :as secretary :refer-macros [defroute]]
            [home.page.home :as home]
            [home.page.sorter :as sorter]
            [home.page.matrix :as matrix]
            [home.page.cube :as cube]
            [home.page.signal :as signal]
            [home.page.camera :as camera]
            [home.page.gol :as gol]
            [home.page.fol :as fol]
            [home.page.unsupported :as unsupported]))

(def current-things
  [:sorter :matrix :cube :camera :game-of-life :face-of-life])

(def page-map
  {:home home/page
   :sorter sorter/page
   :matrix matrix/page
   :cube cube/page
   :signal signal/page
   :camera camera/page
   :game-of-life gol/page
   :face-of-life fol/page
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
  (defroute gol-path "/game-of-life" [] (nav state :game-of-life))
  (defroute fol-path "/face-of-life" [] (nav state :face-of-life))
  (defroute etc-path "*" [] (nav state :home)))
