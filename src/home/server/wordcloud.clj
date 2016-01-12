(ns home.server.wordcloud
  (:require [clojure.string :as string]
            [clojure.java.io :as io]
            [net.cgrand.enlive-html :as html])
  (:refer-clojure :exclude [frequencies]))

(def stopwords
  (-> "static/stopwords.txt" io/resource slurp
      (string/split #"\n")
      set))

(def example-frequencies
  (slurp (io/resource "static/wc-wc.json")))

(def tags
  [:div :h1 :h2 :h3 :h4 :h5 :a :span :p :b])

(def dbg (atom nil))

(defn debug
  [mystery]
  (reset! dbg mystery)
  mystery)

(defn html
  [url]
  (debug (html/html-resource (java.net.URL. url))))

(defn text
  [html]
  (mapcat
   (comp
    (partial map html/text)
    (partial html/select html)
    vector)
   tags))

(defn clean
  [text]
  (filter
   (partial re-matches #"\w+")
   (remove 
    stopwords
    (string/split 
     (string/lower-case
      (string/join " " text))
     #" "))))

(defn frequencies
  ([url] (frequencies url 500))
  ([url limit]
   (let [words (-> url html text clean)
         freqs (clojure.core/frequencies words)
         top (take limit (sort-by last > freqs))]
     (into {} top))))
