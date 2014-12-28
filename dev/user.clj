;;
;; Loaded when running the REPL.
;; Define all your project dev conveniences here.
;;
(ns user
  (:require [kratzen.config :refer :all]
            [kratzen.dates :refer :all]
            [kratzen.model :refer :all]
            [kratzen.boa :refer :all]
            [kratzen.db :refer :all]
            [clojure.java.jdbc :as jdbc]
            [clojure.pprint :as pprint]))

(println "-- loading custom REPL settings from user.clj --")

(defn load-db [test-data]
  (-> (h2-mem-conn)
      (mk-migrator)
      (.update "/sql/init-schema.sql"))
  (save-boa h2-mem test-data))


