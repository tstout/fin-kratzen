;; ;;
;; ;; Loaded when running the REPL or running tests.
;; ;; Define all your project dev conveniences here.
;; ;;
;; (ns user
;;   (:require [kratzen.system :as system]
;;             [kratzen.scheduler :refer [task]]
;;             [kratzen.config :as cfg]
;;             [kratzen.db :refer [pool-db-spec h2-local]]
;;             #_[clojure.tools.namespace.repl :refer [refresh refresh-all]]
;;             [clojure.java.jdbc :as jdbc]
;;             [gd-io.protocols :refer [upload]]
;;             [clj-time.core :as t]
;;             [clj-time.format :as tf]
;;             [clojure.pprint :refer [pprint print-table]]
;;             [kratzen.http :as http]
;;             [kratzen.dates :refer [interval]]
;;             [kratzen.boa-ofx :as ofx]
;;             [kratzen.boa :as boa]
;;             [kratzen.email :refer [send-email]]
;;             [kratzen.db :as db]
;;             [kratzen.dates :as dates]))

;; (println "-- loading custom settings from user.clj --")

;; (defn load-vars []
;;   (require '[kratzen.boa-ofx :as ofx]
;;            '[kratzen.boa :as boa]))


;; (def db (pool-db-spec h2-local))

;; (def system nil)

;; (defn init
;;   "Constructs the dev system."
;;   []
;;   (alter-var-root #'system
;;                   (constantly (system/system))))

;; (defn start
;;   "Starts the dev system."
;;   []
;;   (alter-var-root #'system system/start))

;; (defn stop
;;   "Shuts down and destroys dev system."
;;   []
;;   (alter-var-root #'system
;;                   (fn [s] (when s (system/stop s)))))

;; (defn go
;;   "Initializes the current dev system and starts it."
;;   []
;;   (init)
;;   (start))

;; (defn reset []
;;   (stop)
;;   (refresh :after 'user/go))