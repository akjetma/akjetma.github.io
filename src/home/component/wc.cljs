(ns home.component.wc
  (:require [home.component.svg :as svg]
            [reagent.core :as reagent]))

(defn cloud
  [words cloud-width cloud-height]
  [svg/svg
   {:width "100%"
    :height "100%"
    :view-box (str "0 0 " cloud-width " " cloud-height)
    :id "wordcloud"}
   (for [{:keys [word size transform]} words]
     ^{:key word}
     [svg/text word size transform])])



(defn render-sizes
  [qi words]
  (let [words
        (map
         (fn [[word size]]
           {:word word 
            :size size})
         words)]
    [cloud words 100 100]))

(defn word-from-element
  [elem]
  (let [bb (.getBBox elem)
        size (.getAttribute elem "font-size")]
    {:word (.-textContent elem)
     :size (js/parseInt size)
     :width  (.-width bb)
     :height (.-height bb)}))

(defn after-sized
  [qi]
  (let [elems (.querySelectorAll js/document "#wordcloud text")
        words (map word-from-element elems)]
    (swap! qi assoc :words words)))

(defn size-words
  [qi words]
  (reagent/create-class
   {:reagent-render render-sizes
    :component-did-mount #(after-sized qi)}))

(defn transform-ratio
  [x-max y-max ww wh]
  (min (/ ww x-max)
       (/ wh y-max)))

(defn html-cloud
  ([words] (html-cloud words (.-innerWidth js/window) (.-innerHeight js/window)))
  ([words x-max y-max]
   (let [ww (- (.-innerWidth js/window) 20)
         wh (- (.-innerHeight js/window) 20)
         ratio (transform-ratio x-max y-max ww wh)]
     [:div#wordcloud 
      {:style {:transform (str "translate(20px, 20px) scale(" ratio ")")
               :transform-origin "0px 0px"
               :transition "transform 1s"}}
      (for [{:keys [word size transform]} words]
        ^{:key word}
        [:span.text
         {:style
          {:font-size (str size "px")
           :font-family "Courier"
           :line-height "1em"
           :transform transform
           :transform-origin "0px 0px"
           :transition "transform 2s"
           :position "absolute"
           :x 0
           :y 0}}
         word])])))

(defn html-word-from-element
  [elem]
  (let [width (.-offsetWidth elem)
        height (.-offsetHeight elem)]
    {:word (.-textContent elem)
     :size height
     :width width
     :height height}))

(defn html-after-sized
  [qi]
  (let [elems (.querySelectorAll js/document "#wordcloud .text")
        words (map html-word-from-element elems)]
    (swap! qi assoc :words words)))

(defn html-render-sizes
  [qi words]
  (let [words
        (map
         (fn [[word size]]
           {:word word 
            :size size})
         words)]
    [html-cloud words 100 100]))

(defn html-size-words
  [qi words]
  (reagent/create-class
   {:reagent-render html-render-sizes
    :component-did-mount #(html-after-sized qi)}))
