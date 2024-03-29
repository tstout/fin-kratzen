(ns kratzen.classifier
  (:require [judgr.settings :refer [settings update-settings]]
            [judgr.core :refer [classifier-from]]
            [clojure.java.jdbc :as jdbc]
            [kratzen.config :refer [load-res]]
            [clojure.tools.logging :as log])
  (:import (com.stuartsierra.component Lifecycle)))

(def sql
  {:load-training                (load-res "sql/select-class-training.sql")
   :select-boa-unclassified      (load-res "sql/select-boa-unclassified.sql")
   :insert-boa-checking-category (load-res "sql/insert-boa-checking-category.sql")})

(defn categories [db-spec]
  (jdbc/with-db-connection
    [conn db-spec]
    (jdbc/query conn ["select * from FINKRATZEN.CATEGORY"])))

(defn add-category
  "Add a classification training record to the classifier"
  [classifier category train-data]
  {:pre [(keyword? category)
         (string? train-data)
         (not (nil? classifier))]}
  (.train! classifier train-data category))

(defn load-categories
  "Configure the classification categories.
  Judgr suppports per-class probability thresholds.
  For the moment, these are disabled."
  [db-spec]

  (update-settings
   settings
   [:classes]
   (mapv #(keyword (:name %)) (categories db-spec))
   [:classifier :default :threshold?] false))

(defn unclassified-stmts [db-spec]
  (jdbc/with-db-connection
    [conn db-spec]
    (jdbc/query conn [(:select-boa-unclassified sql)])))

(defn mk-category-record [stmt class]
  {:pre [(keyword? class)]}

  {:bank_id      (:bank_id stmt)
   :posting_date (:posting_date stmt)
   :category     (name class)})

(defn classify-stmts [db-spec classifier]
  (jdbc/with-db-connection
    [conn db-spec]
    (doseq [stmt (unclassified-stmts db-spec)]
      (let [class (.classify classifier (:description stmt))]
        (jdbc/insert!
         conn :finkratzen.boa_checking_category (mk-category-record stmt class))))))

;; TODO - see about cleaning this up with destructuring...
;;
(defn load-training-data [classifier db-spec]
  (jdbc/with-db-connection
    [conn db-spec]
    (doseq [record (jdbc/query conn [(:load-training sql)])]
      (add-category classifier (keyword (:category record)) (:data record)))))

(defn mk-classifier [db-spec]
  (classifier-from (load-categories db-spec)))

(defn prime-classifier [db-spec]
  (let [classifier (mk-classifier db-spec)]
    (load-training-data classifier db-spec)
    ;;(classify-stmts db-spec classifier)
    classifier))

(defn update-classifications [db-spec]
  (->>
   (prime-classifier db-spec)
   (classify-stmts db-spec)))

(defrecord BayesClassifier [db-spec]
  Lifecycle

  (start [component]
    (let [classifier (prime-classifier db-spec)]
      (classify-stmts db-spec classifier)
      (assoc component :classifier classifier)))

  (stop [component]
    (assoc component :classifier nil)))