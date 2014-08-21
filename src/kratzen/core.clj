(ns kratzen.core
  (:require [kratzen.config :refer :all])
  (:require [kratzen.db :refer :all])
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io])
  (:use [clojure.tools.logging :only (info error)])
  (:gen-class :main true))

(#_ (load "db"))
(#_ (load "model"))

(defn -main [& args]
  (info "fin-kratzen starting...")
  (let [server (start-h2)]
    (info "H2 Server status" (.getStatus server))
    (doto (mk-migrator)
      (.update "init-schema.sql"))))
