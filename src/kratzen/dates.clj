(ns kratzen.dates
  (:import (java.sql Date)
           (org.joda.time LocalDate DateTimeConstants DateTimeZone))
  (:require
    [clj-time.core :as t]
    [clj-time.format :as tf]
    [clj-time.periodic :refer [periodic-seq]]))

(defn tm-format [time]
  (tf/unparse
    (tf/formatters :mysql)
    time))

(defn days-ago [offset]
  (-> offset t/days t/ago LocalDate.))

(defn every-day-at
  "Create a periodic sequence corresponding to every day
  at the specified hour"
  [hour]
  (periodic-seq (.. (t/now)
                    (withZone (DateTimeZone/forID "America/Chicago"))
                    (withTime hour 0 0 0))
                (-> 1 t/days)))

(defn sql-date
  "crete a java.sql.Date from year month day"
  ([year month day]
   (-> (t/date-time year month day)
       (.getMillis)
       (Date.)))

  ;;
  ;;create a java.sql.Date from a joda LocalDate
  ;;
  ([date]
   (Date.
     (-> date
         (.toDateTimeAtStartOfDay)
         (.getMillis)))))

(defn days-before-now
  "Create a date some number of days in the past"
  [offset]
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
  "Create an interval map with a :start and :end defining
  a date range starting offset days in the past"
  ([day-offset]
   {:pre [(number? day-offset)]}
   {:start (days-before-now (inc day-offset))
    :end   (days-before-now day-offset)})
  ([start end]
   {:start start :end end}))



