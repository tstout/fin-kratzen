(ns kratzen.boa
  (:import (ofx.client BoaData Retriever Credentials$Builder)
           (net.sf.ofx4j.client AccountStatement))
  (:require [kratzen.config :refer :all]
            [kratzen.db :refer :all]
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

(defn download-boa-stmts []
  "Grab BOA statements via ofx-io"
  (let [start (days-from-now 2)
        end (days-from-now 1)]
    (info "Downloading statements for" start end)
    (-> (Retriever. (BoaData.) BoaData/CONTEXT creds)
        (.fetch start end))))

(defn get-stmts []
  (let [stmts (download-boa-stmts)
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

(defn download-and-save-stmts []
  (save-boa (h2-local-server-conn) (extract-stmt-fields (get-stmts))))
