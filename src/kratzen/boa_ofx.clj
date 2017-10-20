(ns kratzen.boa-ofx
  (:require [clj-http.client :as http]
            [clj-time.format :as tf]
            [clj-time.core :as t]
            [kratzen.dates :refer [days-ago]]
            [kratzen.config :as cfg]
            [clojure.java.io :as io])
  (:import (org.joda.time DateTimeZone)
           (java.util UUID)
           (net.sf.ofx4j.domain.data ResponseEnvelope MessageSetType)
           (net.sf.ofx4j.io AggregateUnmarshaller)))

(def req-template
  "
  OFXHEADER:100
  DATA:OFXSGML
  VERSION:103
  SECURITY:NONE
  ENCODING:USASCII
  CHARSET:1252
  COMPRESSION:NONE
  OLDFILEUID:NONE
  NEWFILEUID:$newfile-id

  <OFX>
     <SIGNONMSGSRQV1>
  <SONRQ>
  <DTCLIENT>$dtclient
  <USERID>$user
  <USERPASS>$pass
  <GENUSERKEY>N
  <LANGUAGE>ENG
  <FI>
  <ORG>HAN
  <FID>5959
  </FI>
  <APPID>QWIN
  <APPVER>2400
  <CLIENTUID>$client-uid
  </SONRQ>
  </SIGNONMSGSRQV1>

    <BANKMSGSRQV1>
      <STMTTRNRQ>
        <TRNUID>$trn-uid</TRNUID>
        <STMTRQ>
          <BANKACCTFROM>
            <BANKID>$routing</BANKID>
            <ACCTID>$account</ACCTID>
            <ACCTTYPE>CHECKING</ACCTTYPE>
          </BANKACCTFROM>
          <INCTRAN>
            <DTSTART>$dt-start</DTSTART>
            <DTEND>$dt-end</DTEND>
            <INCLUDE>Y</INCLUDE>
          </INCTRAN>
        </STMTRQ>
      </STMTTRNRQ>
    </BANKMSGSRQV1>
   </OFX>")

(def time-fmt
  {:dtclient  "yyyyMMddHHmmss.SSS"
   :start-end "yyyyMMddHHmmss"})

(defn fmt-date [d fmt-type]
  (->
    fmt-type
    time-fmt
    tf/formatter
    (tf/unparse d)))

(defn t-days-ago [day-offset fmt-type]
  (fmt-date
    (-> day-offset t/days t/ago)
    fmt-type))

(defn ofx-date-range [m days-ago]
  (merge m {:dtstart (t-days-ago days-ago :start-end)
            :dtend   (t-days-ago 0 :start-end)}))

(defn mk-pairs
  "Transform a map into a vector of key value pairs.
  For example: {:a 1 :b 2} becomes [[:a 1] [:b 2]]"
  [m]
  (for [[k v] m] [k v]))

(defn txt-from-template
  "Given a map of parameters, treat the key as a variable
  name and the value as a replacement value to be applied to
  an arbitrary text template"
  [parms template]
  (->>
    parms
    mk-pairs
    (reduce
      (fn [t kv] (let [[k v] kv] (.replace t k v)))
      template)))

;(defn mk-creds [m]
;  (merge m
;         (select-keys
;           (cfg/creds)
;           [user pass account routing client-id])))

(defn build-req
  "Create an OFX SGML request based on the supplied map of parameters."
  [parms]
  (let [{:keys [dtstart
                dtend
                client-id
                routing
                account
                user
                pass]} parms
        uuid (str (UUID/randomUUID))]
    (->
      {"$newfile-id" uuid
       "$dtclient"   (fmt-date (t/now) :dtclient)
       "$trn-uid"    uuid
       "$user"       user
       "$pass"       pass
       "$routing"    routing
       "$account"    account
       "$client-uid" client-id
       "$dt-start"   dtstart
       "$dt-end"     dtend}
      (txt-from-template req-template))))

(defn fetch-records [parms]
  (http/post "https://eftx.bankofamerica.com/eftxweb/access.ofx"
             {:body    (build-req parms)
              :headers {"Content-Type" "application/x-ofx"
                        "Accept"       "*/*,application/x-ofx"
                        "User-Agent"   "InetClntApp/3.0"}}))

(defn fetch-trn
  "Fetch transactions for current date - days.
  Returns a raw OFX SGML response."
  [days]
  (->>
    days
    (ofx-date-range {})
    (merge (cfg/creds))
    fetch-records
    :body))

(defn read-response
  "Use the OFX4J library to parse the SGML response"
  [resp]
  (with-open [data (io/input-stream (.getBytes resp))]
    (->
      (.unmarshal (AggregateUnmarshaller. ResponseEnvelope) data)
      (.getMessageSet MessageSetType/banking)
      (.getStatementResponses))))

;; getAvailableBalance at same level as getTransactionList contains
;; balance

(defn extract-trans
  "Grab the transactions from the ofx java objects."
  [m]
  (merge m
         {:transactions
          (->>
            (m :bank-trans)
            (.getTransactionList)
            (.getTransactions)
            (map bean))}))

(defn extract-balance
  "Grab the balance from the ofx java object"
  [m]
  (merge m {:balance
            (->
              (m :bank-trans)
              (.getAvailableBalance)
              bean)}))

(defn query-boa [days]
  (->>
    days
    fetch-trn
    read-response
    first
    (.getMessage)
    (assoc {} :bank-trans)
    extract-trans
    extract-balance))