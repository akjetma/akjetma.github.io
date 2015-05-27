(ns home.page.home)

(defn page
  [state]
  [:ul
   [:li
    [:a {:href "/#/sorter"} "element sorter thing"]]
   [:li
    [:a {:href "/#/matrix"} "matrix thing"]]])
