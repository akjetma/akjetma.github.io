(ns home.core
  (:require [reagent.core :as reagent]))

(defn test-component
  []
  [:h1 "test"])

(reagent/render-component
 [test-component]
 (.getElementById js/document "app"))
