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

(defn sample-table [data owner]
  (reify
    om/IRender
    (render [this]
      (table {:striped? true :bordered? true :condensed? true :hover? true}
             (d/thead
               (d/tr
                 (d/th "#")
                 (d/th "First Name")
                 (d/th "Last Name")
                 (d/th "Username")))
             (d/tbody
               (d/tr
                 (d/td "1")
                 (d/td "Mark")
                 (d/td "Otto")
                 (d/td "@mdo"))
               (d/tr
                 (d/td "2")
                 (d/td "Jacob")
                 (d/td "Thornton")
                 (d/td "@fat"))
               (d/tr
                 (d/td "3")
                 (d/td {:col-span 2} "Larry the Bird")
                 (d/td "@twitter")))))))

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