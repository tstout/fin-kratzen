(ns kratzen.db
  (:import (db.io.h2 H2Db))
  (:import (db.io.migration Migrators))
  (:import (db.io.h2 H2Credentials))
  (:import (org.h2.tools Server))
  (:import (java.sql Timestamp))
  (:import (java.sql Date))
  (:require [kratzen.config :refer :all])
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io])
  (:use [clojure.tools.logging :only (info error)]))

;;
;; Database related stuff...
;;

(def ^:privte init-schema
  (load-res "init-schema.sql"))

(def db-config
  "Returns a map containing :user and :pass
  for the BOA database"
  (let [cfg (load-config)
        boa (cfg :boa)]
    (zipmap [:user :pass] [(boa :db-user) (boa :db-pass)])))

(def db-creds
  (H2Credentials/h2LocalServerCreds "fin-kratzen" "db"))

(defn start-h2
  "Start a local H2 TCP Server"
  []
  (info "starting h2...")
  (let [h2Server (Server/createTcpServer (into-array String []))]
    (.start h2Server)))


(defn mk-migrator
  "Create a schema migrator for H2"
  []
  (Migrators/liquibase (H2Db.) db-creds))

