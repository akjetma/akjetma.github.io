(ns home.component.svg
  (:require [clojure.string :as string]))
 
(defn svg
  ([body] (svg {} body))
  ([opts body]
   [:svg
    (merge 
     {:xmlns "http://www.w3.org/2000/svg"
      :xmlns:svg "http://www.w3.org/2000/svg"
      :xmlns:xlink "http://www.w3.org/1999/xlink"
      :version "1.0"}
     opts)
    body]))

(defn rotate
  [deg & [[x y]]]
  (let [deg (or deg 0)]
    (str "rotate(" deg " " x " " y ")")))

(defn translate
  [dx dy]
  (str "translate(" dx ", " dy ")"))

(defn text
  ([body size] (text body size nil))
  ([body size transform]
   [:g.text {:transform transform}     
    [:text
     {:font-family "Courier"
      :font-size size
      :x 0
      :y size}
     body]]))

(defn view-box
  ([w h] (view-box 0 0 w h))
  ([x y w h]
   (string/join " " [x y w h])))
