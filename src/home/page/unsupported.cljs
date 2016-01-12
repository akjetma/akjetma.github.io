(ns home.page.unsupported)

(defn page
  [state]
  [:div#unsupported-page
   [:h1 (str "no " (-> @state :browser name) " :(")]])
