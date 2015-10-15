(ns home.support)

(def browser-things
  {:chrome [:home :sorter :matrix :cube :camera :shader]
   :safari [:home :sorter :matrix :cube :shader]
   :firefox [:home :matrix :sorter :camera :shader]
   :other [:home :sorter :matrix :cube :camera :shader]})

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

