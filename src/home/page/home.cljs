(ns home.page.home
  (:require [home.util :as util]))

(defn page
  [state]
  [:div#home-page
   [:div.box
    [:ul
     [:li "Hi, this is my dumb Clojurescript web app thing."]
     [:li "The client is a statically hosted Github Pages type thing. " [:a.link {:href "https://github.com/akjetma/akjetma.github.io"} "[src]"]]
     [:li "There's a little API running on Heroku for the wordcloud thing. " [:a.link {:href "https://github.com/akjetma/akjetma-back"} "[src]"]]]]])
