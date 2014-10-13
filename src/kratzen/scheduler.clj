(ns kratzen.scheduler
  (:import (java.util.concurrent ScheduledThreadPoolExecutor TimeUnit Executors))
  (:require [clj-time.core :as t]
            [clj-time.periodic :refer [periodic-seq]])
  (:require [kratzen.db :refer :all])
  (:require [kratzen.model :refer :all])
  (:use [clojure.tools.logging :only (info error)]))

(def ^:private executor
  (Executors/newCachedThreadPool))

(defn download-stmts []
  (info "downloading stmts..."))

(defn start-scheduler []
  (.scheduleAtFixedRate executor download-stmts 0 5 TimeUnit/SECONDS))

(defn stop-scheduler []
  (.shutdown executor))



