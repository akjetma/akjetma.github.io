(defproject home "0.1.0"
  :dependencies [[org.clojure/clojure "1.7.0-beta3"]
                 [org.clojure/clojurescript "0.0-3269"]
                 [reagent "0.5.0"]
                 [secretary "1.2.3"]]

  :plugins [[lein-cljsbuild "1.0.6"]
            [lein-figwheel "0.3.1"]]

  :clean-targets ^{:protect false} ["resources/public/js" "target"]

  :cljsbuild {:builds [{:id "dev"
                        :source-paths ["src-cljs"]
                        :figwheel {:on-jsload "home.core/fw-reload"}
                        :compiler {:main "home.core"
                                   :asset-path "js/out"
                                   :output-to "resources/public/js/app.dev.js"
                                   :output-dir "resources/public/js/out"
                                   :source-map-timestamp true}}
                       {:id "min"
                        :source-paths ["src-cljs"]
                        :compiler {:main "home.core"
                                   :output-to "resources/public/js/app.min.js"
                                   :optimizations :advanced
                                   :pretty-print false}}]}

   :figwheel {:css-dirs ["resources/public/css"]})
