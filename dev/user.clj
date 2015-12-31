;;
;; Loaded when running the REPL or running tests.
;; Define all your project dev conveniences here.
;;
(ns user
  (:require [kratzen.config :refer :all]
            [kratzen.dates :refer :all]
            [kratzen.model :refer :all]
            [kratzen.boa :refer :all]
            [kratzen.db :refer :all]
            [kratzen.server :refer [system]]
            [kratzen.backup :refer [mk-backup backup-file]]
            [kratzen.classifier :refer :all]
            [clojure.java.jdbc :as jdbc]
            [gd-io.protocols :refer [upload]]
            [clj-time.core :as t]
            [clojure.pprint :refer [pprint]]
            [kratzen.http :as http]))

(println "-- loading custom settings from user.clj --")

(def db (pool-db-spec h2-local))

(def system nil)

(defn init
  "Constructs the current development system."
  []
  (alter-var-root #'system
                  (constantly (system/system))))

(defn start
  "Starts the current development system."
  []
  (alter-var-root #'system system/start))

(defn stop
  "Shuts down and destroys the current development system."
  []
  (alter-var-root #'system
                  (fn [s] (when s (system/stop s)))))

(defn go
  "Initializes the current development system and starts it running."
  []
  (init)
  (start))


