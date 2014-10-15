(ns kratzen.scheduler
  (:import (java.util.concurrent ScheduledThreadPoolExecutor TimeUnit Executors))
  (:require [clj-time.core :as t]
            [clj-time.periodic :refer [periodic-seq]])
  (:require [kratzen.db :refer :all])
  (:require [kratzen.model :refer :all])
  (:use [clojure.tools.logging :only (info error)]))

(def ^:private executor
  (ScheduledThreadPoolExecutor. 1))

(defn invoke-task [f]
  (try
    (f)
    (catch Exception e (error e))))
;;    (catch Exception e (error "Exception" (.getMessage e)))))

(defn start-task [f period-in-sec]
  (info "starting task....")
  (.scheduleAtFixedRate executor #(invoke-task f) 0 period-in-sec TimeUnit/SECONDS))

(defn stop-scheduler []
  (.shutdown executor))
