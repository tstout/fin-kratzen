(ns kratzen.db
  (:import (db.io.config DBVendor DBCredentials$Builder))
  (:import (db.io.migration Migrators)
           (db.io.config Databases DBHost))
  (:import (org.h2.tools Server)
           (java.net InetAddress)
           (db.io.core ConnFactory))
  (:require [kratzen.config :refer :all]
            [com.stuartsierra.component :as component])
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [clojure.tools.logging :as log]
            [clojure.java.jdbc :as jdbc]))

;;
;; Database related stuff...
;;
(comment
  (def ^:privte init-schema
    (load-res "init-schema.sql")))

(defn host-name []
  (let [host (.. InetAddress getLocalHost getHostName)]
    (log/info (format "Using host name %s for DB..." host))
    host))

(def db-config
  "A map containing :user and :pass
  for the BOA database"
  (let [cfg (load-config)
        boa (:boa cfg)]
    (zipmap [:user :pass] [(:db-user boa) (:db-pass boa)])))

(defn db-conn-factory [db-vendor]
  "Create a DB connection factory using db.io"
  (Databases/newConnFactory db-vendor
                            (-> (DBCredentials$Builder.)
                                (.withDbhost (DBHost. (host-name)))
                                (.withDBName "fin-kratzen")
                                (.withDBDir "~/.fin-kratzen/db"))))

(defn h2-mem-conn []
  (db-conn-factory DBVendor/H2_MEM))

(defn start-h2 []
  "Start a local H2 TCP Server"
  (log/info "starting h2...")
  (let [h2Server (Server/createTcpServer (into-array String ["-tcpAllowOthers"]))]
    (.start h2Server)))

(defn mk-migrator [db-spec]
  "Create a schema migrator for H2"
  (let [spec db-spec]
    (Migrators/liquibase
      (reify ConnFactory
        (connection [_]
          (jdbc/get-connection spec))))))

(def h2-local
  {:classname   "org.h2.Driver"
   :subprotocol "h2"
   :subname     (format "tcp://%s/~/.fin-kratzen/db/fin-kratzen;JMX=TRUE" (host-name))
   :user        (:user db-config)
   :password    (:pass db-config)})

(def h2-mem
  {:classname   "org.h2.Driver"
   :subprotocol "h2"
   :subname     "mem:fin-kratzen;DB_CLOSE_DELAY=-1"
   :user        "sa"
   :password    ""})

(defrecord Database []
  component/Lifecycle

  (start [component]
    (log/info "Starting DB component...")
    (let [db (assoc component :db-server (start-h2))]
      (log/info "Running DB migration...")
      (-> (mk-migrator h2-local)
          (.update "/sql/init-schema.sql"))
      db))

  (stop [component]
    (log/info "stopping DB...")
    (.stop (:db-server component))
    (assoc component :db-server nil)))
