(ns home.page.sorter)

(defn generate-item
  [rank-init]
  [rank-init 
   {:rank rank-init
    :hue (rand-int 360)}])

(defn generate-items
  [n]
  (into 
   {} 
   (map 
    generate-item 
    (range n))))

(defn shuffle-ranks
  [item-map] 
  (let [new-ranks (->> item-map (vals) (map :rank) (shuffle))]
    (into 
     {} 
     (map 
      (fn 
        [[id item] new-rank]
        [id (assoc item :rank new-rank)])
      item-map
      new-ranks))))

(defn reverse-ranks
  [item-map]
  (let [item-max (-> item-map (count) (dec))]
    (into 
     {}
     (map 
      (fn 
        [[id item]]
        [id (update item :rank #(- item-max %))])
      item-map))))

(defn rank-by-hue
  [item-map]
  (let [hue-sorted (sort-by (fn [[_ item]] (:hue item)) item-map)]
    (into
     {}
     (map-indexed
      (fn 
        [hue-index [id item]]
        [id (assoc item :rank hue-index)])
      hue-sorted))))

(defn grid-place
  [num-columns rank]
  (let [v-offset (* 110 (quot rank num-columns))
        h-offset (* 110 (mod rank num-columns))]
    (str "translate3d("h-offset"%, "v-offset"%, 0)")))

(defn list-item
  [num-columns id {:keys [hue rank]}]
  [:div.item 
   {:style 
    {:background-color (str "hsl(" hue ", 100%, 50%)")
     :z-index (- id)
     :transform (grid-place num-columns rank)}
    :id id}])

(defn controls
  [state]
  (let [num-columns (get-in @state [:page :num-columns])
        num-items (count (get-in @state [:page :items]))]
    [:div#sortable-controls
     [:div.row
      [:input 
       {:type "number"
        :value num-items
        :on-change #(swap! state assoc-in [:page :items] (-> % .-target .-value js/parseInt generate-items))}]
      [:span "# of items: " num-items]]
     [:div.row 
      [:input 
       {:type "range"
        :min 1
        :max 30
        :value num-columns
        :on-change #(swap! state assoc-in [:page :num-columns] (-> % .-target .-value js/parseInt))}]
      "# of columns: " num-columns]
     [:div.row
      [:button {:on-click #(swap! state update-in [:page :items] shuffle-ranks)} "Shuffle"]
      [:button {:on-click #(swap! state update-in [:page :items] reverse-ranks)} "Reverse"]
      [:button {:on-click #(swap! state update-in [:page :items] rank-by-hue)} "Sort by color"]]]))

(defn page
  [state]
  (swap! 
   state 
   assoc :page 
   {:items (generate-items 100)
    :num-columns 10})
  (fn
    [state]
    (let [{{:keys [num-columns items]} :page} @state]
      [:div
       [controls state]
       [:div#sortable-list
        (for [[id item] items]
          ^{:key id} [list-item num-columns id item])]])))
