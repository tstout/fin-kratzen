(ns kratzen.cli
  (:require [clojure.string :as st])
  (:require [kratzen.system :refer :all])
  (:use [clojure.string :only [replace-first]])
  (:use [clojure.tools.logging :only (info error)]))

;;
;; Command line processing.
;;
;; I'm aware of tools.cli, however, I wanted to
;; do this myself as a learning exercise. It is very
;; naive and needs some work.
;;
;; The goal here is to transform a vector of
;; strings:
;;
;; ["--opt1" "val1" "-opt2"]
;;
;; into a map:
;;
;; {:opt1 val1 :opt2 nil}
;;
;; and then invoke any functions
;; assigned to each option.
;;
;; Options are assigned a function
;; via a configuration map. For example:
;;
;; {:server {:cmd run-server}}
;;
;; defines an option of --server with no option value
;; to run the function named run-server
;;

(defn opt-to-key
  "transform --opt or -opt to :opt"
  [opt]
  (keyword
    (replace-first opt #"^(--|-)", "")))

(defn find-opts
  "filter collection to include any option strings
 option strings start with -- or -"
  [opts-with-index]
  (filter
    #(some? (re-find #"^(--|-)" (first %)))
    opts-with-index))

(defn opts-to-keys
  "Replace each [--opt index] with [:opt index]"
  [opts-with-index]
  (map
    #(assoc % 0 (opt-to-key (first %)))
    opts-with-index))

(defn option?
  "check if arg is a - or -- argument..."
  [arg]
  (some? (re-find #"^(--|-)" arg)))

(defn index-exists? [coll index]
  (> (count coll) index))

(defn opt-val
  "Extract the option value from the option if it is present"
  [cmd-index-pair raw-opts opt-cfg]
  (let [val-index (inc (second cmd-index-pair))]
    (when (index-exists? raw-opts val-index)
      (nth raw-opts val-index))))

(defn opts-to-cmds
  "Create a vector of
 [{:cmd cmd-fn :opt opt-value-or-nil} ...]"
  [keys-with-index opt-cfg raw-opts]
  (map
    #(zipmap
      [:cmd :opt]
      [(get-in opt-cfg [(first %) :cmd]) (opt-val % raw-opts opt-cfg)])
    keys-with-index))

(defn add-indices
  "transform a seq to include an index with each item"
  [coll]
  (map #(vector %1 %2) coll (range)))

(defn parse-args
  "Parse command line args"
  [args opt-cfg]
  (-> (add-indices args)
      (find-opts)
      (opts-to-keys)
      (opts-to-cmds opt-cfg args)))

(defn process-args [args opt-cfg]
  (apply
    #(when (:cmd %)
      (if (:opt %)
        ((:cmd %) (:opt %))
        ((:cmd %))))
    (parse-args args opt-cfg)))
