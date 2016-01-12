(ns home.page.gol
  (:require [jsh.gol :as gol-js]
            [home.component.shader-common :as sc]))
 
(def page
  (sc/make-page
   "gol" gol-js/start gol-js/stop
   [sc/quad-shader sc/copy-shader sc/gol-shader]))
