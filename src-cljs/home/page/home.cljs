(ns home.page.home)

(defn place
  [loc]
  [:li
   [:a {:href (str "/#/" loc)}
    (str loc " thing")]])

(defn page
  [state]
  [:ul
   (map place ["sorter" "matrix" "cube" "signal"])])
