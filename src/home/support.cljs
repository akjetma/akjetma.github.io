(ns home.support)

(def everything
  [:home :sorter :matrix :cube :camera :game-of-life :face-of-life :wordcloud])

(def browser-things
  {:chrome everything
   :opera everything
   :safari (remove #{:camera :face-of-life} everything)
   :firefox everything
   :other everything})

(defn browser-type
  []
  (let [is-opera (or (.-opera js/window) (-> js/navigator .-userAgent (.indexOf " OPR/") (> 0)))]
    (cond
      (and (.-chrome js/window) (not is-opera)) :chrome
      is-opera :opera
      (exists? (aget js/window "InstallTrigger")) :firefox
      (-> js/Object .-prototype .-toString (.call (.-HTMLElement js/window)) (.indexOf "Constructor") (> 0)) :safari
      :else :other)))

