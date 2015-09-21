(ns home.page.matrix)

(defn index-controller
  [state i j]
  [:input.mat-slider 
   {:type "range"
    :min -2
    :max 2
    :step 0.001
    :value (get-in @state [:matrix i j])
    :on-change #(swap! state assoc-in [:matrix i j] 
                       (-> % .-target .-value js/parseFloat))}])

(defn control-mat
  [state]
  [:div.mat-controls
   (mapcat
    (fn [i]
      (map
       (fn [j] ^{:key [i j]} [index-controller state i j])
       (range 3)))
    (range 2))])

(defn render-mat
  [a b tx c d ty & [child]]
  [:div.mat 
   {:style 
    {:transform (str "matrix(" (->> [a c b d (* 10 tx) (* 10 ty)] (interpose ",") (apply str)) ")")}}
   child])

(defn display-mat
  [state]
  (let [{[[a b tx] [c d ty]] :matrix} @state
        renderable (partial vector render-mat a b tx c d ty)]
    [:div.mat-container
     (reduce (fn [nest render-fn] (render-fn nest)) (renderable) (repeat 25 renderable))]))

(defn page
  [state]
  (when-not (:matrix @state)
    (swap! state assoc :matrix
           [[1 0 0]
            [0 1 0]]))
  (fn matrix-page
    [state]
    [:div#matrix-page
     [control-mat state]
     [display-mat state]]))
