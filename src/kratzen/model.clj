(in-ns 'kratzen.core)

;BANK_ID varchar(100) not null,
;POSTING_DATE date not null,
;AMOUNT decimal(19,4),
;RECORD_CREATED datetime default current_timestamp(),
;

;
; Define interface(s) needed by db-io...
; Might be easier just to define this with java...
; Would a protocol have any benefit here...
;
(definterface BoaRecord
              [^String bankId []]
              [^java.sql.Date postingDate []]
              [^java.math.BigDecimal amount []]
              [^java.sql.Timestamp recordCreated []])

;
; TODO
;
(defn read-boa-record [])