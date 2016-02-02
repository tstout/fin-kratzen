(ns kratzen.reports
  (:require [kratzen.config :refer [load-res]]
            [clojure.java.jdbc :as jdbc]
            [kratzen.db :refer [h2-local pool-db-spec]]))

(def sql
  {:week-stmts (load-res "select-boa-last-7-days.sql")})

(defn boa-stmts-week []
  (jdbc/with-db-connection
    [conn (pool-db-spec h2-local)]
    (jdbc/query conn [(:week-stmts sql)])))

(defn week-credits [stmts]
  (filter #(< 0 (:amount %)) stmts))

(defn week-debits [stmts]
  (filter #(> 0 (:amount %)) stmts))

(defn max-amount [stmts]
  (last (sort-by :amout stmts)))

(defn sum [stmts]
  (reduce + (map :amount stmts)))

(defn mk-weekly-summary []
  (let [stmts (boa-stmts-week)
        credits (week-credits stmts)
        debits (week-debits stmts)]
    {:credits (sum credits)
     :debits (sum debits)
     :max-credit (max-amount credits)
     :max-debit (max-amount debits)}))