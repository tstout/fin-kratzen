(ns kratzen.scheduler
  (:import (java.util.concurrent ScheduledThreadPoolExecutor TimeUnit Executors))
  (:use [clojure.tools.logging :only (info error)]))

(def ^:private executor
  (ScheduledThreadPoolExecutor. 5))

(defn invoke-task [f]
  (try
    (f)
    (catch Throwable e (error e))))

(defn start-task [f period-in-sec]
  (info "starting task....")
  (.scheduleAtFixedRate executor #(invoke-task f) 0 period-in-sec TimeUnit/SECONDS))

(defn stop-scheduler []
  (.shutdown executor))
