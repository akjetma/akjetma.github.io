(ns home.page.cube
  (:require [reagent.core :as r]))

(declare cube)

(defn face
  [state side]
  [:div.face {:class side
              :on-click #(swap! state assoc side [cube])}
   (get @state side [:i.fa.fa-hand-pointer-o])])

(defn cube
  []
  (let [cube-state (r/atom {})]
    (fn []
      [:div.cube
       (map 
        (fn [side]
          [face cube-state side])
        ["front" "top" "right" "left" "bottom" "back"])])))

(defn camera-controls
  [state]
  [:div.camera-controls
   [:input {:type "range"
            :min -360
            :max 360
            :value (-> @state :cube :camera-x)
            :on-change #(swap! state assoc-in [:cube :camera-x] (-> % .-target .-value))}]
   [:input {:type "range"
            :min -360
            :max 360
            :value (-> @state :cube :camera-y)
            :on-change #(swap! state assoc-in [:cube :camera-y] (-> % .-target .-value))}]
   [:input {:type "range"
            :min -360
            :max 360
            :value (-> @state :cube :camera-z)
            :on-change #(swap! state assoc-in [:cube :camera-z] (-> % .-target .-value))}]])

(defn page
  [state]
  (when-not (:cube @state)
    (swap! state assoc :cube
           {:camera-x -45
            :camera-y -45
            :camera-z 0}))
  (fn cube-page
    [state]
    (let [{{:keys [camera-x camera-y camera-z]} :cube} @state
          transform (str "rotateX(" camera-x "deg) "
                         "rotateY(" camera-y "deg) " 
                         "rotateZ(" camera-z "deg)")]
      [:div#cube-page
       [camera-controls state]
       
       [:div#cube-container {:style {:transform transform}}
        [:div.window
         [cube]]]])))
