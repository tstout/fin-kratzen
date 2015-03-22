(ns kratzen.dates
  (:import (java.sql Date)
           (org.joda.time LocalDate))
  (:require
    [clj-time.core :as t]))

(defn sql-date
  ([year month day]
    "crete a java.sql.Date from year month day"
    (-> (t/date-time year month day)
        (.getMillis)
        (Date.)))

  ([date]
    "create a java.sql.Date from a joda LocalDate"
    (Date.
      (-> date
          (.toDateTimeAtStartOfDay)
          (.getMillis)))))

(defn days-before-now [offset]
  "Create a date some number of days in the past"
  (sql-date (t/minus (t/today) (t/days offset))))

(defn days-from [year month day]
  (let [now (t/now)
        date (t/date-time year month day)]
    (t/in-days
      (if (t/before? date now)
        (t/interval date now)
        (t/interval now date)))))

(defn mk-local-date [sql-date]
  (LocalDate/fromDateFields sql-date))

;; TODO - interval is probably not the most descriptive name...
(defn interval
  ([day-offset]
    {:pre [(number? day-offset)]}
    "Create an interval map with a :start and :end defining
    a date range starting offset days in the past"
    {:start (-> (days-before-now (inc day-offset)))
     :end   (-> (days-before-now day-offset))})
  ([start end]
    {:start start :end end}))



