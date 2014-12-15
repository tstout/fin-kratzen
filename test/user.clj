 (ns user
   (:import
     (db.io.operations Query Queries Updates))
   (:require
     [kratzen.db :refer :all]
     [kratzen.model :refer :all]
     [kratzen.boa :refer :all]
     [kratzen.dates :refer :all]
     [clj-time.core :as t]))

 (println "----loaded user file from test ---")

 (defn load-db [test-data]
   (-> (h2-mem-conn)
       (mk-migrator)
       (.update "/sql/init-schema.sql"))
   (save-boa h2-mem test-data))