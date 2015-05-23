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

(defn shuffle-items!
  [state]
  (swap! 
   state 
   update-in [:page :items] 
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

(defn reverse-items!
  [state]
  (swap!
   state
   update-in [:page :items]
   (fn 
     [item-map]
     (let [item-max (dec (count item-map))]
       (into 
        {}
        (map 
         (fn 
           [[id item]]
           [id (update item :rank #(- item-max %))])
         item-map))))))

(defn color-sort!
  [state]
  (swap!
   state
   update-in [:page :items]
   (fn 
     [item-map]
     (let [sorted (sort-by #(-> % (get 1) :hue) item-map)]
       (into
        {}
        (map-indexed
         (fn [i [id item]]
           [id (assoc item :rank i)])
         sorted))))))

(defn grid-place
  [num-columns rank]
  (let [v-offset (* 110 (quot rank num-columns))
        h-offset (* 110 (mod rank num-columns))]
    (str "translate3d("h-offset"%, "v-offset"%, 0)")))

(defn list-item
  [num-columns hue-shift id {:keys [hue rank]}]
  [:div.item 
   {:style 
    {:background-color (str "hsl(" (+ hue hue-shift) ", 100%, 50%)")
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
      [:button {:on-click #(shuffle-items! state)} "Shuffle"]
      [:button {:on-click #(reverse-items! state)} "Reverse"]
      [:button {:on-click #(color-sort! state)} "Sort by color"]]]))

(defn page
  [state]
  (swap! 
   state 
   assoc :page 
   {:items (generate-items 100)
    :num-columns 10
    :hue-shift 0})
  (.setInterval js/window #(swap! state update-in [:page :hue-shift] + 36) 5000)
  (fn
    [state]
    (let [{{:keys [num-columns items hue-shift]} :page} @state]
      [:div
       [controls state]
       [:div#sortable-list
        (for [[id item] items]
          ^{:key id} [list-item num-columns hue-shift id item])]])))
