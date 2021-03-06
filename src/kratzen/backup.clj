(ns kratzen.backup
  (:require [gd-io.file :refer [mk-gdrive]]
            [gd-io.protocols :refer [upload rm]]
            [clojure.java.jdbc :as jdbc]
            [clojure.tools.logging :as log]
            [clojure.core.async :refer [close!]]
            [kratzen.db :refer [next-seq-val pool-db-spec h2-local]]
            [kratzen.scheduler :refer [task]]
            [kratzen.dates :refer [every-day-at]]
            [clojure.java.io :refer [file]]
            [kratzen.config :refer [load-res]])
  (:import [com.stuartsierra.component Lifecycle]))

(def sql
  {:old-backups (load-res "select-old-backup.sql")})

(def local-backup-file
  (format
    "%s/.fin-kratzen/db/fk-backup.zip"
    (System/getProperty "user.home")))

(defn legacy-backup-files [db-spec]
  (jdbc/with-db-connection
    [conn db-spec]
    (jdbc/query
      conn
      [(:old-backups sql)])))

(defn rm-backup-meta [id db-spec]
  (jdbc/with-db-connection
    [conn db-spec]
    (jdbc/delete! conn :finkratzen.backup_files ["ID = ?" id])))

(defn rm-old-backups [db-spec]
  (let [gdrive (mk-gdrive)]
    (doseq [{:keys [id file_name]} (legacy-backup-files db-spec)]
      (log/infof "deleting backup file %s" file_name)
      (rm gdrive id)
      (rm-backup-meta id db-spec))))

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

;; TODO - this belongs in logging namespace
;; TODO - create a fn wrapping with-db-connection/pool-db-spec
(defn trim-logs []
  (jdbc/with-db-connection
    [conn (pool-db-spec h2-local)]
    (log/infof "purging logs older than 30 days")
    (jdbc/delete! conn
                  :finkratzen.log
                  ["when <= getdate() - 30"])))

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
  (trim-logs)
  (rm-old-backups db-spec)
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
    (assoc this :backup (task
                          (every-day-at 7)
                          (fn [_] (upload-backup db-spec)))))
  (stop [this]
    (log/infof "Stopping backup component ...")
    (when-let [backup-ch (:backup this)]
      (close! backup-ch))
    (assoc this :backup nil)))