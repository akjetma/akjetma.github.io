(ns home.routes
  (:require [secretary.core :as secretary :refer-macros [defroute]]
            [home.page.home :as home]
            [home.page.sorter :as sorter]
            [home.page.matrix :as matrix]
            [home.page.cube :as cube]
            [home.page.signal :as signal]))

(defn nav
  [state page]
  (swap! state assoc :current-page page))

(defn define-routes!
  [state]
  (defroute home-path "/" [] (nav state home/page))
  (defroute sorter-path "/sorter" [] (nav state sorter/page))
  (defroute matrix-path "/matrix" [] (nav state matrix/page))
  (defroute cube-path "/cube" [] (nav state cube/page))
  (defroute signal-path "/signal" [] (nav state signal/page))
  (defroute etc-path "*" [] (nav state home/page)))
