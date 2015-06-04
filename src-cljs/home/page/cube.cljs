(ns home.page.cube)

(defn cube
  ([] (cube {}))
  ([{:keys [front top right left bottom back]}]
   [:div.cube
    [:div.face.front front]
    [:div.face.top top]
    [:div.face.right right]
    [:div.face.left left]
    [:div.face.bottom bottom]
    [:div.face.back back]]))

(defn page
  [state]
  [:div#cube-page
   [:div.window
    [cube 
     {:front [cube {:front [cube {:front [cube]}]}] 
      :top [cube {:front [cube {:front [cube]}]}] 
      :right [cube {:front [cube {:front [cube]}]}] 
      :left [cube {:front [cube {:front [cube]}]}] 
      :bottom [cube {:front [cube {:front [cube]}]}] 
      :back [cube {:front [cube {:front [cube]}]}]}]]])
