;
; Database related stuff...
;
(in-ns 'kratzen.core)

(def init-schema
  (load-res "init-schema.sql"))

;
; Pull DB user and pass from boa config
;
(def db-config
  (let [cfg (load-config)
        boa (cfg :boa)]
    (zipmap [:user :pass] [(boa :db-user) (boa :db-pass)])))

(def db-creds
  ;
  ; fin-kratzen - db name
  ; directory - db
  ;
  (H2Credentials/h2LocalServerCreds "fin-kratzen" "db"))

;
; Startup local H2 in server mode
;
(defn start-h2 []
  (info "starting h2...")
  (let [h2Server (Server/createTcpServer (into-array String []))]
    (.start h2Server)))

;
; Create a db.io.Migtrator
;
(defn mk-migrator []
  (Migrators/liquibase (H2Db.) db-creds))

