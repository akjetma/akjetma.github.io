(ns home.routes
  (:require [secretary.core :as secretary :refer-macros [defroute]]
            [home.page.home :as home]
            [home.page.sorter :as sorter]
            [home.page.matrix :as matrix]
            [home.page.cube :as cube]
            [home.page.camera :as camera]
            [home.page.gol :as gol]
            [home.page.fol :as fol]
            [home.page.wordcloud :as wordcloud]
            [home.page.unsupported :as unsupported]))

(def page-map
  {:home home/page
   :sorter sorter/page
   :matrix matrix/page
   :cube cube/page
   :camera camera/page
   :game-of-life gol/page
   :face-of-life fol/page
   :wordcloud wordcloud/page
   :unsupported unsupported/page})

(defn nav
  [state page]
  (if (some #{page} (:things @state))
    (swap! state assoc :current-page page)
    (swap! state assoc :current-page :unsupported)))

(defn define-routes!
  [state]
  (defroute landing-path "/" [] (nav state (rand-nth (:things @state))))
  (defroute home-path "/home" [] (nav state :home))
  (defroute sorter-path "/sorter" [] (nav state :sorter))
  (defroute matrix-path "/matrix" [] (nav state :matrix))
  (defroute cube-path "/cube" [] (nav state :cube))
  (defroute camera-path "/camera" [] (nav state :camera))
  (defroute gol-path "/game-of-life" [] (nav state :game-of-life))
  (defroute fol-path "/face-of-life" [] (nav state :face-of-life))
  (defroute wordcloud-path "/wordcloud" [] (nav state :wordcloud))
  (defroute etc-path "*" [] (nav state :home)))
