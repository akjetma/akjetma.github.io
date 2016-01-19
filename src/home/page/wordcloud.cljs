(ns home.page.wordcloud
  (:require [ajax.core :as http]
            [woolpack.core :as cloud]
            [reagent.core :as reagent]))

(defn rotate
  [deg & [[x y]]]
  (let [deg (or deg 0)]
    (str "rotate(" deg " " x " " y ")")))

(defn translate
  [dx dy]
  (str "translate(" dx ", " dy ")"))

(defn wisp
  [{:keys [word size transform]}]
  ^{:key word}
  [:span.text 
   {:style {:font-size (str size "px")
            :font-family "Courier"
            :line-height "1em"
            :transform transform
            :transform-origin "0px 0px"
            :transition "transform 2s"
            :position "absolute"
            :x 0
            :y 0}}
   word])

(defn cloud
  ([words] (cloud words (.-innerWidth js/window) (.-innerHeight js/window)))
  ([words x-max y-max]
   (let [ww (- (.-innerWidth js/window) 20)
         wh (- (.-innerHeight js/window) 20)
         ratio (min (/ ww x-max) (/ wh y-max))]
     [:div#wordcloud 
      {:style {:transform (str "scale(" ratio ")")
               :transform-origin "0px 0px"
               :transition "transform 1s"}}
      (map wisp words)])))

(defn cloud-word
  [elem]
  (let [width (.-offsetWidth elem)
        height (.-offsetHeight elem)]
    {:word (.-textContent elem)
     :size height
     :width width
     :height height}))

(defn determine-sizes
  [state]
  (let [elems (.querySelectorAll js/document "#wordcloud .text")
        words (map cloud-word elems)]
    (swap! state assoc :wc-words words)))

(defn render-for-sizing
  [state words]
  (let [words
        (map
         (fn [[word size]]
           {:word word 
            :size size})
         words)]
    [cloud words 100 100]))

(defn size-check
  [state words]
  (reagent/create-class
   {:reagent-render render-for-sizing
    :component-did-mount #(determine-sizes state)}))

(defn render-transform
  "add the transform property to a positioned word"
  [{:keys [x y rotation] :as word}]
  (let [x (str x "px")
        y (str y "px")
        rotation (str rotation "deg")]
    (assoc word :transform 
           (str (translate x y) " " (rotate rotation)))))

(defn render-transforms
  [cloud]
  (update cloud :placed (partial map render-transform)))

(defn make-cloud
  "returns a renderable cloud structure from some words"
  [words]
  (render-transforms
   (cloud/billow words)))

(defn handle-words
  [state response]
  (swap! state assoc :wc-raw response :wc-loading false))

(defn handle-error
  [state {:keys [status-text]}]
  (swap! state assoc :wc-loading false :wc-error status-text))

(defn get-words
  [state url]
  (swap! state assoc :wc-raw nil :wc-words nil :wc-error nil :wc-loading true)
  (http/GET "https://akjetma.herokuapp.com/words.json" {:handler (partial handle-words state)
                                                        :error-handler (partial handle-error state)
                                                        :params {:url url}}))

(defn input
  [state]
  (let [url (:wc-url @state)]
    [:div
     [:input {:type "text"
              :placeholder "http://cnn.com"
              :value url
              :on-change #(swap! state assoc :wc-url (-> % .-target .-value))}]
     [:button {:on-click #(get-words state url)} "Go"]]))

(defn display-cloud
  [state words]
  (let [{:keys [x-max y-max placed runs]} (make-cloud words)]
    (fn [_ _]
      [cloud placed x-max y-max])))

(defn load-words
  [state words]
  [:div
   [:h2 "rendering..."]
   [:div {:style {:visibility "hidden"}}
    [size-check state words]]])

(defn page
  [state]
  (let [{:keys [wc-raw wc-words wc-take wc-drop wc-loading wc-error]} @state
        wc-drop (or wc-drop 0)
        wc-take (or wc-take 100)]
    [:div {:style {:padding "10px"}}
     [input state]
     (cond
       wc-loading [:h2 "loading..."]
       wc-error [:h4 wc-error]
       wc-words [display-cloud state (->> wc-words (drop wc-drop) (take wc-take) shuffle)]         
       wc-raw [load-words state wc-raw])]))


