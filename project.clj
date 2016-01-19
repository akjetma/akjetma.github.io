(defproject home "0.1.0"
  :dependencies [;; clojure:
                 [org.clojure/clojure "1.7.0"]
                 [http-kit "2.1.18"]
                 [polaris "0.0.15"]
                 [ring "1.4.0"]
                 [ring-cors "0.1.7"]
                 [enlive "1.1.6"]
                 [org.clojure/data.json "0.2.6"]                 
                 [org.clojure/math.numeric-tower "0.0.4"]
                 
                 ;; clojurescript:
                 [org.clojure/clojurescript "1.7.145"]                 
                 [binaryage/devtools "0.4.1"]
                 [reagent "0.5.1"]
                 [secretary "1.2.3"]
                 [cljs-ajax "0.5.2"]
                 [akjetma/woolpack "0.1.0"]]

  :plugins [[lein-cljsbuild "1.1.0"]
            [lein-figwheel "0.5.0-1"]]

  :main home.server

  :source-paths ["src"]

  :clean-targets ^{:protect false} ["resources/public/js/app.js"
                                    "resources/public/js/out"
                                    "resources/public/css/home.css"
                                    "target"]

  :cljsbuild {:builds 
              [{:id "dev"
                :source-paths ["src"]
                :figwheel {:on-jsload "home.client/load"}
                :compiler {:main home.client
                           :asset-path "js/out"
                           :libs ["homejs"]
                           :output-to "resources/public/js/app.js"
                           :output-dir "resources/public/js/out"
                           :source-map-timestamp true}}
               {:id "min"
                :source-paths ["src"]
                :compiler {:main home.client
                           :output-to "resources/public/js/app.js"
                           :libs ["homejs"]
                           :optimizations :advanced
                           :pretty-print false}}]}

  :figwheel {:css-dirs ["resources/public/css"]})
