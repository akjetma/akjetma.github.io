(ns home.util)

(defn resource
  [state asset]
  (str (:asset-prefix @state) asset))

(defn url?
  [candidate]  
  (re-matches 
   #"^https?://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]" 
   candidate))

(defn ^:export setgum
  []
  (when-let [gum (some (partial aget js/navigator)
                       ["getUserMedia" "webkitGetUserMedia" "mozGetUserMedia" "msGetUserMedia"])]
    (set! (.-getUserMedia js/navigator) gum)
    true))
