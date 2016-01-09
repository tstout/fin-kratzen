(ns kratzen.backup
  (:require [gd-io.file :refer [mk-gdrive]]
            [gd-io.protocols :refer [upload]]
            [clojure.java.jdbc :as jdbc]
            [clojure.tools.logging :as log]
            [clojure.core.async :refer [close!]]
            [kratzen.db :refer [next-seq-val pool-db-spec h2-local]]
            [kratzen.scheduler :refer [periodic-task]]
            [clojure.java.io :refer [file]])
  (:import [com.stuartsierra.component Lifecycle]))

(def local-backup-file
  (format
    "%s/.fin-kratzen/db/fk-backup.zip"
    (System/getProperty "user.home")))

(defn mk-backup
  "Create a backup zip of the H2 database"
  [db-spec]
  (jdbc/execute!
    db-spec
    [(format "backup to '%s'" local-backup-file)]))

(defn next-backup-file []
  (format
    "fk-backup-%d.zip"
    (next-seq-val (pool-db-spec h2-local) "FINKRATZEN.BACKUP_SEQ")))

(defn upload-to-gdrive [bfile]
  (->
    (mk-gdrive)
    (upload {:title         bfile
             :parent-folder "/backup/fin-kratzen"
             :file          (file local-backup-file)})))

(defn save-backup-meta [file-id file-name]
  (jdbc/with-db-connection
    [conn (pool-db-spec h2-local)]
    (log/infof "Saving backup record for file-id %s" file-id)
    (jdbc/insert!
      conn
      :finkratzen.backup_files
      {:id        file-id
       :file_name file-name})))

(defn upload-backup [db-spec]
  (log/infof "creating local backup at %s" local-backup-file)
  (mk-backup db-spec)
  (let [bfile (next-backup-file)
        gdrive-file-id (upload-to-gdrive bfile)]
    (log/infof "uploaded backup file %s (%s)" bfile gdrive-file-id)
    (save-backup-meta gdrive-file-id bfile)))

(defrecord Backup [db-spec]
  Lifecycle
  (start [this]
    (log/info "Starting backup component...")
    (assoc this :backup (periodic-task
                          30
                          (fn [_] (upload-backup db-spec)))))
  (stop [this]
    (log/infof "Stopping backup component ...")
    (when-let [boa-ch (:backup this)]
      (close! boa-ch))
    (assoc this :backup nil)))