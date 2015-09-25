(ns home.page.camera
  (:require [reagent.core :as reagent]))

(def framerate (/ 1000 30))

(defn set-gum?
  []
  (when-let [gum (some (partial aget js/navigator)
                       ["getUserMedia" "webkitGetUserMedia" "mozGetUserMedia" "msGetUserMedia"])]
    (set! (.-getUserMedia js/navigator) gum)
    true))

(defn render-loop
  [contexts video]
  (let [num (count contexts)
        width (.-videoWidth video)
        height (quot (.-videoHeight video) num)]
    (doall
     (map-indexed
      (fn [n context]        
        (.drawImage context video 0 (* n height) width height 0 0 width height))
      contexts)))
  (.setTimeout js/window #(render-loop contexts video) framerate))

(defn start-loop
  [contexts video object-url]
  (set! (.-src video) object-url)
  (.setTimeout js/window #(render-loop contexts video) 1000))

(defn got-media
  [state contexts video stream]
  (let [object-url (-> js/window .-URL (.createObjectURL stream))]
    (swap!
     state assoc
     :camera-stream stream
     :camera-object-url object-url)
    (start-loop contexts video object-url)))





(defn output-render
  [state total n]
  (let [id (str "output-" n)
        dimensions (:camera-dimensions @state)
        width (:width dimensions)
        height (quot (:height dimensions) total)]
    [:canvas.output
     {:id id :width width :height height}]))

(defn output-did-mount
  [state n]
  (.log js/console "output-did-mount")
  (let [canvas (.querySelector js/document (str "#output-" n))
        context (.getContext canvas "2d")]
    (swap! state update-in [:camera-contexts] (fnil conj []) context)))

(defn output
  [state total n]
  (reagent/create-class
   {:reagent-render output-render
    :component-did-mount #(output-did-mount state n)}))




(defn input-render
  [_]
  [:video#input {:auto-play true}])

(defn input-did-mount
  [state]
  (.log js/console "input-did-mount")
  (let [video (.querySelector js/document "#input")]
    (swap! state assoc :camera-video video)
    (.addEventListener
     video "canplay"
     #(swap! state assoc
             :camera-dimensions {:width (.-videoWidth video) :height (.-videoHeight video)}))))

(defn input
  [state]
  (reagent/create-class
   {:reagent-render input-render
    :component-did-mount #(input-did-mount state)}))




(defn page-render
  [state]
  (let [canvases (:camera-canvas-count @state)]
    [:div#camera-page
     [input state]
     (for [n (range canvases)]
       ^{:key n}
       [output state canvases n])]))

(defn page-did-mount
  [state]
  (.log js/console "page-did-mount")
  (let [{:keys [camera-contexts camera-video camera-object-url]} @state]
    (if camera-object-url
      (start-loop camera-contexts camera-video camera-object-url) 
      (if (set-gum?)
        (.getUserMedia
         js/navigator #js {:video true}
         (partial got-media state camera-contexts camera-video)
         #(.log js/console "error: " %))
        (swap! state assoc :camera-error :unsupported)))))

(defn page
  [state]
  (swap! state assoc :camera-canvas-count 10)
  (reagent/create-class
   {:reagent-render page-render
    :component-did-mount #(page-did-mount state)}))
