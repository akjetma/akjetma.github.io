(ns home.page.home
  (:require [home.util :as util]))

(defn page
  [state]
  [:div#home-page
   [:div.box
    [:ul
     [:li "Hi, this is my Clojurescript web app thing."]
     [:li "The client is a statically hosted Github Page. " [:a.link {:href "https://github.com/akjetma/akjetma.github.io"} "[src]"]]
     [:li "There's a little API running on Heroku for the wordcloud generator. " [:a.link {:href "https://github.com/akjetma/akjetma-back"} "[src]"]]]]])
