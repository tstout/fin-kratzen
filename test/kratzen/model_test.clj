(ns kratzen.model-test
  (:import (java.util HashMap Calendar)
           (java.sql Timestamp)
           (db.io.operations Query Queries Updates))
  (:use [expectations])
  (:require [user :refer :all]
            [clj-time.core :as t]
            [kratzen.config :refer :all]
            [kratzen.model :refer :all]
            [kratzen.db :refer :all]
            [kratzen.dates :refer :all]
            [clojure.java.jdbc :as jdbc]))

(def posting-date (sql-date 2014 12 12))

(def test-data
  [["483882" posting-date 1.00M "some tran 1"]
   ["483883" posting-date 2.00M "some tran 2"]
   ["483884" posting-date 3.00M "some tran 3"]
   ["483885" posting-date 4.00M "some tran 4"]])

(defn setup
  {:expectations-options :before-run}
  []
  (load-db test-data))

(defn teardown
  {:expectations-options :after-run}
  []
  (-> (h2-mem-conn)
      (Updates/newUpdate "delete from finkratzen.boa_checking" (object-array []))
      (.run)))
;;
;; Validate basic to-clj-map operation...
;;
(expect-let
  [jmap (HashMap. {"a" "value-for-a"})]
  (to-clj-map jmap)
  {:a "value-for-a"})

;;
;; Verify we can fetch BOA data from DB...
;;
(expect-let
  [transactions (fetch-boa h2-mem posting-date posting-date)]
  4
  (.size transactions))

;;
;; Verify that the sum of the amounts is 10...
;;
(expect-let
  [transactions (fetch-boa h2-mem posting-date posting-date)]
  10M
  (reduce + (map #(:amount %) transactions)))
