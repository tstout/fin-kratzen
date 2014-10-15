(ns kratzen.model
  (:import (java.sql Date Timestamp)
           (db.io.operations Queries Updates))
  (:require [kratzen.config :refer :all]
            [clojure.walk :refer :all]
            [kratzen.db :refer :all])
  (:use [clojure.tools.logging :only (info error)]))

;;
;;BANK_ID varchar(100) not null,
;;POSTING_DATE date not null,
;;AMOUNT decimal(19,4),
;;RECORD_CREATED datetime default current_timestamp(),
;;

;;
;; Define interface(s) needed by db-io...
;; Might be easier just to define this with java...
;;
(definterface CheckingEntry
              [^String bankId []]
              [^java.sql.Date postingDate []]
              [^java.math.BigDecimal amount []]
              [^java.sql.Timestamp recordCreated []])

(def ^:private sql
  {:select-boa (load-res "select-boa.sql")
   :insert-boa (load-res "insert-boa.sql")})

(defn fetch-boa [conn start end]
  (-> (Queries/newQuery conn)
      (.run CheckingEntry (:select-boa sql) (object-array [start end]))))

(defn save-boa [conn records]
  "Assumes records is a seq of vectors, where
  each vector contains the SQL args"
  (doseq [record records]
    ;; TODO - use builder to reuse connection for each insert...
    (-> (Updates/newUpdate conn (:insert-boa sql) (object-array record))
        (.run))))

;;
(defn to-clj-map
  "convert java.util.Map with String keys into to a clojure map
  with keywords as the keys"
  [jmap]
  (keywordize-keys (into {} jmap)))