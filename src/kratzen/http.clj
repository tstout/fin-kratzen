(ns kratzen.http
  (:use [clojure.tools.logging :as log])
  (:use [ring.adapter.jetty])
  (:import (com.stuartsierra.component Lifecycle)
           (org.joda.time.format PeriodFormatterBuilder)
           (org.joda.time Period))
  (:require [clj-time.core :as t]
            [clojure.data.json :as json]))

(def ^:private start-time
  (t/now))

(defn uptime []
  (-> (PeriodFormatterBuilder.)
    (.appendDays)
    (.appendSuffix " d")
    (.appendHours)
    (.appendSuffix " h")
    (.appendMinutes)
    (.appendSuffix " m ")
    (.appendSeconds)
    (.appendSuffix " s")
    (.printZeroNever)
    (.toFormatter)
    (.print (Period. start-time (t/now)))))

(defn uptime-in-seconds []
  (str
    (t/in-seconds
      (t/interval start-time (t/now)))
    "s"))

(def routes
  {"/about" "about"
   "/ping"  "ping"
   })

(defn mk-response
  "create a ring response map"
  ([body]
   (mk-response body 200 {"Content-Type" "text/html"}))

  ([body status]
   (mk-response body status {"Content-Type" "text/html"}))

  ([body status headers]
   {:status  status
    :headers headers
    :body    body}))

(defmulti controller
          (fn [uri]
            (routes uri)))

(defmethod controller "ping" [_]
  (mk-response (json/write-str {:uptime (uptime)
                                :uptime-in-s (uptime-in-seconds)})))

(defmethod controller :default [_]
  (mk-response "404: Not Found" 404))

(defmethod controller "about" [_]
  (mk-response "Not Yet Implemented\n"))

(defn handler [request]
  (let [uri (:uri request)]
    (log/infof "URI: %s" uri)
    (controller (:uri request))))

(defrecord Http []
  Lifecycle

  (start [component]
    (log/info "starting http...")
    (assoc component :http (run-jetty handler {:port 3000 :join? false})))

  (stop [component]
    (log/info "stopping http...")
    (.stop (:http component))
    (assoc component :http nil)))
