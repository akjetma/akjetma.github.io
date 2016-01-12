(ns home.page.photos
  (:require [jsh.photos :as photos-js]
            [home.component.shader-common :as sc]))

(def page
  (sc/make-page
   "photos" photos-js/start photos-js/stop
   [sc/quad-shader sc/photos-shader]))
