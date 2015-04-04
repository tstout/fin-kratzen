(ns kratzen.server
  (:use [clojure.tools.logging :only (info error)])
  (:use [ring.adapter.jetty])
  (:require [kratzen.db :refer :all]
            [kratzen.boa :refer :all]
            [kratzen.http :refer :all]
            [kratzen.config :refer :all]
            [kratzen.logging :refer :all]
            [kratzen.channels :refer :all])

  (:require [kratzen.scheduler :refer :all]
            [com.stuartsierra.component :as component]
            [clojure.core.async :refer [chan]]))

(def conf
  {:channels {:log-chan (chan)}
   :db-spec  (pool-db-spec h2-local)})

(defn get-system [conf]
  "Create a system out of individual components.
  The ->Record constructors appear to be necessary
  to prevent some namespace loading issues. This problem
  was troubling. More research is needed to determine
  if this is the right solution.
  "
  (component/system-map
    :database (->Database)
    :logging (component/using (->Logger (:channels conf) (:db-spec conf)) [:database])
    :scheduler (component/using (new-scheduler 2) [:database])
    :http (->Http)
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
    ;;(init-logging)
   (info "Starting Service...")
   (alter-var-root #'system component/start)))
;(start-task
;  #(download-and-save-stmts h2-local 1)
;  (read-string interval))))
