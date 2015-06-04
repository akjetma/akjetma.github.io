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
      :top [cube {:top [cube {:top [cube]}]}] 
      :right [cube {:right [cube {:right [cube]}]}] 
      :left [cube {:left [cube {:left [cube]}]}] 
      :bottom [cube {:bottom [cube {:bottom [cube]}]}] 
      :back [cube {:back [cube {:back [cube]}]}]}]
    ]])
