(ns ^:figwheel-always kratzen.main
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [om-bootstrap.table :refer [table]]
            [om-bootstrap.grid :as g]
            [om-bootstrap.button :as b]
            [om-tools.dom :as d :include-macros true]
            [cljs.core.async :refer [put! chan <! pub]]
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

(defn stmt-view [{:keys [posting_date description amount]} owner]
  (reify
    om/IRender
    (render [this]
      (d/tr
        (d/td (str posting_date))
        (d/td description)
        (d/td amount)))))

(defn stmts-view [data owner]
  (reify
    om/IRender
    (render [this]
      (table {:striped? true :bordered? true :condensed? true :hover? true}
             (d/thead
               (d/tr
                 (d/th "Posting Date")
                 (d/th "Description")
                 (d/th "Amount")))
             (d/tbody
               (om/build-all stmt-view (:boa-stmts data)))))))

(om/root stmts-view app-state
         {:target (. js/document (getElementById "app"))})