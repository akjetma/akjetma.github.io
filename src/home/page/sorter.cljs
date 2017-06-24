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

(defn ring-mapping
  [matrix]
  (let [M (count matrix)
        N (count (first matrix))
        max-depth (quot (min M N) 2)
        rings (mapv
               (fn [d]
                 (vec
                  (concat 
                   ;; top-left -> ~top-right
                   (mapv #(vector d %) (range d (- N d 1)))
                   
                   ;; top-right -> ~bottom-right
                   (mapv #(vector % (- N d 1)) (range d (- M d 1)))
                   
                   ;; bottom-right -> ~bottom-left
                   (mapv #(vector (- M d 1) %) (reverse (range (+ d 1) (- N d))))
                   
                   ;; bottom-left -> ~top-left
                   (mapv #(vector % d) (reverse (range (+ d 1) (- M d)))))))
               (range max-depth))]
    (if (and (even? M) (even? N))
      rings
      (conj rings
            (vec
             (mapcat
              (fn [i] 
                (mapv 
                 (fn [j]
                   [i j])
                 (range max-depth (- N max-depth))))
              (range max-depth (- M max-depth))))))))

(defn leftshift
  [v n]
  (let [rightshift (mod n (count v))]
    (vec (take-last (count v) (take (+ (count v) rightshift) (cycle v))))))

(defn unfurl
  [matrix mapping]
  (mapv
   (fn [ring]
     (mapv (partial get-in matrix) ring))
   mapping))

(defn rebuild
  [matrix unfurled mapping]
  (reduce
   (fn [matrix [[row col] v]]
     (assoc-in matrix [row col] v))
   matrix
   (apply concat
          (map-indexed
           (fn [ring-index ring]
             (map-indexed
              (fn [notch-index origin]
                [origin (get-in unfurled [ring-index notch-index])])
              ring))
           mapping))))

(defn leftshift-matrix
  [matrix shift-n]
  (let [mapping (ring-mapping matrix)
        unfurled (unfurl matrix mapping)
        shifted (mapv #(leftshift % shift-n) unfurled)]
    (rebuild matrix shifted mapping)))

(defn items-to-matrix
  [item-map num-columns]
  (let [num-items (count item-map)
        pseudo-mat (mapv vec (partition-all num-columns (sort-by (comp :rank val) item-map)))
        last-row (last pseudo-mat)]
    (conj (vec (butlast pseudo-mat))
          (vec (concat (repeat (- num-columns (count last-row)) nil) last-row)))))

(defn matrix-to-items
  [item-matrix]
  (into 
   {}
   (map-indexed
    (fn [mat-idx item-kv]
      (when-let [[id item] item-kv]
        [id (assoc item :rank mat-idx)]))
    (remove nil? (apply concat item-matrix)))))

(defn rotate-items
  [item-map num-columns]
  (-> item-map
      (items-to-matrix num-columns)
      (leftshift-matrix 1)
      (matrix-to-items)))

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
    [:div.controls
     [:input.sort-control
      {:type "number"
       :value num-items
       :on-change #(swap! state assoc-in [:sorter :items] (-> % .-target .-value js/parseInt generate-items))}]
     [:input.sort-control 
      {:type "range"
       :min 1
       :max 30
       :value num-columns
       :on-change #(swap! state assoc-in [:sorter :num-columns] (-> % .-target .-value js/parseInt))}]
     [:div.sort-control.sort-type
      [:span.btn {:on-click #(swap! state update-in [:sorter :items] shuffle-ranks)} (:shuffle emoji)]
      [:span.btn {:on-click #(swap! state update-in [:sorter :items] reverse-ranks)} (:reverse emoji)]
      [:span.btn {:on-click #(swap! state update-in [:sorter :items] rank-by-hue)} (:rainbow emoji)]
      [:span.btn {:on-click #(swap! state 
                                    (fn [s] (update-in s [:sorter :items] rotate-items (-> s :sorter :num-columns))))} "rotate"]]]))

(defn list-item
  [num-columns id {:keys [hue rank]}]
  (let [translation (grid-place num-columns rank)]
    [:div.item 
     {:style 
      {:background-color (str "hsl(" hue ", 50%, 70%)")
       :z-index (- id)
       :transform translation
       :-webkit-transform translation}
      :id id}]))

(defn item-list
  [state]
  (let [{{:keys [num-columns items]} :sorter} @state]
    [:div.list
     {:style
      {:width (str (* num-columns 55) "px")}}
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
