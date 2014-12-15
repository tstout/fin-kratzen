(ns kratzen.model
  (:import (java.sql Date Timestamp)
           (db.io.operations Queries Updates))
  (:require [kratzen.config :refer :all]
            [kratzen.dates :refer :all]
            [clojure.walk :refer :all]
            [kratzen.db :refer :all]
            [clojure.java.jdbc :as jdbc])
  (:use [clojure.tools.logging :only (info error)]))

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

;(defn fetch-boa [conn start end]
;  (-> (Queries/newQuery conn)
;      (.run CheckingEntry (:select-boa sql) (object-array [start end]))))

(defn fetch-boa
  ([db start end]
   (jdbc/query db [(:select-boa sql) start end]))
  ([db offset]
   (let [interv (interval offset)]
     (fetch-boa db (:start interv) (:end interv)))))

(defn select-boa []
  (jdbc/query h2-local ["select top(4) * from finkratzen.boa_checking"]))

;(defn save-boa [conn records]
;  "Assumes records is a seq of vectors, where
;  each vector contains the SQL args"
;  (info "Processing " (count records) "records")
;  (doseq [record records]
;    ;; TODO - use builder to reuse connection for each insert...
;    (-> (Updates/newUpdate conn (:insert-boa sql) (object-array record))
;        (.run))))

(defn save-boa [conn records]
  "Assumes records is a seq of vectors, where
  each vector contains the SQL args"
  (doseq [record records]
    (println (str "Processing row..." (nth record 0) ":" (nth record 1)))
    ;; TODO - this is ugly, see if insert! can support arbitrary unnamed args
    (jdbc/execute! conn [(:insert-boa sql)
                         (nth record 0)
                         (nth record 1)
                         (nth record 2)
                         (nth record 3)])))

;;
(defn to-clj-map
  "convert java.util.Map with String keys into to a clojure map
  with keywords as the keys"
  [jmap]
  (keywordize-keys (into {} jmap)))