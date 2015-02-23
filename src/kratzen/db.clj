(ns kratzen.db
  (:import (db.io.config DBVendor DBCredentials$Builder))
  (:import (db.io.migration Migrators)
           (db.io.config Databases))
  (:import (org.h2.tools Server))
  (:require [kratzen.config :refer :all]
            [com.stuartsierra.component :as component])
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [clojure.tools.logging :as log]))

;;
;; Database related stuff...
;;
(comment
  (def ^:privte init-schema
    (load-res "init-schema.sql")))

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
                                (.withDBName "fin-kratzen")
                                (.withDBDir "~/.fin-kratzen/db"))))

(defn h2-local-server-conn []
  (db-conn-factory DBVendor/H2_LOCAL_SERVER))

(defn h2-mem-conn []
  (db-conn-factory DBVendor/H2_MEM))

(defn start-h2
  "Start a local H2 TCP Server"
  []
  (log/info "starting h2...")
  (let [h2Server (Server/createTcpServer (into-array String []))]
    (.start h2Server)))

(defn mk-migrator [conn-factory]
  "Create a schema migrator for H2"
  []
  (Migrators/liquibase conn-factory))

(def h2-local
  {:classname   "org.h2.Driver"
   :subprotocol "h2"
   :subname     "tcp://127.0.0.1/~/.fin-kratzen/db/fin-kratzen"
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
    (log/info "Starting DB...")
    (assoc component :db-server (start-h2)))

  (stop [component]
    (log/info "stopping DB...")
    (.stop (:db-server component))
    (assoc component :db-server nil)))
