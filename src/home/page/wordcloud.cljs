(ns home.page.wordcloud
  (:require [ajax.core :as http]
            [home.debug :as cloud]
            [reagent.core :as reagent]
            [home.util :as util]))

(defn rotate
  [deg & [[x y]]]
  (let [deg (or deg 0)]
    (str "rotate(" deg " " x " " y ")")))

(defn translate
  [dx dy]
  (str "translate(" dx ", " dy ")"))

(defn remove-word
  [state word]
  (swap! state update :wc-selected (partial remove #{word})))

(defn wisp
  [state {:keys [word size transform]}]
  [:span.text 
   {:on-click #(remove-word state word) 
    :style {:cursor "pointer"
            :font-size (str size "px")
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
  [state words selected x-max y-max]
  (let [page (.getElementById js/document "page")
        ww (- (.-offsetWidth page) 20)
        wh (- (.-innerHeight js/window) 20)
        ratio (min (/ ww x-max) (/ wh y-max))]
    [:div#wordcloud 
     {:style {:transform (str "scale(" ratio ")")
              :transform-origin "0px 0px"
              :transition "transform 1s"
              :width "0px"}}
     (for [word selected]
       ^{:key word}
       [wisp state (get words word)])]))



;; v----- display layout -----v

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

(defn display-cloud
  [state words chosen]
  (let [{:keys [x-max y-max placed]} (make-cloud (map (partial get words) chosen))
        w-map (into {} (map (juxt :word identity) placed))]
    [cloud state w-map chosen x-max y-max]))

;; ^----- display layout -----^



;; v----- initial layout -----v

(defn dimension
  [elem]
  (let [width (.-offsetWidth elem)
        height (.-offsetHeight elem)]
    {:word (.-textContent elem)
     :size height
     :width width
     :height height}))

(defn calculate
  [state]
  (let [elems (.querySelectorAll js/document "#wordcloud .text")
        words (map dimension elems)
        words (into {} (map (juxt :word identity) words))]
    (swap! state assoc :wc-words words :wc-selected (-> words keys shuffle))))

(defn layout
  [state words]
  [cloud state words (keys words) 100 100])

(defn compute
  [state words]
  (reagent/create-class
   {:reagent-render layout
    :component-did-mount #(calculate state)}))

(defn load-words
  [state words]
  [:div
   [:h2 "rendering..."]
   [:div {:style {:visibility "hidden"}}
    [compute state words]]])

;; ^----- initial layout -----^



;; v---------- page ----------v

(defn handle-words
  [state response]
  (let [biggest (apply max (vals response))
        scale (/ 100 biggest)
        raw (into {} (map
                      (fn [[word size]] [word {:word word :size (* scale size)}])
                      (take 100 (sort-by last > response))))]
    (swap! state assoc :wc-raw raw :wc-loading false)))

(defn handle-error
  [state {:keys [response status-text]}]
  (swap! state assoc 
         :wc-loading false 
         :wc-error (str status-text ": " response)))

(defn get-words
  [state url]
  (swap! state assoc :wc-raw nil :wc-words nil :wc-selected nil :wc-error nil :wc-loading true)
  (http/GET (str (:server @state) "/words.json")
            {:handler (partial handle-words state)
             :error-handler (partial handle-error state)
             :params {:url url}}))

(defn submit
  [state url]
  (if-let [url (or (util/url? url) 
                   (util/url? (str "http://" url)))] 
    (get-words state url)
    (swap! state assoc :wc-error "Invalid URL :(")))

(defn input
  [state]
  (let [{url :wc-url loaded :wc-words} @state]
    [:div     
     [:input {:type "text"
              :placeholder "http://cnn.com"
              :value url
              :on-change #(swap! state assoc :wc-url (-> % .-target .-value))
              :on-key-down #(when (= (.-keyCode %) 13) (submit state url))}]
     [:button {:disabled (not url)
               :on-click #(submit state url)} 
      "Go"]
     (when loaded
       [:span 
        " click on a word to remove it"])]))

(defn page
  [state]
  (let [{:keys [wc-raw wc-words wc-loading wc-error wc-selected]} @state]
    [:div {:style {:padding "10px"}}
     [input state]
     (cond
       wc-loading [:h2 "loading..."]
       wc-error [:h4 wc-error]
       wc-words [display-cloud state wc-words wc-selected]
       wc-raw [load-words state wc-raw])]))


