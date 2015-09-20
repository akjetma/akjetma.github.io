(ns home.page.signal)

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

(def opposite
  {oo xx
   xx oo
   uu dd
   ur dl
   rr ll
   rd lu
   dd uu
   dl ur
   ll rr
   lu rd})

(defn mapm
  ([cell mat] (mapm cell identity mat))
  ([cell row mat]
   (mapv
    (comp
     row
     (partial mapv cell))
    mat)))

(defn p-intersect?
  [m-a m-b]
  (some
   true?
   (map = (flatten m-a) (flatten m-b))))

(def in
  [[rd dd dl]
   [rr xx ll]
   [ur uu lu]])

(def out
  (mapm
   (partial get opposite)
   in))

(def initial-matrix
  [[rd xx xx xx xx]
   [xx xx xx xx xx]
   [xx xx xx xx xx]])

(defn indexed-matrix
  [mat]
  (into
   {}
   (mapcat
    (fn [row i]
      (map
       (fn [cell j]
         [[i j] cell])
       row
       (range)))
    mat
    (range))))

(defn scene
  [mat]
  [:div.scene
   (map-indexed
    (fn [i row]
      [:div.row
       (map-indexed
        (fn [j cell]
          [:span.cell cell])
        row)])
    mat)])

(defn page
  [state]
  [:div#signal-page
   [scene initial-matrix]])
