(ns home.page.signal
  (:require-macros [reagent.ratom :refer [reaction]])
  (:require [reagent.core :as reagent]))

(def out
  [["↖" "↑" "↗"]
   ["←" nil "→"]
   ["↙" "↓" "↘"]])

(def in
  (mapv
   (comp vec reverse)
   (reverse out)))

(def catmap
  (comp
   (partial into {})
   mapcat))

(defn idx-coll
  [coll]
  (partition 2 (interleave (range) coll)))

(defn neighbors
  [i j m]
  (let [rows (count m)
        cols (count (first m))]
    (catmap
     (fn [ii]
       (map
        (fn [jj]
          [[ii jj]
           (get-in m [(mod (+ i ii) rows) (mod (+ j jj) cols)])])
        (range -1 2)))
     (range -1 2))))

(defn scene
  [mat]
  [:div.scene
   (for [[i row] (idx-coll mat)]
     [:div.row
      (for [[j cell] (idx-coll row)]
        [:span.cell cell])])])

(defn page
  [state]
  [:div#signal-page
   [scene in]])
