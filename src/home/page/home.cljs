(ns home.page.home)

(defn page
  [state]
  [:div#home-page
   [:div.box
    [:ul
     [:li "Hello! My name is Adam Jetmalani and sometimes I make web toys."]
     [:li "I can write non-webtoy code too. " [:a.link {:href "/resume"} "Resume right here."]]]]])
