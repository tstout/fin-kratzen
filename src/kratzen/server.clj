(ns kratzen.server
  (:use [clojure.tools.logging :only (info error)])
  (:require [kratzen.db :refer :all]
            [kratzen.boa :refer :all])
  (:require [kratzen.scheduler :refer :all]))

(defn start-db []
  (let [server (start-h2)]
    (info "H2 Server status" (.getStatus server))
    (-> (mk-migrator (h2-local-server-conn))
        (.update "/sql/init-schema.sql"))))

(defn run-service []
  (info "Starting Service...")
  (start-db)
  (start-task download-boa-stmts 5))