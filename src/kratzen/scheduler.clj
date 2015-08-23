(ns kratzen.scheduler
  (:import (java.util.concurrent ScheduledThreadPoolExecutor TimeUnit))
  (:require [clojure.tools.logging :as log]
            [com.stuartsierra.component :as component]
            [clojure.core.async :as a :refer [<! >! go-loop]]
            [clj-time.periodic :refer [periodic-seq]]
            [chime :refer [chime-ch]]
            [clj-time.core :as t]))

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

(defn mk-chime-ch
  "Create a channel that will chime at the specified period"
  [period]
  (log/infof "creating chime channel with period of %d seconds" period)
  (chime-ch (periodic-seq
              (t/now)
              (-> period t/seconds))))

;; TODO - get rid of java interop stuff in favor of chime. This
;; no longer needs to be a component. Components that need peroidic
;; tasks can put the chime channel in the component map and stop
;; as needed in component/stop

(defn chime-task [ch f]
  (go-loop []
    (when-let [msg (<! ch)]
      (f msg)
      (recur)))
  ch)

(defn periodic-task [period f]
  (chime-task
    (mk-chime-ch period)
    f))

(defrecord Scheduler [thread-pool num-threads]
  component/Lifecycle

  (start [component]
    (log/info "starting scheduler")

    (periodic-task 3 #(prn "chiming at: " %))

    (assoc component :thread-pool (ScheduledThreadPoolExecutor. num-threads)))

  (stop [component]
    (log/info "stopping scheduler")
    (.shutdown (:thread-pool component))
    (assoc component :thread-pool nil)))


(defn new-scheduler [num-threads]
  (map->Scheduler {:num-threads num-threads}))