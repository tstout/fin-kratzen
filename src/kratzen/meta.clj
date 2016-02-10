(ns kratzen.meta
  (:require [kratzen.config :refer [load-edn-resource]]
            [trptcolin.versioneer.core :refer [get-version]]))

;(->> "project.clj"
;     slurp
;     read-string
;     (drop 2)
;     (cons :version)
;     (apply hash-map)
;     (def project))

(defn kratzen-version []
  (get-version "com.github.tstout" "fin-kratzen"))
  ;(:version project))


