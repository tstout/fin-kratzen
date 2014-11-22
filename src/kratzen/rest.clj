(ns kratzen.rest
  (:use [clojure.tools.logging :only (info error)])
  (:use [ring.adapter.jetty]))

(defn handler [request]
  (info request)
  {:status  200
   :headers {"Content-Type" "text/html"}
   :body    "Hello World"})


