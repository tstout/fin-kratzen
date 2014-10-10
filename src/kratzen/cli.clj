(ns kratzen.cli
  (:require [clojure.string :as st])
  (:require [kratzen.server :refer :all])
  (:use [clojure.string :only [replace-first]]))

;;
;; Command line processing.
;;
;; I'm aware of tools.cli, however, I wanted to
;; do this myself as a learning exercise.
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
;; via a configuration map
;;

(defn- show-help []
  (str
    "Run the kratzen server"))

(defn- show-help [])

(def options
  {:server {:value false :cmd run-service}
   :help   {:value false :cmd show-help}})

(defn opt-to-key [opt]
  "transform --opt or -opt to :opt"
  (keyword (replace-first opt #"^(--|-)", "")))

(defn find-opts [opts-with-index]
  (filter #(some? (re-find #"^(--|-)" (first %1))) opts-with-index))

(defn opts-to-keys [opts-with-index]
  "Replace each [--opt index] with [:opt index]"
  (map #(assoc %1 0 (opt-to-key (first %1))) opts-with-index))

(defn opt-val [cmd-index-pair raw-opts opt-cfg]
  (when (get-in opt-cfg [(first cmd-index-pair) :value])
    (nth raw-opts (second cmd-index-pair)))
  nil)

(defn opts-to-cmds [keys-with-index opt-cfg raw-opts]
  "Create a collection of
  [{:cmd cmd-fn :opt opt-value-or-nil} ...]"
  (map
    #(zipmap
      [:cmd :opt]
      [(get-in opt-cfg [(first %1) :cmd]) (opt-val %1 raw-opts opt-cfg)])
    keys-with-index))

(defn add-indices [coll]
  "transform a seq to include an index with each item"
  (map #(vector %1 %2) coll (range)))

(defn parse-args [args opt-cfg]
  (-> (add-indices args)
      (find-opts)
      (opts-to-keys)
      (opts-to-cmds opt-cfg args)))


