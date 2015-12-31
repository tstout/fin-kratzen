(ns kratzen.boa
  (:import (ofx.client BoaData Retriever Credentials$Builder)
           (org.joda.time LocalDate))
  (:require [kratzen.config :refer :all]
            [kratzen.dates :refer :all]
            [kratzen.db :refer [pool-db-spec h2-local]]
            [clojure.set :refer :all]
            [kratzen.model :refer :all]
            [kratzen.scheduler :refer [periodic-task]]
            [clojure.core.async :refer [close!]]
            [kratzen.classifier :refer [update-classifications]]
            [com.stuartsierra.component :as component]
            [clojure.tools.logging :as log]))

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

(defn ofx-fetch
  "Call ofx-io to download statements"
  [start end]
  (let [tran-list
        (-> (Retriever. (BoaData.) BoaData/CONTEXT creds)
            (.installCustomTrustStore)
            (.fetch start end)
            (.getTransactionList))
        trans (if (nil? tran-list)
                {}
                (.getTransactions tran-list))]
    (ofx-to-map trans)))

(defn download-boa-stmts
  "Grab BOA statements via ofx-io"
  [day-offset]
  (let [start (days-before-now (inc day-offset))
        end (days-before-now day-offset)]
    (log/info "Downloading statements for" start end)
    (ofx-fetch (mk-local-date start) (mk-local-date end))))

(defn get-key [stmt]
  (vector (:bank_id stmt) (:posting_date stmt)))

(defn stmt-keys
  "Create a set containing only the primary keys of the
  statement collection"
  [stmts]
  (into #{}
        (map #(get-key %) stmts)))

(defn new-stmt-keys
  "determine stmts that do not already exist in the db"
  [ofx-stmts db-stmts]
  (difference (stmt-keys ofx-stmts) (stmt-keys db-stmts)))

(defn extract-new-stmts [new-keys stmts]
  (filter
    #(contains? new-keys (get-key %))
    stmts))

(defn existing-stmts
  "fetch existing stmts from the db and convert to a set
  containing the statement keys"
  [db interval]
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
        (extract-new-stmts new-keys ofx-stmts)))

    (update-classifications (pool-db-spec h2-local))))

(defrecord BoaDownload [scheduler interval-in-s]
  component/Lifecycle

  (start [this]
    (assoc this :boa-download
                (periodic-task
                  interval-in-s
                  (fn [_] (download-and-save-stmts (pool-db-spec h2-local) 2)))))

  (stop [this]
    (close! (:boa-download this))))

(defn boa-download [interval-in-s]
  (map->BoaDownload {:interval-in-s interval-in-s}))