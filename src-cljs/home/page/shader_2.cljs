(ns home.page.shader-2
  (:require [libjs.face :as face]
            [home.component.shader-common :as sc]))
 
(def page
  (sc/make-page
   "shader-2" face/start face/stop
   [sc/quad-shader sc/copy-shader sc/face-shader]))
