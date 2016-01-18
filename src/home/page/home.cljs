(ns home.page.home
  (:require [home.util :as util]))

(defn page
  [state]
  [:div#home-page
   [:div.box
    [:ul
     [:li "Hello! My name is Adam Jetmalani and sometimes I make web toys."]
     [:li "I can write non-webtoy code too. Pretty resume " [:a.link {:href (util/resource state "/resume.html")} "here"] ". PDF resume " [:a.link {:href (util/resource state "/AdamJetmalani.pdf")} "here"] "."]]]])
