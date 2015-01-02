(ns kratzen.boa
  (:import (ofx.client BoaData Retriever Credentials$Builder)
           (org.joda.time LocalDate))
  (:require [kratzen.config :refer :all]
            [kratzen.dates :refer :all]
            [kratzen.db :refer :all]
            [clojure.set :refer :all]
            [kratzen.model :refer :all]
            [clj-time.core :as t])
  (:use [clojure.tools.logging :as log]))

;;
;; Load BOA credentials from cfg file...
;;
(def ^:private creds
  (let [cfg (:boa (load-config))]
    (-> (Credentials$Builder.)
        (.withUser (:user cfg))
        (.withPass (:pass cfg))
        (.withRouting (:routing cfg))
        (.withAccount (:account cfg))
        (.build))))

(defn ofx-to-map [transactions]
  (map
    #(hash-map
      :bank_id (.getId %)
      :posting_date (sql-date (LocalDate. (.getDatePosted %)))
      :amount (.getAmount %)
      :description (.getName %))
    transactions))

(defn ofx-fetch [start end]
  (-> (Retriever. (BoaData.) BoaData/CONTEXT creds)
      (.installCustomTrustStore)
      (.fetch start end)
      (.getTransactionList)
      (.getTransactions)
      (ofx-to-map)))

(defn download-boa-stmts [day-offset]
  "Grab BOA statements via ofx-io"
  (let [start (days-before-now (inc day-offset))
        end (days-before-now day-offset)]
    (log/info "Downloading statements for" start end)
    (ofx-fetch (mk-local-date start) (mk-local-date end))))

(defn get-key [stmt]
  (vector (:bank_id stmt) (:posting_date stmt)))

(defn stmt-keys [stmts]
  "Create a set containing only the primary keys of the
  statement collection"
  (into #{}
        (map #(get-key %) stmts)))

(defn new-stmt-keys [ofx-stmts db-stmts]
  "determine stmts that do not already exist in the db"
    (difference (stmt-keys ofx-stmts) (stmt-keys db-stmts)))

(defn extract-new-stmts [new-keys stmts]
  (filter
    #(contains? new-keys (get-key %))
    stmts))

(defn existing-stmts [db interval]
  "fetch existing stmts from the db and convert to a set
  containing the statement keys"
  (log/info
    "Checking local DB for statements in"
    (:start interval) (:end interval))
    (fetch-boa
      db
      (:start interval)
      (:end interval)))

(defn download-and-save-stmts [db day-offset]
  (let [interval (interval day-offset)
        old-stmts (existing-stmts db interval)
        ofx-stmts (download-boa-stmts day-offset)]
    (log/info "Transaction count:" (count ofx-stmts)
              "DB Stmt count:" (count old-stmts))
    (save-boa
      db
      (let [new-keys (new-stmt-keys ofx-stmts old-stmts)]
        (doseq [key new-keys]
          (log/info "new-key:" key))
        (extract-new-stmts new-keys ofx-stmts)))))

