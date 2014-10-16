(ns kratzen.boa
  (:import (ofx.client BoaData Retriever Credentials$Builder)
           (net.sf.ofx4j.client AccountStatement))
  (:require [kratzen.config :refer :all]
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
  (t/minus (t/now) (t/days offset)))

(defn download-boa-stmts []
  (info "download-boa-stmts")
  (let [start (days-from-now 2)
        end (days-from-now 1)]
    (info "Downloading statements for" start end)
    (-> (Retriever. (BoaData.) BoaData/CONTEXT creds)
        (.fetch start end))))

(defn save-boa-smts []
  )