(ns kratzen.logging
  (:require [clj-logging-config.log4j :as log-cfg]
            [com.stuartsierra.component :as component]
            [clojure.java.jdbc :as jdbc]
            [clojure.pprint :as pp]
            [kratzen.db :refer [h2-local]])
  (:import (org.apache.log4j EnhancedPatternLayout ConsoleAppender FileAppender AppenderSkeleton)
           (java.util Date)))

;;
;; Logging config via https://github.com/malcolmsparks/clj-logging-config
;;
(def ^:private log-ns
  ["kratzen.db"
   "kratzen.server"
   "kratzen.http"
   "kratzen.boa"
   "kratzen.scheduler"
   "kratzen.model"])

(defn- log-ev->map [ev]
  (assoc (bean ev) :event ev))

(defn- ev->db-col [ev]
  (let [ev-map (log-ev->map ev)]
    {:when   (Date. (:timeStamp ev-map))
     :logger (:loggerName ev-map)
     :level  (str (:level ev-map))
     :msg    (:message ev-map)
     :thread (:threadName ev-map)
     :ndc    (:NDC ev-map)}))

(defn mk-db-appender [db-spec]
  "Create a log4j Appender that writes to the DB"
  (let [spec db-spec]
    (proxy [AppenderSkeleton] []
      (append [ev]
        (jdbc/insert! spec :finkratzen.log (ev->db-col ev)))

      (close []
        nil))))

(defn- init-logging [db-spec]

  (#_
    (log-cfg/set-config-logging-level! :debug))

  (log-cfg/set-loggers!
    log-ns
    {:name  "console"
     :level :debug
     :out   (ConsoleAppender.
              (EnhancedPatternLayout. "%d %-5p %c: %m%n"))}
    log-ns
    {:name  "database"
     :level :debug
     :out   (mk-db-appender db-spec)}))

(defrecord Logger [channels db-spec]
  component/Lifecycle

  (start [this]
    (init-logging db-spec)
    ;;(log-rx channels db-spec)
    this)

  (stop [this]
    this))