(ns kratzen.server
  (:require [kratzen.db :refer [->Database pool-db-spec h2-local]]
            [kratzen.boa :refer [boa-download]]
            [kratzen.http :refer [->Http]]
            [kratzen.config :refer :all]
            [kratzen.classifier :refer :all]
            [kratzen.logging :refer [->Logger]]
            [kratzen.channels :refer :all]
            [clojure.tools.logging :refer [info error]])

  (:require [kratzen.scheduler :refer :all]
            [com.stuartsierra.component :as component]
            [clojure.core.async :refer [chan]]
            [kratzen.backup :refer [->Backup]]
            [clojure.tools.logging :as log]))

(def conf
  {:channels {:log-chan (chan)}
   :db-spec  (pool-db-spec h2-local)})

(defn get-system
  "Create a system out of individual components"
  [conf]

  (component/system-map
    :database (->Database)
    :logging (component/using
               (->Logger (:channels conf) (:db-spec conf))
               [:database])
    :classifier (component/using
                  (->BayesClassifier (:db-spec conf))
                  [:database])
    :http (->Http)
    :backup (component/using
              (->Backup (:db-spec conf))
              [:database])
    :boa-download (component/using
                    (boa-download 3600)
                    [:database])))

(def system (get-system conf))

(defn run-service
  "Start the service with the specified download interval in seconds"
  ;([]
  ;  "Start the service with the default download interval of 60 seconds"
  ;  (run-service "3600"))
  ([]
    ;;(init-logging)
   (log/info "Starting Service...")
   (alter-var-root #'system component/start)))
;(start-task
;  #(download-and-save-stmts h2-local 1)
;  (read-string interval))))
