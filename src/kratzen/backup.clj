(ns kratzen.backup
  (:require [gd-io.file :refer [mk-gdrive]]
            [gd-io.protocols :refer [upload]]
            [clojure.java.jdbc :as jdbc]
            [clojure.tools.logging :as log]
            [clojure.core.async :refer [close!]]
            [kratzen.scheduler :refer [periodic-task]])
  (:import [com.stuartsierra.component Lifecycle]))


(def backup-file
  (format
    "%s/.fin-kratzen/db/fk-backup.zip"
    (System/getProperty "user.home")))

(defn mk-backup [db-spec]
    (jdbc/execute!
      db-spec
      [(format "backup to '%s'" backup-file)]))

(defn upload-backup [db-spec]
  (log/infof "creating backup at %s" backup-file)
  (mk-backup db-spec)
  ;(let [gdrive (mk-gdrive)]
  ;            (upload gdrive))
  )

(defrecord Backup [db-spec]
  Lifecycle
  (start [this]
    (log/info "Starting backup component...")
    (assoc this :backup (periodic-task
                          5
                          (fn [_] (upload-backup db-spec)))))
  (stop [this]
    (log/infof "Stopping backup component ...")
    (when-let [boa-ch (:backup this)] (close! boa-ch))
    (assoc this :backup nil)))