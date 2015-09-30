(ns home.support)

(def browser-things
  {:chrome [:home :sorter :matrix :cube :camera]
   :safari [:home :sorter :matrix :cube]
   :firefox [:home :matrix :sorter :camera]
   :other [:home :sorter :matrix :cube :camera]})

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

