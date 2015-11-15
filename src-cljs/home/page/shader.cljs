(ns home.page.shader
  (:require [reagent.core :as reagent]
            [libjs.shader :as shader])
  (:require-macros [home.macros :as macros]))
 
(defn shader
  [id type body]
  [:script
   {:id id
    :type (str "x-shader/x-" type)
    :dangerouslySetInnerHTML {:__html body}}])

(defn page-render
  [_]
  [:div#shader-page
   [shader "copy" "fragment" (macros/slurp "resources/public/js/shader/copy.c")]
   [shader "vs" "vertex" (macros/slurp "resources/public/js/shader/vertex.c")]
   [shader "fs" "fragment" (macros/slurp "resources/public/js/shader/fragment.c")]
   [:canvas#shader-canvas]])

(defn page
  [_]
  (reagent/create-class
   {:reagent-render page-render
    :component-did-mount shader/start
    :component-will-unmount shader/stop}))
