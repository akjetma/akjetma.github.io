(ns home.support)

(def browser-things
  {:chrome [:home :sorter :matrix :cube :camera :game-of-life :face-of-life]
   :safari [:home :sorter :matrix :cube :game-of-life]
   :firefox [:home :matrix :sorter :camera :shader :game-of-life :face-of-life]
   :other [:home :sorter :matrix :cube :camera :game-of-life :face-of-life]})

(defn browser-type
  []
  (let [is-opera (or (.-opera js/window) (-> js/navigator .-userAgent (.indexOf " OPR/") (> 0)))]
    (cond
      (and (.-chrome js/window) (not is-opera)) :chrome
      is-opera :opera
      (exists? (aget js/window "InstallTrigger")) :firefox
      (-> js/Object .-prototype .-toString (.call (.-HTMLElement js/window)) (.indexOf "Constructor") (> 0)) :safari
      (or false (.-documentMode js/document)) :ie
      :else :other)))

