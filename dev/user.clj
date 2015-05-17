;;
;; Loaded when running the REPL or running tests.
;; Define all your project dev conveniences here.
;;
;(ns user
;  (:require [kratzen.config :refer :all]
;            [kratzen.dates :refer :all]
;            [kratzen.model :refer :all]
;            [kratzen.boa :refer :all]
;            [kratzen.db :refer :all]
;            [kratzen.classifier :refer :all]
;            [clojure.java.jdbc :as jdbc]
;            [clj-time.core :as t]
;            [clojure.pprint :as pprint]
;            [kratzen.http :as http]))

;(println "-- loading custom settings from user.clj --")
;
;(defn load-db [test-data]
;  (-> (mk-migrator h2-mem)
;      (.update "/sql/init-schema.sql"))
;  (save-boa h2-mem test-data))


