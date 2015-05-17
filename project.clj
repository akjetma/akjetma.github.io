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
                        :figwheel true
                        :compiler {:main "home.core"
                                   :asset-path "js/out"
                                   :output-to "resources/public/js/app.js"
                                   :output-dir "resources/public/js/out"
                                   :source-map true
                                   :pretty-print true}}]}

  :figwheel {:css-dirs ["resources/public/css"]})
