(defproject com.github.tstout/fin-kratzen "0.1.3"
  :description "Financial Scraper"
  :url "https://github.com/tstout/fin-kratzen"
  :license {:name "MIT"
            :url  "http://opensource.org/licenses/MIT"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/clojurescript "1.7.228" :exclusions [org.clojure/clojure junit]]
                 [com.andrewmcveigh/cljs-time "0.3.11"]
                 [clj-logging-config "1.9.12"]
                 [ring/ring-core "1.4.0" :exclusions [org.clojure/tools.reader]]
                 [ring/ring-jetty-adapter "1.4.0"]
                 [racehub/om-bootstrap "0.5.3"]
                 [com.github.tstout/db-io "1.0.3" :exclusions [com.google.guava/guava]]
                 [com.github.tstout/ofx-io "0.1.1"]
                 [com.github.tstout/gd-io "0.1.0"]
                 [com.draines/postal "1.11.3"]
                 [clj-http "3.7.0"]
                 [expectations "2.1.3"]
                 [trptcolin/versioneer "0.2.0"]
                 [judgr "0.3.0"]
                 [com.stuartsierra/component "0.2.3"]
                 [clj-time "0.11.0"]
                 [org.clojure/java.jdbc "0.4.1"]
                 [com.h2database/h2 "1.3.176"]
                 [sablono "0.3.4"]
                 [jarohen/chime "0.1.6"]
                 [org.omcljs/om "0.9.0"]
                 [org.clojure/tools.logging "0.3.1"]
                 [org.clojure/core.async "0.2.374"]
                 [org.clojure/data.json "0.2.6"]
                 [org.slf4j/slf4j-log4j12 "1.7.12"]
                 [net.sf.ofx4j/ofx4j "1.6"]
                 [log4j/log4j "1.2.17" :exclusions [javax.mail/mails
                                                    javax.jms/jms
                                                    com.sun.jmdk/jmxtools
                                                    com.sun.jmx/jmxri]]]
  :uberjar-exclusions [#"expectations*" #"junit*"]
  :hooks [leiningen.cljsbuild]

  :plugins [[lein-autoexpect "1.4.2"]
            [lein-cljsbuild "1.1.2"]
            [lein-ring "0.9.3" :exclusions [org.clojure/clojure]]
            [lein-expectations "0.0.7"]
            [lein-figwheel "0.3.2" :exclusions [org.clojure/clojure org.codehaus.plexus/plexus-utils]]]

  ;;:source-paths ["src"]
  ;;
  ;; clojurescript stuff...
  ;;
  :clean-targets ^{:protect false} ["resources/public/js/compiled" "target"]

  :cljsbuild {
              :builds [{:id           "dev"
                        :source-paths ["src-cljs"]

                        :figwheel     {:on-jsload "kratzen.main/on-js-reload"}

                        :compiler     {:main                 kratzen.main
                                       :asset-path           "js/compiled/out"
                                       :output-to            "resources/public/js/compiled/kratzen.js"
                                       :output-dir           "resources/public/js/compiled/out"
                                       :source-map-timestamp true
                                       :verbose              true}}

                       {:id           "release"
                        :source-paths ["src-cljs"]
                        :compiler     {
                                       :output-to     "resources/public/js/compiled/kratzen.js"
                                       :optimizations :advanced
                                       :pretty-print  false}}]}
  :figwheel {
             ;; :http-server-root "public" ;; default and assumes "resources"
             ;; :server-port 3449 ;; default
             :css-dirs ["resources/public/css"]             ;; watch and update CSS

             ;; Start an nREPL server into the running figwheel process
             ;; :nrepl-port 7888

             ;; Server Ring Handler (optional)
             ;; if you want to embed a ring handler into the figwheel http-kit
             ;; server, this is for simple ring servers, if this
             ;; doesn't work for you just run your own server :)
             ;; :ring-handler hello_world.server/handler

             ;; To be able to open files in your editor from the heads up display
             ;; you will need to put a script on your path.
             ;; that script will have to take a file path and a line number
             ;; ie. in  ~/bin/myfile-opener
             ;; #! /bin/sh
             ;; emacsclient -n +$2 $1
             ;;
             ;; :open-file-command "myfile-opener"

             ;; if you want to disable the REPL
             ;; :repl false

             ;; to configure a different figwheel logfile path
             ;; :server-logfile "tmp/logs/figwheel-logfile.log"
             }
  :ring {:handler kratzen.http/handler}
  :resource-paths ["resources" "resources/sql" "resources/public"]
  :profiles {:dev {:source-paths ["dev"]
                   :dependencies [[org.clojure/tools.namespace "0.2.11"]
                                  [org.clojure/java.classpath "0.2.3"]]}}
  :repl-options {:init-ns user}
  :main kratzen.core)
;:jvm-opts ["-Dcom.sun.management.jmxremote"
;           "-Dcom.sun.management.jmxremote.port=8004"
;           "-Dcom.sun.management.jmxremote.authenticate=false"
;           "-Dcom.sun.management.jmxremote.ssl=false"])
;;:aot [kratzen.core]) ;; causes trouble with namespace reloading

