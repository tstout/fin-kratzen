(ns kratzen.db
  (:import (db.io.config DBVendor DBCredentials$Builder))
  (:import (db.io.migration Migrators)
           (db.io.config Databases DBHost))
  (:import (org.h2.tools Server)
           (java.net InetAddress)
           (db.io.core ConnFactory)
           (org.h2.jdbcx JdbcConnectionPool))
  (:require [kratzen.config :refer [load-res load-config]]
            [com.stuartsierra.component :as component]
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
    (log/infof "Using host name %s for DB..." host)
    host))

(def db-config
  "A map containing :user and :pass
  for the BOA database"
  (let [cfg (load-config)
        boa (:boa cfg)]
    (zipmap [:user :pass] [(:db-user boa) (:db-pass boa)])))


(defn db-conn-factory
  "Create a DB connection factory using db.io"
  [db-vendor]
  (Databases/newConnFactory db-vendor
                            (-> (DBCredentials$Builder.)
                                (.withDbhost (DBHost. (host-name)))
                                (.withDBName "fin-kratzen")
                                (.withDBDir "~/.fin-kratzen/db"))))

(defn h2-mem-conn []
  (db-conn-factory DBVendor/H2_MEM))

(defn start-h2
  "Start a local H2 TCP Server"
  []
  (log/info "starting h2...")
  (let [h2Server (Server/createTcpServer (into-array String ["-tcpAllowOthers"]))]
    (.start h2Server)))

(defn mk-migrator
  "Create a schema migrator for H2"
  [db-spec]
  (let [spec db-spec]
    (Migrators/liquibase
      (reify ConnFactory
        (connection [_]
          (jdbc/get-connection spec))))))

(def h2-local
  {:classname   "org.h2.Driver"
   :subprotocol "h2"
   :subname     (format "tcp://%s/~/.fin-kratzen/db/fin-kratzen;jmx=true" (host-name))
   :user        (:user db-config)
   :password    (:pass db-config)})

(def h2-mem
  {:classname   "org.h2.Driver"
   :subprotocol "h2"
   :subname     "mem:fin-kratzen;DB_CLOSE_DELAY=-1"
   :user        "sa"
   :password    ""})

(defn- mk-h2-pool
  "Creates a simple H2 connection pool (supplied by H2)
   Nothing fancy, but probably adequate for this app."
  [db-spec]

  {:datasource
   (JdbcConnectionPool/create
     (format "jdbc:h2:%s" (:subname db-spec))
     (:user db-spec)
     (:password db-spec))})

(defn reset-seq [db-spec seq-name]
  (jdbc/execute!
    db-spec
    [(format "alter sequence %s restart with 0 increment by 1" seq-name)]))

(defn next-seq-val
  "Get the next value of a sequence"
  [db-spec seq-name]
  (jdbc/with-db-connection
    [conn db-spec]
    (:value
      (first
        (jdbc/query
          conn
          [(format "select nextval('%s') as value" seq-name)])))))

(def pool-db-spec (memoize mk-h2-pool))

(def schema-files ["/sql/init-schema.sql"
                   "/sql/backup-schema.sql"])

(defn run-query [sql]
  (jdbc/with-db-connection
    [conn (pool-db-spec h2-local)]
    (jdbc/query conn [sql])))

(defrecord Database []
  component/Lifecycle

  (start [component]
    (log/info "Starting DB component...")
    (let [db (assoc component :db-server (start-h2))]
      (log/info "Running DB migration...")
      (doseq [script schema-files]
        (log/infof "processing schema file %s" script)
        (-> (mk-migrator h2-local)
            (.update script)))
      db))

  (stop [component]
    (log/info "stopping DB...")
    (when-let [server (:db-server component)]
      (.stop server))
    (assoc component :db-server nil)))

