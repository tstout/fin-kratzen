(ns kratzen.boa-test
  (:require [kratzen.boa :refer :all]
            [kratzen.db :refer [pool-db-spec h2-mem]]
            [user :refer :all]
            [kratzen.dates :refer :all]
            [clj-time.core :as t]
            [clojure.set :refer :all]
            [kratzen.db-util :refer [load-db]]
            [expectations :refer [expect]]))

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

(def mixed-data
  [{:description  "Check"
    :amount       -15M
    :posting_date second-posting-date
    :bank_id      "00090258902"}

   {:description  "7 Eleven"
    :amount       -10.0000M
    :posting_date second-posting-date
    :bank_id      "89592480293"}

   {:description  "MARKET STREET   10/17 #000603680"
    :amount       -21.0900M
    :posting_date first-posting-date
    :bank_id      "00095061017"}])

(defn setup
  {:expectations-options :before-run}
  []
  (load-db existing-data))

;;
;; Verify existing-stmts returns the right number
;; of records
;;
(expect
  (count existing-data)
  (count (existing-stmts (pool-db-spec h2-mem) search-intv)))

;;
;; Verify new statements identified correctly
;;
(expect
  #{["00090258902" second-posting-date] ["89592480293" second-posting-date]}
  (new-stmt-keys new-data existing-data))

;;
;; Check if all new statements are extracted
;;
(expect
  (count new-data)
  (count (extract-new-stmts (new-stmt-keys new-data existing-data) new-data)))

;;
;; Check that no new statements detected permaturely
;;
(expect
  0
  (count (new-stmt-keys existing-data existing-data)))

(expect
  2
  (count (difference (stmt-keys mixed-data) (stmt-keys existing-data))))

(expect
  2
  (count (new-stmt-keys mixed-data existing-data)))

(expect
  2
  (count
    (extract-new-stmts
      (new-stmt-keys mixed-data existing-data) mixed-data)))

(expect
  (count existing-data)
  (count (existing-stmts (pool-db-spec h2-mem) search-intv)))

;(expect
;  new-data
;  (with-redefs
;    (boa/download-boa-stmts (fn [_] new-data))
;    (boa/download-boa-stmts 1)))
