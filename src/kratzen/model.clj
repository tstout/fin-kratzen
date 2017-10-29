(ns kratzen.model
  (:require [kratzen.config :refer :all]
            [kratzen.dates :refer :all]
            [clojure.walk :refer :all]
            [clojure.stacktrace :as st]
            [kratzen.db :refer :all]
            [clojure.java.jdbc :as jdbc]
            [clojure.tools.logging :as log]))

(def ^:private sql
  {:select-boa (load-res "select-boa.sql")
   :insert-boa (load-res "insert-boa.sql")})

(defn fetch-boa
  ([db start end]
   {:pre [(:datasource db)]}
   (jdbc/with-db-connection
     [conn db]
     (jdbc/query conn [(:select-boa sql) start end])))

  ([db offset]
   (let [interv (interval offset)]
     (fetch-boa db (:start interv) (:end interv)))))

(defn select-boa []
  (jdbc/query pool-db-spec ["select top(4) * from finkratzen.boa_checking"]))

(defn save-boa
  "save to boa table..."
  [db records]
  (log/info "saving" (count records) "BOA records")
  (jdbc/with-db-connection
    [conn db]
    (doseq [record records]
      (log/info "Saving " record)
      (try
        (jdbc/insert! conn :finkratzen.boa_checking record)
        (catch Throwable e (log/error (str (st/root-cause e)) ""))))))

;;
(defn to-clj-map
  "convert java.util.Map with String keys into to a clojure map
  with keywords as the keys"
  [jmap]
  (keywordize-keys (into {} jmap)))