(ns kratzen.boa
  (:import (ofx.client BoaData Retriever Credentials$Builder)
           (net.sf.ofx4j.client AccountStatement))
  (:require [kratzen.config :refer :all]
            [kratzen.db :refer :all]
            [clojure.set :refer :all]
            [kratzen.model :refer :all]
            [clj-time.core :as t])
  (:use [clojure.tools.logging :only (info error)]))

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

(defn days-from-now [offset]
  "Create a date some number of days in the past"
  (t/minus (t/now) (t/days offset)))

(defn interval [offset]
  {:start (-> (days-from-now (inc offset))
              (.toDate))
   :end   (-> (days-from-now offset)
              (.toDate))})

(defn download-boa-stmts [day-offset]
  "Grab BOA statements via ofx-io"
  (let [start (days-from-now (inc day-offset))
        end (days-from-now day-offset)]
    (info "Downloading statements for" start end)
    (-> (Retriever. (BoaData.) BoaData/CONTEXT creds)
        (.installCustomTrustStore)
        (.fetch start end))))

(defn stmt-keys [stmts]
  (map
    #(hash-map :bank-id (.bankId %) :posting-date (.postingDate %))
    stmts))

(defn new-stmts [ofx-stmts db-stmts]
  "determine stmts that do not already exist in the db"
  (difference (stmt-keys ofx-stmts) db-stmts))

(defn existing-stmts [interval]
  "fetch existing stmts form the db and convert to a clojure map containing the statement keys"
  (info "Checking local DB for statements in" (:start interval) (:end interval))
  (stmt-keys
    (fetch-boa
      (h2-local-server-conn) (:start interval) (:end interval))))

(defn get-stmts [day-offset]
  (let [stmts (download-boa-stmts day-offset)
        trans (.getTransactionList stmts)]
    (info "transaction list is "
          (if
            (nil? trans)
            "nil"
            (.. trans getTransactions size)))
    (when-not (nil? trans)
      (.getTransactions trans))))

(defn extract-stmt-fields [transactions]
  (map
    #(vector (.getId %) (.getDatePosted %) (.getAmount %) (.getName %))
    transactions))

(defn download-and-save-stmts [day-offset]
  (let [interval (interval day-offset)
        old-stmts (existing-stmts interval)
        ofx-stmts (get-stmts day-offset)]
    (save-boa (h2-local-server-conn) (extract-stmt-fields (get-stmts day-offset)))))
