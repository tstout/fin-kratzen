;
; See http://jayfields.com/expectations/index.html for a description of the
; expectations test library.
;
(ns kratzen.core-test
  (:require [kratzen.config :refer :all]
            [expectations :refer [expect-let]]
            [kratzen.core :refer :all]))

(expect-let [sample-config (load-edn-resource ".fin-kratzen-example.clj")
             boa-config (:boa sample-config)]
            {:account  "8675309"
             :routing  "123400088"
             :user     "foo"
             :password "bar"
             :db-user  "sa"
             :db-pass  ""}
            boa-config)


