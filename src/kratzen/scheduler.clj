(ns kratzen.scheduler
  (:import (java.util.concurrent ScheduledThreadPoolExecutor TimeUnit Executors))
  (:use [clojure.tools.logging :only (info error)]))

;;
;; This is dead simple at the moment. When the need arises, I'll probably use cronj
;; https://github.com/zcaudate/cronj
;;
(def ^:private executor
  (ScheduledThreadPoolExecutor. 2))

(defn invoke-task [f]
  (try
    (f)
    (catch Throwable e (error e e))))

(defn start-task
  [f period-in-sec]
  (info "starting task...." f)
  (.scheduleAtFixedRate
    executor #(invoke-task f) 0 period-in-sec TimeUnit/SECONDS))

(defn stop-scheduler []
  (.shutdown executor))
