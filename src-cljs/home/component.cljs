(ns home.component
  (:require [home.routes :as routes]))

(def social-map
  {"twitter" "http://twitter.com/_magnusmagnus"
   "flickr" "http://flickr.com/whatadam"
   "github-alt" "http://github.com/akjetma"
   "facebook" "http://facebook.com/adam.kumar.jetmalani"
   "linkedin" "http://linkedin.com/in/akjetma"
   "envelope" "mailto:adamsinternetmailbox@gmail.com"})

(defn bar
  [state]
  (let [possible (:things @state)]
    [:div#bar
     [:div.contents
      [:h2.bar-item [:a {:href "/#/home"} "adam jetmalani"]]
      [:div.thing-list.bar-item
       [:h4 "things"]
       [:div.list
        (for [place routes/current-things]
          ^{:key place}
          [:div.thing (when-not (some #{place} possible) {:class "impossible"})
           [:a {:href (str "/#/" (name place))}
            (str (name place) " thing")]])]]
      [:div.social.bar-item
       (for [[social link] social-map]
         ^{:key social}
         [:a.social-link {:href link :target "_blank"}
          [:i.fa {:class (str "fa-" social)}]])]]]))

(defn state-inspector
  [state]
  (let [shown? (:show-inspector? @state)]
    [:div#state-inspector
     {:style {:height (if shown? "100px" "0px")}}
       [:div#inspector-toggle
        {:on-click #(swap! state update :show-inspector? not)}
        "氣"]
     (when shown?
       [:div.code-container
        [:code.edn-map 
         (for [[k v] @state]
           ^{:key k}
           [:div.pair
            [:span.key (str k)]
            [:span.val (str v)]])]])]))

(defn app
  [state]
  [:div#app
   [bar state]
   [:div#page [(get routes/page-map (:current-page @state)) state]]
   [state-inspector state]])
