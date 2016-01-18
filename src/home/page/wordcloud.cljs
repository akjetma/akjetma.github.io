(ns home.page.wordcloud
  (:require [ajax.core :as http]
            [home.component.wc :as wc]
            [home.component.svg :as svg]))

(def right 0)
(def down 90)
(def left 180)
(def up 270)
(def directions [right down left up])

(defn box-corners
  "coordinates of rectangle vertices given top left and bottom right corner."
  ([x1 y1 x2 y2] (box-corners x1 y1 x2 y2 0))
  ([x1 y1 x2 y2 pad]
   (let [x1 (- x1 pad)
         y1 (- y1 pad)
         x2 (+ x2 pad)
         y2 (+ y2 pad)]
     [[x1 y1] [x2 y1] 
      [x1 y2] [x2 y2]])))

(defn box-lines
  "rectangle's line segments given corners"
  [[a b 
    c d]]
  [[a b] [c d] [a c] [b d]])

(defn orthogonal?
  "takes two line segments"
  [[[_ y1] [_ y2]] 
   [[_ y3] [_ y4]]]
  (not= (= y1 y2)
        (= y3 y4)))

(defn intersecting?
  "takes two line segments"
  [a b]
  (boolean
   (when (orthogonal? a b) 
     (let [[[x1 y1] [x2 y2]] a
           [[x3 y3] [x4 y4]] b]
       (or (and (or (<= x1 x3 x2)
                    (>= x1 x3 x2))
                (or (<= y3 y1 y4)
                    (>= y3 y1 y4)))
           (and (or (<= x3 x1 x4)
                    (>= x3 x1 x4))
                (or (<= y1 y3 y2)
                    (>= y1 y3 y2))))))))

(defn pierces?
  "takes two sets of vertcies defining the corners of rectangles 
  and finds out if any of their line segments intersect."
  [i j]
  (let [[iab icd iac ibd] (box-lines i)
        [jab jcd jac jbd] (box-lines j)]
    (or (intersecting? iab jac) (intersecting? iab jbd)
        (intersecting? icd jac) (intersecting? icd jbd)
        (intersecting? iac jab) (intersecting? iac jcd)
        (intersecting? ibd jab) (intersecting? ibd jcd))))

(defn compare-points
  "tests comparisons for x and y values of points"
  [[x1 y1] [x2 y2] xc yc]
  (and (xc x1 x2)
       (yc y1 y2)))

(defn engulfs?
  "tests whether one shape entirely encompasses another"
  [[ia ib
    ic id]
   [ja jb
    jc jd]]
  (and (compare-points ia ja < <)
       (compare-points ib jb > <)
       (compare-points ic jc < >)
       (compare-points id jd > >)))

(defn collide?
  "checks whether the two rectangles occupy the same space"
  [i j]
  (or
   (engulfs? i j) 
   (engulfs? j i)
   (pierces? i j)))

(defn opposite-corner
  "return the matching coordinate that describes the bounding
  box of a rectangle of width and height rotated about the 
  starting coordinate."
  [x y {:keys [width height]} rotation]
  (condp = rotation
    right [(+ x width) (+ y height)]
    down [(- x height) (+ y width)]
    left [(- x width) (- y height)]
    up [(+ x height) (- y width)]))

(defn get-start
  "given the ending coordinate and rotation of the previous 
  shape, find where the next shape should start based on its
  rotation."
  [{:keys [x y last-rotation]} {:keys [height]} rotation]
  (condp = [last-rotation rotation]
    [right right] [x (- y height)]
    [right left] [x (+ y height)]
    [left right] [x (- y height)]
    [left left] [x (+ y height)]
    [up down] [(+ x height) y]
    [up up] [(- x height) y]
    [down down] [(+ x height) y]
    [down up] [(- x height) y]        
    [x y]))

(defn find-place
  "tries to find a valid placement for a rectangle given a starting
  coordinate."
  ([cloud word] (find-place cloud word (:rotation cloud) 0))
  ([cloud word rotation tries]
   (let [[x1 y1] (get-start cloud word rotation)
         [x2 y2] (opposite-corner x1 y1 word rotation)
         [x-min x-max] (if (< x1 x2) [x1 x2] [x2 x1])
         [y-min y-max] (if (< y1 y2) [y1 y2] [y2 y1])
         box (box-corners x-min y-min x-max y-max -1)
         success? (not-any? (partial collide? box) (:boxes cloud))
         exhausted? (= 5 tries)]
     (cond 
       success? [x1 y1 x2 y2 rotation box]       
       exhausted? nil
       :else 
       (find-place 
        cloud 
        word 
        (mod (- rotation 90) 360) 
        (inc tries))))))

