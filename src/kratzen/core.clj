(ns kratzen.core
  (:require [kratzen.cli :refer :all])
  (:require [kratzen.config :refer :all])
  (:require [kratzen.db :refer :all])
  (:require [kratzen.model :refer :all])
  (:require [kratzen.server :refer :all])
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io])
  (:use [clojure.tools.logging :only (info error)])
  (:gen-class :main true))

(defn show-help []
  (println (load-res "help.txt")))

(def options
  {:server {:value false :cmd run-service}
   :help   {:value false :cmd show-help}})

(defn -main [& args]
  (if (zero? (count args))
    (show-help)
    (process-args args options)))
