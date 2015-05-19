(ns home.page.tests)

(defn blank-page
  [state]
  [:div])

(defn test-page
  [state]
  [:div
   [:h1 "test"]
   [:a {:href "/#/"} "home"]])

(defn home-page
  [state]
  [:div
   [:h1 "home"]
   [:ul
    [:li
     [:a {:href "/#/sorter"} "sort things"]]
    [:li
     [:a {:href "/#/test"} "test"]]]])

