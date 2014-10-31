(ns kratzen.server
  (:use [clojure.tools.logging :only (info error)])
  (:use [ring.adapter.jetty])
  (:require [kratzen.db :refer :all]
            [kratzen.boa :refer :all])
  (:require [kratzen.scheduler :refer :all]))

(defn handler [request]
  (info request)
  {:status  200
   :headers {"Content-Type" "text/html"}
   :body    "Hello World"})

(defn start-db []
  (let [server (start-h2)]
    (info "H2 Server status" (.getStatus server))
    (-> (mk-migrator (h2-local-server-conn))
        (.update "/sql/init-schema.sql"))))

(defn run-service []
  (info "Starting Service...")
  (start-db)
  (start-task
    #(download-and-save-stmts 1)
    5)
  (run-jetty handler {:port 3000}))