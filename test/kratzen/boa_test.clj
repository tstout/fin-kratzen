(ns kratzen.boa-test
  (:use [expectations])
  (:require [kratzen.boa :refer :all]
            [kratzen.db :as db]
            [user :refer :all]
            [kratzen.dates :refer :all]
            [clj-time.core :as t]))

(def first-posting-date (sql-date 2014 10 17))
(def second-posting-date (sql-date 2014 10 18))
(def search-intv (interval first-posting-date second-posting-date))

(def existing-data
  [{:description  "CITY OF COPPELL  DES:Water Bill"
    :amount       -66.4700M
    :posting_date first-posting-date
    :bank_id      "00090258901"}

   {:description  "Check"
    :amount       -10.0000M
    :posting_date first-posting-date
    :bank_id      "89592480293"}

   {:description  "Check"
    :amount       -30.0000M
    :posting_date first-posting-date
    :bank_id      "89492541895"}

   {:description  "MARKET STREET   10/17 #000603680"
    :amount       -21.0900M
    :posting_date first-posting-date
    :bank_id      "00095061017"}])

(def new-data
  [{:description  "Check"
    :amount       -15M
    :posting_date second-posting-date
    :bank_id      "00090258902"}

   {:description  "7 Eleven"
    :amount       -10.0000M
    :posting_date second-posting-date
    :bank_id      "89592480293"}])

(defn setup
  {:expectations-options :before-run}
  []
  (load-db existing-data))

;;
;; verify existing-stmts returns the right number
;; of records
;;
(expect
  (count existing-data)
  (count (existing-stmts db/h2-mem search-intv)))

(expect
  #{["00090258902" second-posting-date] ["89592480293" second-posting-date]}
  (new-stmt-keys new-data existing-data))

(expect
  (count new-data)
  (count (new-stmt-keys new-data existing-data)))

(expect
  (count new-data)
  (count (extract-new-stmts (new-stmt-keys new-data existing-data) new-data)))



;(expect
;  new-data
;  (with-redefs
;    (boa/download-boa-stmts (fn [_] new-data))
;    (boa/download-boa-stmts 1)))
