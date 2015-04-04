(ns kratzen.channels
  (:require [com.stuartsierra.component :as component]
            [clojure.core.async :as async]))

;;
;; Inject this everywhere or divide into functionally separate
;; components
;;
(defrecord Channels []
  component/Lifecycle

  (start [component]
    (assoc component :channels {:log-db (async/chan)}))

  (stop [component]
    (async/close! (:log-db component))))