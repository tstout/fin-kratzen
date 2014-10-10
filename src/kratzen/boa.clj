(ns kratzen.boa
  (:import (ofx.client BoaData Retriever)))


;List<Transaction> transactions =
;new Retriever (new BoaData (),
;                  BoaData.CONTEXT,
;                  Credentials.fromProperties(".boa-creds.properties"))
;.fetch(newDate("2014/05/01"), newDate("2014/06/01"));

(defn load-boa-creds []

)



(defn fetch-boa-trans
  "Load transactions from BOA checking for the specified date range"
  [start end]
  ;(let [creds
  ;       retriever (Retriever. BoaData/CONTEXT, creds)]
  ;  )
)
