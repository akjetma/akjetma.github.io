(ns home.page.signal
  (:require-macros [reagent.ratom :refer [reaction]])
  (:require [reagent.core :as reagent]))

(def oo "•")
(def xx "°")
(def uu "↑")
(def ur "↗")
(def rr "→")
(def rd "↘")
(def dd "↓")
(def dl "↙")
(def ll "←")
(def lu "↖")

(def directions
  {[-1 -1] lu
   [-1  0] uu
   [-1  1] ur
   [0  -1] ll
   [0   0] oo
   [0   1] rr
   [1  -1] dl
   [1   0] dd
   [1   1] rd})

(def opposite
  {uu dd
   ur dl
   rr ll
   rd lu
   dd uu
   dl ur
   ll rr
   lu rd
   oo xx
   xx oo})

(defn idx-coll
  [coll]
  (partition 2 (interleave (range) coll)))

(defn mat-map
  "map a function over the elements of a matrix. function is called with [i j] of current elem"
  [f m]
  (let [rows (count m)
        cols (count (first m))]
    (mapv
     (fn [i]
       (mapv
        (fn [j]
          (f (get-in m [i j]) [i j]))
        (range cols)))
     (range rows))))

(def catmap
  (comp
   (partial into {})
   mapcat))

(def in
  [[rd dd dl]
   [rr xx ll]
   [ur uu lu]])

(def out
  (mat-map
   #(get opposite %1)
   in))



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
