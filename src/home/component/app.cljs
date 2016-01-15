(ns home.component.app
  (:require [home.routes :as routes]
            [home.support :as support]))

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
      [:div.thing-list.bar-item       
       [:div.list
        (for [place support/everything]
          ^{:key place}
          [:div.thing (when-not (some #{place} possible) {:class "impossible"})
           [:a {:href (str "/#/" (name place))}
            (name place)]])]]
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
