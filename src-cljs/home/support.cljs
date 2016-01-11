(ns home.support)

(def browser-things
  {:chrome [:home :sorter :matrix :cube :camera :game-of-life :face-of-life :photos]
   :safari [:home :sorter :matrix :cube :game-of-life :photos]
   :firefox [:home :matrix :sorter :camera :shader :game-of-life :face-of-life :photos]
   :other [:home :sorter :matrix :cube :camera :game-of-life :face-of-life :photos]})

(defn browser-type
  []
  (let [is-opera (or (.-opera js/window) (-> js/navigator .-userAgent (.indexOf " OPR/") (> 0)))]
    (cond
      (and (.-chrome js/window) (not is-opera)) :chrome
      is-opera :other
      (exists? (aget js/window "InstallTrigger")) :firefox
      (-> js/Object .-prototype .-toString (.call (.-HTMLElement js/window)) (.indexOf "Constructor") (> 0)) :safari
      (or false (.-documentMode js/document)) :other
      :else :other)))

