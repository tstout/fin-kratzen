(ns kratzen.config
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io])
  (:use [clojure.tools.logging :only (info error)]))

(defn load-config
  "Load configuration from ~/.fin-kratzen.clj
   The config must be in EDN format"
  []
  (-> (io/file (System/getProperty "user.home"), ".fin-kratzen.clj")
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
