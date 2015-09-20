(ns home.component)

(defn thing-list
  []
  [:div.thing-list.bar-item
   [:h4 "Things"]
   [:div.list
    (for [place ["sorter" "matrix" "cube" "signal"]]
      ^{:key place}
      [:div.thing
       [:a {:href (str "/#/" place)}
        (str place " thing")]])]])

(defn bar
  [state]
  [:div#bar
   [:div.contents
    [:h2.bar-item [:a {:href "/#/"} "adam jetmalani"]]
    [thing-list]]])

(defn state-inspector
  [state]
  (let [shown? (:show-inspector? @state)]
    [:div#state-inspector
     {:style {:height (if shown? "100px" "0px")}}
       [:div#inspector-toggle
        {:on-click #(swap! state update :show-inspector? not)}
        "\u2603"]
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
