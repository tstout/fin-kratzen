(ns kratzen.core
  (:import (db.io.h2 H2Db))
  (:import (db.io.migration Migrators))
  (:import (db.io.h2 H2Credentials))
  (:import (org.h2.tools Server))
  (:import (java.sql Timestamp))
  (:import (java.sql Date))
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io])
  (:use [clojure.tools.logging :only (info error)]))

(defn load-config []
  (-> (io/file (System/getProperty "user.home"), ".fin-kratzen.clj")
      slurp
      edn/read-string))

(defn load-res [res]
  (-> res
     io/resource
     slurp))

(defn load-edn-resource [res]
  (->> res
       io/resource
       slurp
       edn/read-string))

(load "db")
(load "model")

(defn -main []
  (info "fin-kratzen starting...")
  (let [server (start-h2)
        migrator (mk-migrator)]
    (info "H2 Server status" (.getStatus server))
    (doto
        (mk-migrator)
        (.update "init-schema.sql"))))