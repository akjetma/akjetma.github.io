(ns home.page.home)

(defn page
  [state]
  [:div#home-page
   [:img {:src (str (:asset-prefix @state) "/img/wip.gif")}]])
