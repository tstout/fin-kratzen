(ns kratzen.server
  (:use [clojure.tools.logging :only (info error)])
  (:use [ring.adapter.jetty])
  (:require [kratzen.db :refer :all]
            [kratzen.boa :refer :all]
            [kratzen.http :refer :all])
  (:require [kratzen.scheduler :refer :all]
            [com.stuartsierra.component :as component])
  (:import (kratzen.db Database)
           (kratzen.http Http)))


;(defn start-db []
;  (let [server (start-h2)]
;    (info "H2 Server status" (.getStatus server))
;    (-> (mk-migrator (h2-remote-server-conn))
;        (.update "/sql/init-schema.sql"))))

(def conf {})

(defn get-system [conf]
  "Create a system out of individual components "
  (component/system-map
    :database (Database.)
    :scheduler (component/using (new-scheduler 2) [:database])
    :http (Http.)
    :boa-download (component/using
                    (boa-download 3600)
                    [:scheduler :database])))

(def system (get-system conf))

(defn run-service
  ;([]
  ;  "Start the service with the default download interval of 60 seconds"
  ;  (run-service "3600"))
  ([]
   "Start the service with the specified download interval in seconds"
   (info "Starting Service...")
   (alter-var-root #'system component/start)))
;(start-task
;  #(download-and-save-stmts h2-local 1)
;  (read-string interval))))
