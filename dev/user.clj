;;
;; Loaded when running the REPL or running tests.
;; Define all your project dev conveniences here.
;;
(ns user
  (:require [kratzen.system :as system]
            [kratzen.reports :refer [boa-stmts-week
                                     mk-weekly-summary
                                     week-credits
                                     max-amount
                                     week-debits]]
            [kratzen.boa :refer [creds
                                 download-boa-stmts
                                 balance
                                 ofx-fetch]]
            [kratzen.config :refer [load-config]]
            [kratzen.dates :refer [every-day-at
                                   days-before-now
                                   days-ago]]
            [kratzen.email :refer [daily-summary-template
                                   send-daily-summary
                                   mk-summary-email]]
            [kratzen.db :refer [pool-db-spec
                                h2-local
                                next-seq-val
                                reset-seq]]
            [kratzen.backup :refer [mk-backup local-backup-file trim-logs
                                    legacy-backup-files
                                    rm-old-backups]]
            [clojure.tools.namespace.repl :refer [refresh refresh-all]]
            [clojure.java.jdbc :as jdbc]
            [gd-io.protocols :refer [upload]]
            [clj-time.core :as t]
            [clojure.pprint :refer [pprint]]
            [kratzen.http :as http]
            [kratzen.dates :refer [interval]]
            [kratzen.email :refer [send-email]]))

(println "-- loading custom settings from user.clj --")

(def db (pool-db-spec h2-local))

(def system nil)

(defn init
  "Constructs the dev system."
  []
  (alter-var-root #'system
                  (constantly (system/system))))

(defn start
  "Starts the dev system."
  []
  (alter-var-root #'system system/start))

(defn stop
  "Shuts down and destroys dev system."
  []
  (alter-var-root #'system
                  (fn [s] (when s (system/stop s)))))

(defn go
  "Initializes the current dev system and starts it."
  []
  (init)
  (start))

(defn reset []
  (stop)
  (refresh :after 'user/go))