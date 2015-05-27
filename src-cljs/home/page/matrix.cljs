(ns home.page.matrix
  (:require [reagent.core :as r]))

(defn index-controller
  [state i j]
  [:input.mat-slider 
   {:type "range"
    :min -2
    :max 2
    :step 0.001
    :value (get-in @state [:page :matrix i j])
    :on-change #(swap! state assoc-in [:page :matrix i j] 
                       (-> % .-target .-value js/parseFloat))}])

(defn control-mat
  [state]
  [:div.mat-controls
   (for [i (range 2)] 
     ^{:key i} 
     [:div.row
      (for [j (range 3)]
        ^{:key j} 
        [index-controller state i j])])])

(defn render-mat
  [a b tx c d ty & [child]]
  [:div.mat 
   {:style 
    {:transform (str "matrix(" (->> [a c b d (* 10 tx) (* 10 ty)] (interpose ",") (apply str)) ")")}}
   child])

(defn display-mat
  [state]
  (let [{{[[a b tx] [c d ty]] :matrix} :page} @state
        renderable (partial vector render-mat a b tx c d ty)]
    [:div.mat-container
     (reduce (fn [nest render-fn] (render-fn nest)) (renderable) (repeat 25 renderable))]))

(defn page
  [state]
  (swap! state 
         assoc-in [:page :matrix]
         [[1 0 0]
          [0 1 0]])
  (fn 
    [state]
    [:div#matrix-page
     [:p.help "Use arrow keys for fine adjustments"]
     [control-mat state]
     [display-mat state]]))
