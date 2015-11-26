(ns home.page.fol
  (:require [jsh.fol :as fol-js]
            [home.component.shader-common :as sc]))
    
(def page
  (sc/make-page
   "fol" fol-js/start fol-js/stop
   [sc/quad-shader sc/copy-shader sc/fol-shader]))
