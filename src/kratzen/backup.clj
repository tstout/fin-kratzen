(ns kratzen.backup
  (:require [gd-io.file :refer [mk-gdrive]]
            [gd-io.protocols :refer []]
            [clojure.tools.logging :as log]
            [clojure.core.async :refer [close!]]
            [kratzen.scheduler :refer [periodic-task]])
  (:import [com.stuartsierra.component Lifecycle]))


(defn upload-backup [time]
  (prn "Upload-backup " time))

(defrecord Backup []
  Lifecycle
  (start [this]
    (log/info "Starting backup component...")
    (assoc this :backup (periodic-task 5 upload-backup)))
  (stop [this]
    (log/info "Stopping backup component...")
    (close! (:boa-download this))))