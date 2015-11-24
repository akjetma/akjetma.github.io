(ns home.page.shader
  (:require [jsh.gol :as gol]
            [home.component.shader-common :as sc]))
 
(def page
  (sc/make-page
   "shader" gol/start gol/stop
   [sc/quad-shader sc/copy-shader sc/gol-shader]))
