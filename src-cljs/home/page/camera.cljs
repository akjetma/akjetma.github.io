(ns home.page.camera
  (:require [reagent.core :as reagent]))

(def framerate (/ 1000 300))
(def buffer-ct 40)

(defn set-gum?
  []
  (when-let [gum (some (partial aget js/navigator)
                       ["getUserMedia" "webkitGetUserMedia" "mozGetUserMedia" "msGetUserMedia"])]
    (set! (.-getUserMedia js/navigator) gum)
    true))

(defn buffer-loop
  [buffer video width height]
  (let [old-buffer-data (.getImageData buffer 0 0 (* (dec buffer-ct) width) height)]   
    (.drawImage buffer video 0 0)
    (.putImageData buffer old-buffer-data width 0)
    (.setTimeout js/window #(buffer-loop buffer video width height) framerate)))

(defn output-loop
  [output buffer width row-height]
  (doall
   (for [row (range buffer-ct)]
     (.putImageData
      output
      (.getImageData buffer (* width row) (* row-height row) width row-height)
      0
      (* row-height row))))
  (.setTimeout js/window #(output-loop output buffer width row-height) framerate))

(defn start-loop
  [state]
  (let [{:keys [camera-video camera-object-url camera-output camera-buffer]} @state]
    (set! (.-src camera-video) camera-object-url)
    (.setTimeout
     js/window
     (fn []
       (let [{:keys [width height]} (:camera-dimensions @state)]
         (buffer-loop camera-buffer camera-video width height)
         (output-loop camera-output camera-buffer width (/ height buffer-ct))))
     1000)))

(defn got-media
  [state stream]
  (swap! state assoc :camera-object-url (-> js/window .-URL (.createObjectURL stream)))
  (start-loop state))





(defn output-render
  [state]
  (let [{:keys [width height]} (:camera-dimensions @state)]
    [:canvas#output {:width width :height height}]))

(defn output-did-mount
  [state]
  (swap! state assoc :camera-output (-> js/document (.querySelector "#output") (.getContext "2d"))))

(defn output
  [state]
  (reagent/create-class
   {:reagent-render output-render
    :component-did-mount #(output-did-mount state)}))




(defn buffer-render
  [state]
  (let [{:keys [width height]} (:camera-dimensions @state)]
    [:canvas#buffer {:width (* buffer-ct width) :height height}]))

(defn buffer-did-mount
  [state]
  (swap! state assoc :camera-buffer (-> js/document (.querySelector "#buffer") (.getContext "2d"))))

(defn buffer
  [state]
  (reagent/create-class
   {:reagent-render buffer-render
    :component-did-mount #(buffer-did-mount state)}))


(defn input-render
  [_]
  [:video#input {:auto-play true}])

(defn input-did-mount
  [state]
  (let [video (.querySelector js/document "#input")]
    (swap! state assoc :camera-video video)
    (.addEventListener video "canplay"
     #(swap! state assoc :camera-dimensions {:width (-> % .-target .-videoWidth) :height (-> % .-target .-videoHeight)}))))

(defn input
  [state]
  (reagent/create-class
   {:reagent-render input-render
    :component-did-mount #(input-did-mount state)}))




(defn page-render
  [state]
  [:div#camera-page
   [input state]
   [buffer state]
   [output state]])

(defn page-did-mount
  [state]
  (cond
    (:camera-object-url @state) (start-loop state) 

    (set-gum?)
    (.getUserMedia
     js/navigator #js {:video true}
     (partial got-media state)
     #(.log js/console "error: " %))

    :else (swap! state assoc :camera-error :unsupported)))

(defn page
  [state]
  (reagent/create-class
   {:reagent-render page-render
    :component-did-mount #(page-did-mount state)}))
