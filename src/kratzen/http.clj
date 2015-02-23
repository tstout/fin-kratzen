(ns kratzen.http
  (:use [clojure.tools.logging :as log])
  (:use [ring.adapter.jetty])
  (:import (com.stuartsierra.component Lifecycle)))

;;
;; Thanks to John Lawrence Aspden for providing a nice intro to ring
;; http://www.learningclojure.com/2013/01/getting-started-with-ring.html

(defn handler [request]
  (log/info request)
  {:status  200
   :headers {"Content-Type" "text/html"}
   :body    "Hello World"})

;;(defn api-doc [])

(defrecord Http []
  Lifecycle

  (start [component]
    (log/info "starting http...")
    (assoc component :http (run-jetty handler {:port 3000 :join? false})))

  (stop [component]
    (log/info "stopping http...")
    (.stop (:http component))
    (assoc component :http nil)))
