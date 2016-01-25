(ns home.page.home
  (:require [home.util :as util]))

(defn page
  [state]
  [:div#home-page
   [:div.box
    [:ul
     [:li "Hello! My name is Adam and sometimes I make web toys."]
     [:li "This website also serves as an example project for a Clojure/Clojurescript web app."]
     [:li "The tiny API is hosted on Heroku. Source code " [:a.link {:href "https://github.com/akjetma/akjetma-back"} "here"]]
     [:li "The client is a static page hosted via Github Pages. Source code " [:a.link {:href "https://github.com/akjetma/akjetma.github.io"} "here"]]
     [:li "For more info about me, see my resume " [:a.link {:href (util/resource state "/resume.html")} "here"]]]]])
