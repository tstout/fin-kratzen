(ns ^:figwheel-always kratzen.main
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cljs.core.async :refer [put! chan <! pub]]
            [clojure.data :as data]
            [clojure.string :as string]))
;;
;; This just a simple place holder for the moment. I'm probably going
;; to wait for om-next to be ready before continuing the UI work for this
;; app.
;;
(defonce app-state (atom {:boa-stmts
                          [{:record_created #inst "2015-06-28"
                            :amount         -88.0000M
                            :description    "Check"
                            :posting_date   #inst "2015-06-26"
                            :bank_id        "84492162840-88.00015062625558365.87"},
                           {:record_created #inst "2015-06-28"
                            :amount         -66.7300M
                            :description    "MARKET STREET   06/26 #000742017"
                            :posting_date   #inst "2015-06-26"
                            :bank_id        "00095060626-66.7301506268453.87"}
                           ]}))
