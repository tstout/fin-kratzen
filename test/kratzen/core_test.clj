;
; See http://jayfields.com/expectations/index.html for a description of the
; expectations test library.
;
(ns kratzen.core-test
  (:use [expectations])
  (:require [kratzen.config :refer :all])
  (:require [kratzen.core :refer :all]))

(expect-let [sample-config (load-edn-resource ".fin-kratzen-example.clj")
             boa-config (:boa sample-config)]
  boa-config
  {:account "8675309"
   :routing "123400088"
   :user "foo"
   :password "bar"
   :db-user "sa"
   :db-pass ""})


