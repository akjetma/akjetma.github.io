(ns home.page.cube)

(defn get-active-face
  [ind]
  (str 
   (last 
    (take 
     (inc ind) 
     (cycle ["front" "back" "right" "left" "top" "bottom"])))
   "-active"))

(defn cube
  [state]
  (let [face-index (get-in @state [:page :face-index])]
    [:div.cube-container
     [:div {:class (str "cube " (get-active-face face-index))
            :on-click #(swap! state update-in [:page :face-index] inc)}
      [:div.face.front]
      [:div.face.back]
      [:div.face.right]
      [:div.face.left]
      [:div.face.top]
      [:div.face.bottom]]]))

(defn page
  [state]
  (swap! state assoc-in [:page :face-index] 0)
  (fn 
    [state] 
    [:div#cube-page
     [cube state]]))
