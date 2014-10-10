(ns kratzen.cli-test
  (:use [expectations])
  (:require [kratzen.cli :refer :all]))

;;
;; verify '--' creates a keyword
;;
(expect
  (opt-to-key "--option")
  :option)

;;
;; verify single - creates a keyword
;;
(expect
  (opt-to-key "-opt-1")
  :opt-1)

;;
;; verify no dashes creates a keyword
;;
(expect
  (opt-to-key "opt2")
  :opt2)

(expect
  (add-indices ["zero" "one" "two"])
  [["zero" 0] ["one" 1] ["two" 2]])

(expect
  (find-opts (add-indices ["--opt1" "--opt2"]))
  [["--opt1" 0] ["--opt2" 1]])

(expect
  (opts-to-keys (find-opts (add-indices ["--opt1" "--opt2"])))
  [[:opt1 0] [:opt2 1]])

