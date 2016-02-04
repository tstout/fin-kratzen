(ns kratzen.email
  (:require [kratzen.config :refer [load-edn-resource load-config]]
            [kratzen.reports :refer [mk-weekly-summary]]
            [kratzen.boa :refer [balance]]
            [kratzen.dates :refer [every-day-at
                                   every-x-minutes]]
            [com.stuartsierra.component :as component]
            [clj-time.core :as t]
            [clojure.core.async :refer [close!]]
            [kratzen.scheduler :refer [task]]
            [clojure.tools.logging :as log]
            [kratzen.dates :refer [tm-format]]
            [postal.core :refer [send-message]]))

(defn daily-summary-template []
  (str
    (format "Financial Summary for Today, %s\n"
            (tm-format (t/now)))
    "-------------------------------------------------
  Credits:         %-20s
  Debits:          %-20s
  Current Balance: %-20s"))

(defn mk-summary-email [summary]
  (let [{:keys [credits debits]} summary]
    (format (daily-summary-template)
            credits
            debits
            (balance))))

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
                         (every-x-minutes 2)
                         (fn [_] send-daily-summary)))
    (log/info "Email task started!"))

  (stop [this]
    (log/infof "Stopping Email task ...")
    (when-let [email-ch (:email this)]
      (close! email-ch))
    (assoc this :email nil)))



