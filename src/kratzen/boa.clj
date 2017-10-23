(ns kratzen.boa
  (:import (ofx.client BoaData Retriever Credentials$Builder)
           (org.joda.time LocalDate))
  (:require [kratzen.config :refer :all]
            [clj-time.core :as t]
            [kratzen.dates :refer [sql-date
                                   days-before-now
                                   mk-local-date
                                   interval]]
            [kratzen.db :refer [pool-db-spec h2-local]]
            [kratzen.boa-ofx :refer [query-boa]]
            [clojure.set :refer :all]
            [kratzen.model :refer :all]
            [kratzen.scheduler :refer [periodic-task]]
            [clojure.core.async :refer [close!]]
            [kratzen.classifier :refer [update-classifications]]
            [com.stuartsierra.component :as component]
            [clojure.tools.logging :as log]))

(defn balance[]
  (get-in (query-boa 3) [:balance :amount]))

(defn ofx-to-map [resp]
  (let [{:keys [balance transactions]} resp]
  (map
    #(hash-map
      :bank_id (% :id)
      :posting_date (sql-date (LocalDate. (% :datePosted)))
      :amount (% :amount)
      :description (% :name))
    transactions)))

(defn ofx-fetch
  "Call ofx-io to download statements"
  [day-offset]
  (log/info "Downloading statements...")
  (->
    day-offset
    query-boa
    ofx-to-map))

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
        ofx-stmts (ofx-fetch day-offset)]

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
    (log/info "starting BOA Download periodic task...")
    (assoc this :boa-download
                (periodic-task
                  interval-in-s
                  (fn [_] (download-and-save-stmts (pool-db-spec h2-local) 2)))))

  (stop [this]
    (log/info "Stopping BOA download")
    (when-let [boa-ch (:boa-download this)]
      (close! boa-ch))
    (assoc this :boa-download nil)))

(defn boa-download [interval-in-s]
  (map->BoaDownload {:interval-in-s interval-in-s}))