(defn add-word
  "if a word can be placed into the wordcloud, add it to the cloud
  otherwise skip it and add it to a list of rectangles that couldn't
  be placed. (to be retried later)"
  [cloud word]
  (if-let [placement (find-place cloud word)]
    (let [[x1 y1 x2 y2 new-rotation box] placement
          placed (assoc word :x x1 :y y1 :rotation new-rotation)]
      (-> cloud
          (assoc :x x2
                 :y y2                  
                 :last-rotation new-rotation
                 :rotation (mod (+ new-rotation 180) 360))
          (update :placed conj placed)
          (update :boxes conj box)
          (update :x-min min x1 x2)
          (update :y-min min y1 y2)
          (update :x-max max x1 x2)
          (update :y-max max y1 y2)))
    (update cloud :unplaceable conj word)))

(defn expand-cloud
  "given a list of words, built the data structure that describes
  a wordcloud"
  ([words]
   (let [cloud {:x 0 :x-min 0 :x-max 0
                :y 0 :y-min 0 :y-max 0
                :rotation right
                :last-rotation up
                :placed []
                :unplaceable []
                :boxes []
                :runs 0}]
     (expand-cloud cloud words))) 
  ([cloud words]
   (let [{:keys [unplaceable x-max y-min y-max runs] :as greater-cloud} (reduce add-word cloud words)]
     (if (or (empty? unplaceable) (= runs 10))
       greater-cloud
       (expand-cloud
        (assoc 
         greater-cloud
         :x x-max
         :y (rand-nth (range y-min y-max)) 
         :rotation (rand-nth directions) 
         :last-rotation (rand-nth directions)
         :unplaceable []
         :runs (inc runs))
        (shuffle unplaceable))))))
 
(defn fit-cloud
  "adjust the locations of the words in the cloud and 
  the boundaries of the cloud so that it starts at 0 0"
  [{:keys [x-min x-max y-min y-max placed] :as cloud}]
  (assoc 
   cloud
   :x-max (- x-max x-min)
   :y-max (- y-max y-min)
   :placed (map 
           #(-> % (update :x - x-min) (update :y - y-min))
           placed)))

(defn render-transform
  "add the transform property to a positioned word"
  [{:keys [x y rotation] :as word}]
  (let [x (str x "px")
        y (str y "px")
        rotation (str rotation "deg")]
    (assoc word :transform 
           (str (svg/translate x y) " " (svg/rotate rotation)))))

(defn render-transforms
  [cloud]
  (update cloud :placed (partial map render-transform)))

(defn make-cloud
  "returns a renderable cloud structure from some words"
  [words]
  (render-transforms
   (fit-cloud
    (expand-cloud words))))

(defn handle-words
  [qi response]
  (swap! qi assoc :raw-words response :loading false))

(defn get-words
  [qi url]
  (swap! qi assoc :raw-words nil :words nil :loading true)
  (http/GET "https://akjetma.herokuapp.com/words.json" {:handler (partial handle-words qi)
                                                        :params {:url url}}))

(defn input
  [qi]
  (let [url (:url @qi)]
    [:div {:style {:position "absolute"}}
     [:input {:type "text"
              :value url
              :on-change #(swap! qi assoc :url (-> % .-target .-value))}]
     [:button {:on-click #(get-words qi url)} "Go"]]))

(defn display-cloud
  [qi words]
  (let [{:keys [x-max y-max placed runs]} (make-cloud words)]
    (.log js/console "Runs: " runs)
    [wc/html-cloud placed x-max y-max]))

(defn load-words
  [qi words]
  [:div {:style {:padding-top "20px"}}
   [:h2 "rendering..."]
   [:div {:style {:visibility "hidden"}}
    [wc/html-size-words qi words]]])

(defn page
  [qi]
  (fn
    [qi]
    (let [{:keys [raw-words words take-words drop-words loading]} @qi
          drop-words (or drop-words 0)
          take-words (or take-words 100)]
      [:div
       [input qi]
       (cond
         loading [:h2 {:style {:padding-top "20px"}} "loading..."]
         words [display-cloud qi (->> words (drop drop-words) (take take-words) shuffle)]         
         raw-words [load-words qi (->> raw-words (sort-by last >))])])))

