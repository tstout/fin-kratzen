(ns kratzen.scheduler
  (:import (java.util.concurrent ScheduledThreadPoolExecutor TimeUnit))
  (:require [clojure.tools.logging :as log]
            [com.stuartsierra.component :as component]))

;;
;; Simple for the moment. When the need arises, I'll probably use cronj
;; https://github.com/zcaudate/cronj
;;

(defn invoke-task [f]
  (try
    (f)
    (catch Throwable e (log/error e e))))

(defn start-task
  "Start a periodic task. The function f is executed at the specified period.
  Returns a java.util.concurrent.Future "
  [scheduler f period-in-sec]
  (log/info "starting task...." f)
  (.scheduleAtFixedRate
    (:thread-pool scheduler) #(invoke-task f) 0 period-in-sec TimeUnit/SECONDS))

(defrecord Scheduler [thread-pool num-threads]
  component/Lifecycle

  (start [component]
    (log/info "starting scheduler")
    (assoc component :thread-pool (ScheduledThreadPoolExecutor. num-threads)))

  (stop [component]
    (log/info "stopping scheduler")
    (.shutdown (:thread-pool component))
    (assoc component :thread-pool nil)))


(defn new-scheduler [num-threads]
  (map->Scheduler {:num-threads num-threads}))