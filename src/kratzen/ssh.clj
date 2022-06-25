(ns kratzen.ssh
  (:require [clojure.java.shell :as shell]
            [kratzen.config :refer [backup-host]]))

(defn identity-file [key-file]
  (-> "user.home"
      System/getProperty
      (str "/" key-file)))

(defn home-dir []
  (-> "user.home"
      System/getProperty))

;; TODO - perhaps return sh map instead of throwing here...
(defn exec
  "Execute a shell command. On success, returns the stdout of the command.
  On error an exception is thrown with the non-zero exit code and stderr."
  [args]
  {:pre [(coll? args)]}
  (let [{:keys [error exit out]} (apply shell/sh args)]
    (when-not (zero? exit)
      (throw (ex-info (format "cmd %s failed" (first args))
                      {:error error
                       :exit exit})))
    out))

(defn scp-args
  [host backup-fname]
  ["scp"
   "-i"
   "~/opc/ssh-key-2022-05-06.key"
   #_(str "~/.fin-kratzen/db/" backup-fname)
   (str (home-dir) "/.fin-kratzen/db/" backup-fname)
   (str "opc@" host ":/home/opc/backup/fin-kratzen")])

(defn scp
  "Execute a secure copy. "
  [backup-fname]
  (-> @backup-host
      (scp-args backup-fname)
      exec))


(comment
  *e
  (home-dir)

  (apply shell/sh (scp-args @backup-host "fk-backup.zip"))

  (scp-args @backup-host "fk-backup.zip")
  (java.util.UUID/randomUUID)
  (exec ["ls" "-lart"])

  (scp "fk-backup.zip")

  (identity-file "opc/ssh-key-2021-06-20.key")
  ;;
  )

