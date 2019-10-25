(ns kratzen.system
  (:require [kratzen.db :refer [->Database pool-db-spec h2-local]]
            [kratzen.boa :refer [boa-download]]
            [kratzen.email :refer [->Email]]
            [kratzen.http :refer [->Http]]
            [kratzen.config :refer :all]
            [kratzen.classifier :refer :all]
            [kratzen.logging :refer [->Logger]]
            [kratzen.channels :refer :all]
            [clojure.tools.logging :refer [info error]]
            [nrepl.server :refer (start-server stop-server)])

  (:require [kratzen.scheduler :refer :all]
            [com.stuartsierra.component :as comp]
            [clojure.core.async :refer [chan]]
            [kratzen.backup :refer [->Backup]]
            [clojure.tools.logging :as log]))

(def repl-server (atom nil))

(def conf
  {:channels {:log-chan (chan)}
   :db-spec  (pool-db-spec h2-local)})

(defn get-system
  "Create a system out of individual components"
  [conf]
  (comp/system-map
    :database (->Database)
    :logging (comp/using
               (->Logger (:channels conf) (:db-spec conf))
               [:database])
    :classifier (comp/using
                  (->BayesClassifier (:db-spec conf))
                  [:database :logging])
    :http (comp/using (->Http) [:logging])
    :backup (comp/using
              (->Backup (:db-spec conf))
              [:database :logging])
    :boa-download (comp/using
                    (boa-download 3600)
                    [:database :logging])
    :email (comp/using (->Email)
                       [:database :logging])))

(defn system [] (get-system conf))

(defn start
  "Performs side effects to initialize the system, acquire resources,
  and start it running. Returns an updated instance of the system."
  [system]
  (comp/start system))

(defn stop
  "Performs side effects to shut down the system and release its
  resources. Returns an updated instance of the system."
  [system]
  (comp/stop system))

(defn run-service []
  (log/info "Starting Service...")
  (reset! repl-server (start-server :port 4576 :bind "0.0.0.0"))
  (comp/start (system)))

