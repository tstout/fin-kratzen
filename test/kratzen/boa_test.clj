(ns kratzen.boa-test
  (:use [expectations])
  (:require [kratzen.boa :refer :all]
            [user :refer :all]
            [kratzen.dates :refer :all]
            [clj-time.core :as t]))

(def ^:private first-posting-date (sql-date 2014 10 17))
(def ^:private second-posting-date (sql-date 2014 10 18))

(def existing-data
  [{:record_created first-posting-date
    :description    "CITY OF COPPELL  DES:Water Bill"
    :amount         -66.4700M
    :posting_date   first-posting-date
    :bank_id        "00090258901-66.47014101710602.95"}

   {:record_created first-posting-date
    :description    "Check"
    :amount         -10.0000M
    :posting_date   first-posting-date
    :bank_id        "89592480293-10.000141017257210669.42"}

   {:record_created first-posting-date
    :description    "Check"
    :amount         -30.0000M
    :posting_date   first-posting-date
    :bank_id        "89492541895-30.000141017249910679.42"}

   {:record_created first-posting-date
    :description    "MARKET STREET   10/17 #000603680"
    :amount         -21.0900M
    :posting_date   first-posting-date
    :bank_id        "00095061017-21.09014101710709.42"}])

(def new-data
  [{:record_created second-posting-date
    :description    "Check"
    :amount         -15M
    :posting_date   second-posting-date
    :bank_id        "00090258902-66.47014101710602.95"}

   {:record_created second-posting-date
    :description    "7 Eleven"
    :amount         -10.0000M
    :posting_date   second-posting-date
    :bank_id        "89592480293-10.000141017257210669.42"}])

(defn setup
  {:expectations-options :before-run}
  []
  (load-db existing-data))

(expect 1 1)