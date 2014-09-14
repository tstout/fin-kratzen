(defproject com.github.tstout/fin-kratzen "0.1.0"
  :description "Financial Scraper"
  :url "https://github.com/tstout/fin-kratzen"
  :license {:name "MIT"
            :url "http://opensource.org/licenses/MIT"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [com.github.tstout/db-io "1.0.1"]
                 [com.github.tstout/ofx-io "0.0.1"]
                 [expectations "2.0.6"]
                 [clj-time "0.8.0"]
                 [com.h2database/h2 "1.3.167"]
                 [org.clojure/tools.logging "0.2.6"]
                 [org.slf4j/slf4j-log4j12 "1.6.6"]
                 [log4j/log4j "1.2.17" :exclusions [javax.mail/mail
                                                    javax.jms/jms
                                                    com.sun.jmdk/jmxtools
                                                    com.sun.jmx/jmxri]]]
  :uberjar-exclusions [#"expectations*" #"junit*"]
  :plugins [[lein-idea "1.0.1"]
            [lein-expectations "0.0.7"]]
  :resource-paths ["resources" "resources/sql"]
  :main kratzen.core
  :aot [kratzen.core])

