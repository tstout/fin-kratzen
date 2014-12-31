(ns kratzen.model
  (:require [kratzen.config :refer :all]
            [kratzen.dates :refer :all]
            [clojure.walk :refer :all]
            [kratzen.db :refer :all]
            [clojure.java.jdbc :as jdbc]
            [clojure.tools.logging :as log]))

;;
;; Define interface(s) needed by db-io...
;; Might be easier just to define this with java...
;;
(definterface CheckingEntry
              [^String bankId []]
              [^java.sql.Date postingDate []]
              [^java.math.BigDecimal amount []]
              [^java.sql.Timestamp recordCreated []])

(def ^:private sql
  {:select-boa (load-res "select-boa.sql")
   :insert-boa (load-res "insert-boa.sql")})

(defn fetch-boa
  ([db start end]
   (jdbc/query db [(:select-boa sql) start end]))
  ([db offset]
   (let [interv (interval offset)]
     (fetch-boa db (:start interv) (:end interv)))))

(defn select-boa []
  (jdbc/query h2-local ["select top(4) * from finkratzen.boa_checking"]))

(defn save-boa [conn records]
  "save to boa table..."
  (log/info "saving" (count records) "BOA records")
  (doseq [record records]
    (jdbc/insert! conn :finkratzen.boa_checking record)))

;;
(defn to-clj-map
  "convert java.util.Map with String keys into to a clojure map
  with keywords as the keys"
  [jmap]
  (keywordize-keys (into {} jmap)))