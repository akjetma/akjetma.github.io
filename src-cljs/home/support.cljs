(ns home.support)

(def browser-things
  {:chrome [:matrix :cube :camera]
   :safari [:sorter :matrix :cube]
   :firefox [:sorter :camera]
   :other [:sorter :matrix :cube :camera]})

(defn browser-type
  []
  (let [is-opera (or (.-opera js/window) (-> js/navigator .-userAgent (.indexOf " OPR/") (> 0)))]
    (cond
      (and (.-chrome js/window) (not is-opera)) :chrome
      is-opera :opera
      (exists? (.-InstallTrigger js/window)) :firefox
      (-> js/Object .-prototype .-toString (.call (.-HTMLElement js/window)) (.indexOf "Constructor") (> 0)) :safari
      (or false (.-documentMode js/document)) :ie
      :else :other)))

