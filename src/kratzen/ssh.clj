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
                       :exit exit
                       :out out})))
    out))

(defn scp-args
  [host backup-fname]
  ["scp"
   "-i"
   "~/opc/ssh-key-2022-05-06.key"
   #_(str "~/.fin-kratzen/db/" backup-fname)
   (str (home-dir) "/.fin-kratzen/db/" backup-fname)
   (str "opc@" host ":/home/opc/backup/fin-kratzen")])

(defn ssh-rm-args
  [host backup-fname]
  ["ssh"
   (str "opc@" host)
   "-i"
   "~/opc/ssh-key-2022-05-06.key"
   (format "rm /home/opc/backup/fin-kratzen/%s" backup-fname)])

(defn scp
  "Execute a secure copy"
  [backup-fname]
  (-> @backup-host
      (scp-args backup-fname)
      exec))

(defn ssh-rm
  "Execute a remote rm via ssh"
  [backup-fname]
  (-> @backup-host
      (ssh-rm-args backup-fname)
      exec))


(comment
  *e
  (home-dir)
  (format "\"x%s\"" "some-arg")
  (ssh-rm "test.zip")

  @backup-host
  (apply shell/sh (scp-args @backup-host "fk-backup.zip"))

  (ssh-rm "test.zip")

  (->> (ssh-rm-args @backup-host "test.zip")
       (interpose " ")
       (apply str))

  (scp-args @backup-host "fk-backup.zip")
  (exec ["ls" "-lart"])

  (scp "fk-backup.zip")

  (identity-file "opc/ssh-key-2021-06-20.key")
  ;;
  )

