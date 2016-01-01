(ns kratzen.core
  (:require [kratzen.cli :refer :all])
  (:require [kratzen.config :refer :all])
  (:require [kratzen.system :refer :all])
  (:use [clojure.tools.logging :only (info error)])
  (:gen-class :main true))

(defn show-help []
  (println (load-res "help.txt")))

(def options
  {:server {:cmd run-service}
   :help   {:cmd show-help}})

(defn -main [& args]
  (if (zero? (count args))
    (show-help)
    (process-args args options)))

