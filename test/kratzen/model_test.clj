(ns kratzen.model_test
  (:import (java.util HashMap Calendar)
           (db.io.operations Updates Queries))
  (:import (java.util HashMap Calendar)
           (java.sql Timestamp)
           (db.io.operations Query Queries Updates))
  (:require
    [expectations :refer [expect-let]]
    [user :refer :all]
    [clj-time.core :as t]
    [kratzen.config :refer :all]
    [kratzen.model :refer :all]
    [kratzen.db :refer [pool-db-spec h2-mem]]
    [kratzen.dates :refer :all]
    [clojure.java.jdbc :as jdbc]
    [kratzen.db-util :refer [load-db]]))

(def posting-date (sql-date 2014 12 12))

(def test-data
  [{:bank_id "483882" :posting_date posting-date :amount 1.00M :description "some tran 1"}
   {:bank_id "483883" :posting_date posting-date :amount 2.00M :description "some tran 2"}
   {:bank_id "483884" :posting_date posting-date :amount 3.00M :description "some tran 3"}
   {:bank_id "483885" :posting_date posting-date :amount 4.00M :description "some tran 4"}])

(defn setup
  {:expectations-options :before-run}
  []
  (load-db test-data))

(defn teardown
  {:expectations-options :after-run}
  []
  (comment (-> (h2-mem-conn)
               (Updates/newUpdate "delete from finkratzen.boa_checking" (object-array []))
               (.run))))
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
  [transactions (fetch-boa (pool-db-spec h2-mem) posting-date posting-date)]
  4
  (.size transactions))

;;
;; Verify that the sum of the amounts is 10...
;;
(expect-let
  [transactions (fetch-boa (pool-db-spec h2-mem) posting-date posting-date)]
  10M
  (reduce + (map #(:amount %) transactions)))
