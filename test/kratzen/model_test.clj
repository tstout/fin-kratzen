(ns kratzen.model-test
  (:import (java.util HashMap Calendar)
           (java.sql Timestamp)
           (db.io.operations Query Queries Updates))
  (:use [expectations])
  (:require [clj-time.core :as t]
            [kratzen.model :refer :all]
            [kratzen.db :refer :all]))

(def now (-> (t/now) .toDate))

(def test-data
  [["483882" now 1.00M "some tran 1"]
   ["483883" now 2.00M "some tran 2"]
   ["483884" now 3.00M "some tran 3"]
   ["483885" now 4.00M "some tran 4"]])

(defn setup
  {:expectations-options :before-run}
  []
  "Create schema and poke test data into a test DB..."
  (-> (h2-mem-conn)
      (mk-migrator)
      (.update "/sql/init-schema.sql"))
  (save-boa (h2-mem-conn) test-data))

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
  [transactions (fetch-boa (h2-mem-conn) now now)]
  (.size transactions)
  4)

;;
;; Verify that the sum of the amounts is 10...
;;
(expect-let
  [transactions (fetch-boa (h2-mem-conn) now now)]
  (reduce + (map #(.amount %) transactions))
  10M)
