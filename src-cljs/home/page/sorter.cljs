(ns home.page.sorter)

(def emoji
  {:shuffle "🔀"
   :reverse "🔁"
   :rainbow "🌈"})

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

(defn controls
  [state]
  (let [{{:keys [num-columns items]} :sorter} @state
        num-items (count items)]
    [:div#sortable-controls
     [:input 
      {:type "number"
       :value num-items
       :on-change #(swap! state assoc-in [:sorter :items] (-> % .-target .-value js/parseInt generate-items))}]
     [:input.slider 
      {:type "range"
       :min 1
       :max 30
       :value num-columns
       :on-change #(swap! state assoc-in [:sorter :num-columns] (-> % .-target .-value js/parseInt))}]
     [:div.btns
      [:span.btn {:on-click #(swap! state update-in [:sorter :items] shuffle-ranks)} (:shuffle emoji)]
      [:span.btn {:on-click #(swap! state update-in [:sorter :items] reverse-ranks)} (:reverse emoji)]
      [:span.btn {:on-click #(swap! state update-in [:sorter :items] rank-by-hue)} (:rainbow emoji)]]]))

(defn list-item
  [num-columns id {:keys [hue rank]}]
  (let [translation (grid-place num-columns rank)]
    [:div.item 
     {:style 
      {:background-color (str "hsl(" hue ", 100%, 50%)")
       :z-index (- id)
       :transform translation
       :-webkit-transform translation}
      :id id}]))

(defn item-list
  [state]
  (let [{{:keys [num-columns items]} :sorter} @state]
    [:div#sortable-list
     (for [[id {hue :hue :as item}] items]
       ^{:key [id hue]} [list-item num-columns id item])]))

(defn page
  [state]
  (when-not (:sorter @state)
    (swap! state assoc :sorter 
           {:items (generate-items 100)
            :num-columns 10}))
  (fn sorter-page
    [state]
    [:div#sorter-page
     [controls state]
     [item-list state]]))
