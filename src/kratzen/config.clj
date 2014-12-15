(ns kratzen.config
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io])
  (:use [clojure.tools.logging :only (info error)]))

(defn load-config
  "Load configuration from ~/.fin-kratzen/config.clj
   The config must be in EDN format."
  []
  (-> (System/getProperty "user.home")
      (io/file ".fin-kratzen/config.clj")
      slurp
      edn/read-string))

(defn load-res [res]
  (-> res
      io/resource
      slurp))

(defn load-edn-resource [res]
  (->> res
       io/resource
       slurp
       edn/read-string))

