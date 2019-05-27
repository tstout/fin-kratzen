(ns kratzen.config
  (:require [clojure.edn :as edn]
            [clojure.java.io :refer [resource
                                     as-file
                                     file
                                     make-parents
                                     output-stream
                                     writer]]
            [clojure.pprint :refer [pprint pp]]))

(defn load-config
  "Load configuration from ~/.fin-kratzen/config.clj
   The config must be in EDN format."
  []
  (-> (System/getProperty "user.home")
      (file ".fin-kratzen/config.clj")
      slurp
      edn/read-string))

(def cfg-file
  (->
      "user.home"
      System/getProperty
      (file ".fin-kratzen/config.clj")))

(def stub-config
  {:boa {:user ""
         :pass ""
         :account ""
         :routing ""
         :client-id ""
         :db-user ""
         :db-pass ""}
   :email {:user ""
           :pass ""}})

(defn mk-config []
  (when-not (.exists cfg-file)
    (make-parents cfg-file)
    (with-open [out (writer (output-stream cfg-file))]
      (pprint stub-config out))))

(defn load-config
  "Load configuration from ~/.fin-kratzen/config.clj
   The config must be in EDN format."
  []
  (mk-config)
  (-> cfg-file
      slurp
      edn/read-string))

(def cfg (load-config))


(defn
  load-res [res]
  (-> res
      resource
      slurp))

(defn load-edn-resource [res]
  (->> res
       resource
       slurp
       edn/read-string))

(def creds
  (memoize (fn [] (:boa (load-config)))))