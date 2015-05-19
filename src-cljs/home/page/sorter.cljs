(ns home.page.sorter)

(defn random-color
  []
  (subs 
   (.toString
    (+ 16r1000000 (rand-int 16rFFFFFF))
    16)
   1))

(defn generate-item
  [rank-init]
  [rank-init 
   {:rank rank-init
    :color (str "#" (random-color))}])

(defn generate-items
  [n]
  (into 
   {} 
   (map 
    generate-item 
    (range n))))

(defn shuffle-all!
  [state]
  (swap! 
   state update-in [:page :items] 
   (fn 
     [item-map]
     (let [ranks (map :rank (vals item-map))
           shuffled (shuffle ranks)]
       (into
        {} 
        (map 
         (fn 
           [rank-new [id item]]
           [id (assoc item :rank rank-new)])
         shuffled
         item-map))))))

(defn grid-place
  [num-columns rank]
  (let [v-offset (* 110 (int (/ rank num-columns)))
        h-offset (* 110 (mod rank num-columns))]
    (str "translate3d("h-offset"%, "v-offset"%, 0)")))

(defn list-item
  [id item]
  [:div.item 
   {:style 
    {:background-color (:color item)
     :z-index id
     :transform (grid-place 25 (:rank item))}
    :id id}
   id])

(defn page
  [state]
  (swap! state assoc-in [:page :items] (generate-items 300))
  (fn
    [state]
    [:div
     [:button {:on-click #(shuffle-all! state)} 
      "Shuffle"]
     [:div.sortable-list
      (for [[id item] (get-in @state [:page :items])]
        ^{:key id} [list-item id item])]]))
