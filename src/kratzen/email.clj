(ns kratzen.email
  (:require [kratzen.config :refer [load-edn-resource load-config]]
            [kratzen.reports :refer [mk-weekly-summary boa-recent-stmts]]
            [kratzen.boa :refer [balance]]
            [com.stuartsierra.component :as component]
            [clj-time.core :as t]
            [clojure.core.async :refer [close!]]
            [kratzen.scheduler :refer [task]]
            [clojure.pprint :refer [pprint print-table]]
            [clojure.tools.logging :as log]
            [kratzen.dates :refer [tm-format]]
            [postal.core :refer [send-message]]
            [kratzen.dates :refer [every-day-at
                                   every-x-minutes]]))

(defn daily-summary-template [totals txns]
  (str
    (format "Financial Summary for Today, %s\n"
            (tm-format (t/now)))
    (with-out-str
      (print-table totals)
      (print-table txns))))

(defn mk-summary-email [summary]
  (let [{:keys [credits debits]} summary]
    (daily-summary-template
      [{:balance (balance)
        :credits credits
        :debits  debits}]
      (boa-recent-stmts))))

(defn send-email [body]
  (let [{:keys [user pass]} (:email (load-config))]
    (send-message {:host "smtp.gmail.com"
                   :user user
                   :pass pass
                   :ssl  true}
                  {:from    "fin-kratzen"
                   :to      user
                   :subject "fin-kratzen summary"
                   :body    body})))

(defn send-daily-summary []
  (log/info "sending daily email summary...")
  (->
    (mk-weekly-summary)
    mk-summary-email
    send-email))

(defrecord Email []
  component/Lifecycle

  (start [this]
    (log/info "starting Email task...")
    (assoc this :email (task
                         (every-day-at 6)
                         (fn [_] (send-daily-summary)))))

  (stop [this]
    (log/infof "Stopping Email task ...")
    (when-let [email-ch (:email this)]
      (close! email-ch))
    (assoc this :email nil)))



