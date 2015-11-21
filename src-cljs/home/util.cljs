(ns home.util)

(defn ^:export setgum
  []
  (when-let [gum (some (partial aget js/navigator)
                       ["getUserMedia" "webkitGetUserMedia" "mozGetUserMedia" "msGetUserMedia"])]
    (set! (.-getUserMedia js/navigator) gum)
    true))
