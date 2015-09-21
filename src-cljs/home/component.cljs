(ns home.component)

(def social-map
  {"twitter" "http://twitter.com/_magnusmagnus"
   "flickr" "http://flickr.com/whatadam"
   "github-alt" "http://github.com/akjetma"
   "facebook" "http://facebook.com/adam.kumar.jetmalani"
   "linkedin" "http://linkedin.com/in/akjetma"})

(def places
  ["sorter" "matrix" "cube" "signal"])

(defn bar
  [state]
  [:div#bar
   [:div.contents
    [:h2.bar-item [:a {:href "/#/"} "adam jetmalani"]]
    [:div.social.bar-item
     (for [[social link] social-map]
       ^{:key social}
       [:a.social-link {:href link :target "_blank"}
        [:i.fa {:class (str "fa-" social)}]])]
    [:div.thing-list.bar-item
     [:h4 "Things"]
     [:div.list
      (for [place places]
        ^{:key place}
        [:div.thing
         [:a {:href (str "/#/" place)}
          (str place " thing")]])]]]])

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
         (for [[k v] (dissoc @state :current-page)]
           ^{:key k}
           [:div.pair
            [:span.key (str k)]
            [:span.val (str v)]])]])]))

(defn app
  [state]
  [:div
   [bar state]
   [:div#page [(:current-page @state) state]]
   [state-inspector state]])
