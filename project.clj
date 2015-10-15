(defproject home "0.1.0"
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [org.clojure/clojurescript "1.7.145"]
                 [reagent "0.5.1"]
                 [secretary "1.2.3"]]

  :plugins [[lein-cljsbuild "1.1.0"]
            [lein-figwheel "0.4.1"]]

  :clean-targets ^{:protect false} ["resources/public/js/app.dev.js"
                                    "resources/public/js/app.min.js"
                                    "resources/public/js/out"
                                    "resources/public/css/home.css"
                                    "target"]

  :cljsbuild {:builds [{:id "dev"
                        :source-paths ["src-cljs"]
                        :figwheel {:on-jsload "home.core/load"}
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
