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
  [context video]
  (.drawImage context video 0 0)
  (.setTimeout js/window #(render-loop context video) framerate))

(defn start-loop
  [context video object-url]
  (set! (.-src video) object-url)
  (render-loop context video))

(defn got-media
  [state context video stream]
  (let [object-url (-> js/window .-URL (.createObjectURL stream))]
    (swap!
     state assoc
     :camera-stream stream
     :camera-object-url object-url)
    (start-loop context video object-url)))





(defn output-render
  [state]
  [:canvas#output (:camera-dimensions @state)])

(defn output-did-mount
  [state]
  (let [canvas (.querySelector js/document "#output")
        context (.getContext canvas "2d")]
    (swap! state assoc
           :camera-canvas canvas
           :camera-context context)))

(defn output
  [state]
  (reagent/create-class
   {:reagent-render output-render
    :component-did-mount #(output-did-mount state)}))




(defn input-render
  [_]
  [:video#input {:auto-play true}])

(defn input-did-mount
  [state]
  (let [video (.querySelector js/document "#input")]
    (swap! state assoc :camera-video video)
    (.addEventListener
     video "canplay"
     #(swap! state assoc
             :camera-dimensions {:width (.-videoWidth video) :height (.-videoHeight video) :style {:height "110vh"}}))
    ))

(defn input
  [state]
  (reagent/create-class
   {:reagent-render input-render
    :component-did-mount #(input-did-mount state)}))




(defn page-render
  [state]
  [:div#camera-page
   [output state]
   [input state]])

(defn page-did-mount
  [state]
  (let [{:keys [camera-context camera-video camera-object-url]} @state]
    (if camera-object-url
      (start-loop camera-context camera-video camera-object-url) 
      (if (set-gum?)
        (.getUserMedia
         js/navigator #js {:video true}
         (partial got-media state camera-context camera-video)
         #(.log js/console "error: " %))
        (swap! state assoc :camera-error :unsupported)))))

(defn page
  [state]
  (reagent/create-class
   {:reagent-render page-render
    :component-did-mount #(page-did-mount state)}))
