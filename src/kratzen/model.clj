(ns kratzen.model
  (:require [kratzen.config :refer :all]
            [kratzen.dates :refer :all]
            [clojure.walk :refer :all]
            [kratzen.db :refer :all]
            [clojure.java.jdbc :as jdbc]
            [clojure.tools.logging :as log]))

(def ^:private sql
  {:select-boa (load-res "select-boa.sql")
   :insert-boa (load-res "insert-boa.sql")})

(defn fetch-boa
  ([db start end]
   {:pre (:datasource db)}
   (jdbc/with-db-connection [conn db]
                            (println conn)
                            (jdbc/query conn [(:select-boa sql) start end])))

  ([db offset]
   (let [interv (interval offset)]
     (fetch-boa db (:start interv) (:end interv)))))

(defn select-boa []
  (jdbc/query pool-db-spec ["select top(4) * from finkratzen.boa_checking"]))

(defn save-boa [db records]
  "save to boa table..."
  (log/info "saving" (count records) "BOA records")
  (jdbc/with-db-connection [conn db]
                           (doseq [record records]
                             (log/info "Saving " record)
                             (jdbc/insert! conn :finkratzen.boa_checking record))))

;;
(defn to-clj-map
  "convert java.util.Map with String keys into to a clojure map
  with keywords as the keys"
  [jmap]
  (keywordize-keys (into {} jmap)))