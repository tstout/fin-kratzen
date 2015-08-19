(ns kratzen.db-util
  (:require [kratzen.db :refer [mk-migrator h2-mem pool-db-spec]]
            [kratzen.model :refer [save-boa]]))

(defn load-db [test-data]
  (->
    (mk-migrator h2-mem)
    (.update "/sql/init-schema.sql"))
  (save-boa (pool-db-spec h2-mem) test-data))