(defproject com.github.tstout/fin-kratzen "0.1.4"
  :description "Financial Scraper"
  :url "https://github.com/tstout/fin-kratzen"
  :license {:name "MIT"
            :url  "http://opensource.org/licenses/MIT"}
  :dependencies [[org.clojure/clojure "1.10.0"]
                 ;;[org.clojure/clojurescript "1.11.4" :exclusions [org.clojure/clojure junit]]
                 [com.andrewmcveigh/cljs-time "0.5.2"]
                 [nrepl "0.4.0"]
                 [clj-logging-config "1.9.12"]
                 [ring/ring-core "1.6.3" :exclusions [org.clojure/tools.reader]]
                 [ring/ring-jetty-adapter "1.6.3"]
                 [com.google.guava/guava "17.0"]
                 ;;[com.google.guava/guava "23.5-jre"]
                 [com.github.tstout/db-io "1.0.2" :exclusions [com.google.guava/guava]]
                 [com.github.tstout/gd-io "0.1.0" :exclusions [clj-http
                                                               com.google.guava/guava]]
                 [com.draines/postal "2.0.2"]
                 [clj-http "3.7.0"]
                 [expectations "2.1.3"]
                 [trptcolin/versioneer "0.2.0"]
                 [judgr "0.3.0"]
                 [com.stuartsierra/component "0.3.2"]
                 [clj-time "0.14.2"]
                 [org.clojure/java.jdbc "0.7.4"]
                 [com.h2database/h2 "1.4.197"]
                 [jarohen/chime "0.2.2"]
                 [org.clojure/tools.logging "0.4.0"]
                 [org.clojure/core.async "0.3.465"]
                 [org.clojure/data.json "0.2.6"]
                 [org.slf4j/slf4j-log4j12 "1.7.25"]
                 [net.sf.ofx4j/ofx4j "1.6"]
                 [log4j/log4j "1.2.17" :exclusions [javax.mail/mails
                                                    javax.jms/jms
                                                    com.sun.jmdk/jmxtools
                                                    com.sun.jmx/jmxri]]]
  :uberjar-exclusions [#"expectations*" #"junit*"]
  :uberjar-name "fin-kratzen.jar"
  ;;:hooks [leiningen.cljsbuild]

  :plugins [#_[lein-autoexpect "1.4.2"]
            #_[lein-cljsbuild "1.1.7"]
            #_[lein-ring "0.9.3" :exclusions [org.clojure/clojure]]
            #_[lein-expectations "0.0.7"]
            #_[lein-figwheel "0.5.14" :exclusions [org.clojure/clojure org.codehaus.plexus/plexus-utils]]]

  ;;:source-paths ["src"]
  ;;
  :clean-targets ^{:protect false} ["target"]

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

