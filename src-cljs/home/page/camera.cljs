(ns home.page.camera
  (:require [reagent.core :as reagent]))

(def framerate (/ 1000 60))
(def buffer-ct 20)

(defn set-gum?
  []
  (when-let [gum (some (partial aget js/navigator)
                       ["getUserMedia" "webkitGetUserMedia" "mozGetUserMedia" "msGetUserMedia"])]
    (set! (.-getUserMedia js/navigator) gum)
    true))

(defn frame-dim
  [buff-width buff-height h-thick v-thick n]
  {:width (- buff-width (* 2 h-thick n))
   :height (- buff-height (* 2 v-thick n))})

(defn frame-pos
  [h-thick v-thick n]
  {:x (* h-thick n)
   :y (* v-thick n)})

(defn get-frame
  [buff-array buff-width {:keys [width height]} {:keys [x y]} n]
  (.getImageData
   buff-array
   (+ (* buff-width n) x)
   y
   width
   height))

(defn render-loop
  [video buff-array output width height]
  (let [v-thick (/ height buffer-ct 2)
        h-thick (/ width buffer-ct 2)
        old-buffer-data (.getImageData buff-array 0 0 (* (dec buffer-ct) width) height)
        get-dim (partial frame-dim width height h-thick v-thick)
        get-pos (partial frame-pos h-thick v-thick)]   
    (.drawImage buff-array video 0 0)
    (.putImageData buff-array old-buffer-data width 0)
    (doall
     (for [n (range buffer-ct)
           :let [dim (get-dim n)
                 pos (get-pos n)
                 frame (get-frame buff-array width dim pos (- buffer-ct n))]]
       (.putImageData
        output
        frame
        (:x pos)
        (:y pos))))))

(defn start-loop
  [video buff-array output width height]
  (let [buff-array (.getContext buff-array "2d")
        output (.getContext output "2d")]
    (.setInterval
     js/window
     #(render-loop video buff-array output width height)
     framerate)))

(defn set-dims
  [elem width height]
  (.setAttribute elem "width" width)
  (.setAttribute elem "height" height))

(defn can-play
  [video buff-array output e]
  (let [width (.-videoWidth video)
        height (.-videoHeight video)]
    (set-dims output width height)
    (set-dims buff-array (* buffer-ct width) height)
    (start-loop video buff-array output width height)))

(defn got-media
  [video buff-array output stream]
  (set! (.-src video) (-> js/window .-URL (.createObjectURL stream)))
  (.addEventListener
   video "canplay"
   (partial can-play video buff-array output)))

(defn page-render
  [state]
  [:div#camera-page
   [:video#input {:auto-play true}]
   [:canvas#buffer]
   [:canvas#output]])

(defn error
  ([message] (error message nil))
  ([message e]
   (.log js/console (str message " :") e)))

(defn page-did-mount
  [state]
  (let [video (.querySelector js/document "#input")
        buff-array (.querySelector js/document "#buffer")
        output (.querySelector js/document "#output")]
    (if (set-gum?)
      (.getUserMedia
       js/navigator #js {:video true}
       (partial got-media video buff-array output)
       (partial error "error getting user media"))
      (error "browser not supported"))))

(defn page
  [state]
  (reagent/create-class
   {:reagent-render page-render
    :component-did-mount #(page-did-mount state)}))
