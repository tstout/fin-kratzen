(ns kratzen.model-test
  (:import (java.util HashMap))
  (:use [expectations])
  (:require [kratzen.model :refer :all]))

(expect-let
  [jmap (HashMap. {"a" "value-for-a"})
   cmap {:a "value-for-a"}]
  (to-clj-map jmap)
  cmap)

(expect-fn)