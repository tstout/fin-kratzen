;
; Database related stuff...
;
(in-ns 'kratzen.core)

(def init-schema
  (clojure.java.io/resource "init-schema.sql"))
;
; Pull DB user and pass form boa config
;
(def db-config
  (let [cfg (load-config)
        boa (cfg :boa)]
    (zipmap [:user :pass] [(boa :db-user) (boa :db-pass)])))

;
; Startup local H2 in server mode
;
(defn start-h2 []
  (info "starting h2...")
  (let [h2Server (Server/createTcpServer (into-array String []))]
    (.start h2Server)))


;eventBus.post(new DbAvailableEvent());
;logger.info("H2 Started -- Status:%s", h2Server.getStatus());)
;
; Create a db.io.Migtrator
;
(defn mk-migrator []
  (let [creds (H2Credentials/h2LocalServerCreds ".fin-kratzen" "boa")]
        (Migrators/liquibase (H2Db.) creds)))
;ladjkfa
