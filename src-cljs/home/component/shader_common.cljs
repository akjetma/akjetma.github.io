(ns home.component.shader-common
  (:require [reagent.core :as reagent])
  (:require-macros [home.macros :as macros]))

(defn shader
  [type id body]
  [:script
   {:id id
    :type (str "x-shader/x-" type)
    :dangerouslySetInnerHTML {:__html body}}])

(defn quad-shader []
  [shader "vertex" "quad" (macros/slurp "resources/public/js/shader/quad.c")])

(defn copy-shader []
  [shader "fragment" "copy" (macros/slurp "resources/public/js/shader/copy.c")])

(defn gol-shader []
  [shader "fragment" "gol" (macros/slurp "resources/public/js/shader/gol.c")])

(defn make-render
  ([prefix shaders] (make-render prefix shaders nil))
  ([prefix shaders body]
   (fn shader-render
     [_]
     [:div {:id (str prefix "-page")}
      [:canvas {:id (str prefix "-canvas")}]
      (map-indexed
       (fn [i shader] ^{:key i} [shader])
       shaders)      
      body])))

(defn make-page
  ([prefix start stop shaders] (make-page prefix start stop shaders nil))
  ([prefix start stop shaders body]
   (fn shader-page
     [_]
     (reagent/create-class
      {:reagent-render (make-render prefix shaders body)
       :component-did-mount start
       :component-will-unmount stop}))))
