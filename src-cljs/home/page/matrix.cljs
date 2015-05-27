(ns home.page.matrix
  (:require [reagent.core :as r]))

(defn index-controller
  [state i j]
  [:input 
   {:type "range"
    :min 0
    :max 1
    :step 0.001
    :value (get-in @state [:page :matrix i j])
    :on-change #(swap! state assoc-in [:page :matrix i j] 
                       (-> % .-target .-value js/parseFloat))}])

(defn control-mat
  [state]
  [:div
   (for [i (range 2)] 
     ^{:key i} 
     [:div 
      (for [j (range 3)]
        ^{:key j} 
        [index-controller state i j])])])

(defn display-mat
  [state]
  (let [{{[[a b tx] [c d ty]] :matrix} :page} @state]
    [:div 
     {:style 
      {:width "200px"
       :height "200px"
       :background-color "#000"
       :transform (str "matrix(" (->> [a c b d tx ty] (interpose ",") (apply str)) ")")}}]))

(defn page
  [state]
  (swap! state 
         assoc-in [:page :matrix]
         [[1 0 0]
          [0 1 0]])
  (fn 
    [state]
    [:div
     [control-mat state]
     [display-mat state]]))
