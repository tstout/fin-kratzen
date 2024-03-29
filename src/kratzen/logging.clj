(ns kratzen.logging
  (:require [clj-logging-config.log4j :as log-cfg]
            [com.stuartsierra.component :as component]
            [clojure.java.jdbc :as jdbc]
            [kratzen.db :refer [h2-local]])
  (:import (org.apache.log4j EnhancedPatternLayout ConsoleAppender AppenderSkeleton)
           (java.util Date)))

;;
;; Logging config via https://github.com/malcolmsparks/clj-logging-config
;;
(def log-ns
  ["kratzen.db"
   "kratzen.backup"
   "kratzen.server"
   "kratzen.http"
   "kratzen.boa"
   "kratzen.scheduler"
   "kratzen.model"
   "kratzen.email"
   "kratzen.ssh"])

(defn- log-ev->map [ev]
  (assoc (bean ev) :event ev))

;; TODO - use some destructuring here
(defn- ev->db-col [ev]
  (let [ev-map (log-ev->map ev)]
    {:when   (Date. ^long (:timeStamp ev-map))
     :logger (:loggerName ev-map)
     :level  (str (:level ev-map))
     :msg    (:message ev-map)
     :thread (:threadName ev-map)
     :ndc    (:NDC ev-map)}))

;;
;; TODO - this is ultimately using a pooled DB connection
;; but never returning the connection to the pool, fix this.
;;
(defn mk-db-appender
  "Create a log4j database Appender"
  [db-spec]
  (let [spec db-spec]
    (proxy [AppenderSkeleton] []
      (append [ev]
        (try
          (jdbc/insert! spec :finkratzen.log (ev->db-col ev))
          (catch Exception e (prn "Exception: " (.getMessage e)))))

      (close []
        nil))))

(defn- init-logging [db-spec]

  (comment
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