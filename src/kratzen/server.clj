(ns kratzen.server
  (:use [clojure.tools.logging :only (info error)])
  (:use [ring.adapter.jetty])
  (:require [kratzen.db :refer :all]
            [kratzen.boa :refer :all]
            [kratzen.rest :refer :all])
  (:require [kratzen.scheduler :refer :all]))



(defn start-db []
  (let [server (start-h2)]
    (info "H2 Server status" (.getStatus server))
    (-> (mk-migrator (h2-local-server-conn))
        (.update "/sql/init-schema.sql"))))

(defn run-service
  ([]
   "Start the service with the default download interval of 60 seconds"
   (run-service "3600"))
  ([interval]
   "Start the service with the specified download interval in seconds"
  (info "Starting Service...")
  (start-db)
  (start-task
    #(download-and-save-stmts 1)
    (read-string interval))
  (run-jetty handler {:port 3000})))