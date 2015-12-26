(ns kratzen.scheduler
  (:require [clojure.tools.logging :as log]
            [clojure.core.async :refer [<! >! go-loop]]
            [clj-time.periodic :refer [periodic-seq]]
            [chime :refer [chime-ch]]
            [clj-time.core :as t]))

(defn mk-chime-ch
  "Create a channel that will chime every period seconds"
  [period]
  (log/infof "creating chime channel with period of %d seconds" period)
  (chime-ch (periodic-seq
              (-> 1 t/seconds t/from-now)
              (-> period t/seconds))))

(defn with-catch [f arg]
  (try
    (f arg)
    (catch Throwable e (log/error e e))))

(defn chime-task [ch f]
  (go-loop []
    (when-let [msg (<! ch)]
      (with-catch f msg)
      (recur)))
  ch)

(defn periodic-task [period f]
  (chime-task
    (mk-chime-ch period)
    f))