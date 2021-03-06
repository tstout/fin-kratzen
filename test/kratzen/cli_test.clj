(ns kratzen.cli-test
  (:require
    [kratzen.cli :refer :all]
    [expectations :refer [expect]]))

;;
;; verify '--' creates a keyword
;;
(expect
  :option
  (opt-to-key "--option"))

;;
;; verify single - creates a keyword
;;
(expect
  :opt-1
  (opt-to-key "-opt-1"))

;;
;; verify no dashes creates a keyword
;;
(expect
  :opt2
  (opt-to-key "opt2"))

(expect
  [["zero" 0] ["one" 1] ["two" 2]]
  (add-indices ["zero" "one" "two"]))

(expect
  [["--opt1" 0] ["--opt2" 1]]
  (find-opts (add-indices ["--opt1" "--opt2" "foo"])))

(expect
  [[:opt1 0] [:opt2 1]]
  (opts-to-keys (find-opts (add-indices ["--opt1" "--opt2"]))))

;;
;; verify single arg with option value...
;;
(expect
  "start"
  (opt-val [:server 0]
           ["--server" "start"]
           {:server {:cmd :default}}))

;;
;; verify single arg value with option value not at the beginning of option
;; collection
;;
(expect
  "1"
  (opt-val [:baz 2]
           ["--foo" "-bar" "--baz" "1"]
           {:foo {:cmd :default}
            :bar {:cmd :default}
            :baz {:cmd :default}}))

;;
;; verify single arg with no option value...
;;
(expect
  [{:cmd :default :opt nil}]
  (parse-args ["--server"] {:server {:cmd :default}}))
