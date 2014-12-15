(ns kratzen.dates
  (:import (java.sql Date))
  (:require
    [clj-time.core :as t]))

(defn days-from-now [offset]
  "Create a date some number of days in the past"
  (t/minus (t/now) (t/days offset)))

;; TODO - interval is probably not the most descriptive name...
(defn interval [offset]
  "Create an interval map with a :start and :end defining
  a date range starting offset days in the past"
  {:start (-> (days-from-now (inc offset))
              (.toDate))
   :end   (-> (days-from-now offset)
              (.toDate))})

(defn sql-date [year month day]
  "crete a java.sql.Date"
  (-> (t/date-time year month day)
      (.getMillis)
      (Date.)))

