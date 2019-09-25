(defproject home "0.1.0"
  :dependencies [[org.clojure/clojure "1.9.0"]                                 
                 [org.clojure/clojurescript "1.10.312"]                 
                 [binaryage/devtools "0.5.2"]
                 [reagent "0.5.1"]
                 [secretary "1.2.3"]
                 [cljs-ajax "0.5.2"]
                 [markdown-clj "0.9.99"]
                 [akjetma/woolpack "0.1.5"]]

  :plugins [[lein-cljsbuild "1.1.4"]
            [lein-figwheel "0.5.16"]]

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
                           :asset-path "resources/public/js/out"
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
