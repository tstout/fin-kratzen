;;
;; Loaded when running the REPL or running tests.
;; Define all your project dev conveniences here.
;;
(ns user
  (:require [kratzen.system :as system]
            [kratzen.scheduler :refer [task]]
            [kratzen.reports :refer [boa-stmts-week
                                     mk-weekly-summary
                                     boa-recent-stmts
                                     week-credits
                                     max-amount
                                     week-debits]]
            [kratzen.boa :refer [balance
                                 ofx-fetch]]
            [kratzen.config :as cfg]
            [kratzen.dates :refer [every-day-at
                                   every-x-minutes
                                   days-before-now
                                   days-ago]]
            [kratzen.email :refer [daily-summary-template
                                   send-daily-summary
                                   mk-summary-email]]
            [kratzen.db :refer [pool-db-spec
                                run-query
                                h2-local
                                next-seq-val
                                reset-seq]]
            [kratzen.backup :refer [mk-backup
                                    rm-backup-meta
                                    local-backup-file
                                    trim-logs
                                    upload-backup
                                    legacy-backup-files
                                    rm-old-backups
                                    ]]
            [clojure.tools.namespace.repl :refer [refresh refresh-all]]
            [clojure.java.jdbc :as jdbc]
            [gd-io.protocols :refer [upload]]
            [clj-time.core :as t]
            [clj-time.format :as tf]
            [clojure.pprint :refer [pprint print-table]]
            [kratzen.http :as http]
            [kratzen.dates :refer [interval]]
            [kratzen.boa-ofx :as ofx]
            [kratzen.boa :as boa]
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