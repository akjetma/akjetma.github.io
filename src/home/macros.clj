(ns home.macros
  (:require [clojure.java.io :as io])
  (:refer-clojure :exclude [slurp]))

(defn file->map
  [file]
  {:path (.toString file)
   :filename (.getName file)
   :body (clojure.core/slurp (.toString file))})

(defmacro list-files
  [path]
  (->> path
       (io/file)
       (.listFiles)
       (remove #(.isHidden %))
       (mapv file->map)))

(defmacro slurp
  [file]
  (clojure.core/slurp file))
