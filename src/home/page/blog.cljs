(ns home.page.blog
  (:require [markdown.core :as md])
  (:require-macros [home.macros :as macros]))

(defn file->post-ident
  [file]
  (-> file
      :filename
      (clojure.string/split #"\.")
      first
      keyword))

(def posts
  (into 
   {}
   (mapv
    (fn [file]
      (let [ident (file->post-ident file)]
        [ident (assoc file
                      :ident ident
                      :html (md/md->html (:body file)))]))
    (macros/list-files "resources/blog/posts"))))

(defn render-post
  [post]
  [:div.post
   [:a.return-link {:href "/#/blog"} "<-- back"]
   [:div.body
    {:dangerouslySetInnerHTML 
     {:__html (get-in posts [post :html])}}]])

(defn post-list
  []
  [:div.post-list
   (for [[post _] posts]
     [:a {:href (str "/#/blog/" (name post))}
      (name post)])])

(defn page
  [state]
  [:div#blog-page
   (if-let [post (-> @state :blog :post)]
     [render-post post]
     [post-list])])
