(defproject com.github.tstout/fin-kratzen "0.1.0"
            :description "Financial Scraper"
            :url "https://github.com/tstout/fin-kratzen"
            :license {:name "MIT"
                      :url  "http://opensource.org/licenses/MIT"}
            :dependencies [[org.clojure/clojure "1.6.0"]
                           [ring/ring-core "1.3.0"]
                           [ring/ring-jetty-adapter "1.3.0"]
                           [com.github.tstout/db-io "1.0.2"]
                           [net.sf.ofx4j/ofx4j "1.6"]
                           [com.github.tstout/ofx-io "0.1.0"]
                           [expectations "2.0.6"]
                           [com.stuartsierra/component "0.2.2"]
                           [clj-time "0.8.0"]
                           [org.clojure/java.jdbc "0.3.5"]
                           [clj-http "1.0.1"]
                           [com.h2database/h2 "1.3.176"]
                           [org.clojure/tools.logging "0.3.1"]
                           [org.clojure/data.json "0.2.6"]
                           [org.slf4j/slf4j-log4j12 "1.6.6"]
                           [log4j/log4j "1.2.17" :exclusions [javax.mail/mail
                                                              javax.jms/jms
                                                              com.sun.jmdk/jmxtools
                                                              com.sun.jmx/jmxri]]]
            :uberjar-exclusions [#"expectations*" #"junit*"]
            :plugins [[lein-idea "1.0.1"]
                      [lein-autoexpect "1.4.2"]
                      [lein-expectations "0.0.7"]]
            :resource-paths ["resources" "resources/sql"]
            :profiles {:dev {:source-paths ["dev"]
                             :dependencies [[org.clojure/tools.namespace "0.2.8"]]}}
            :repl-options {:init-ns user}
            :main kratzen.core
            :jvm-opts ["-Dcom.sun.management.jmxremote"
                       "-Dcom.sun.management.jmxremote.port=8004"
                       "-Dcom.sun.management.jmxremote.authenticate=false"
                       "-Dcom.sun.management.jmxremote.ssl=false"])
;;:aot [kratzen.core]) ;; causes trouble with namespace reloading

