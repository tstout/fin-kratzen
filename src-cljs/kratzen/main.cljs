(ns ^:figwheel-always kratzen.main
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [om-bootstrap.table :refer [table]]
            [om-bootstrap.grid :as g]
            [om-bootstrap.button :as b]
            [om-tools.dom :as d :include-macros true]
            [cljs.core.async :refer [put! chan <! pub]]
            [clojure.data :as data]
            [clojure.string :as string]))

(defonce app-state (atom {}))

(defn btn-grp []
  (b/button-group {}
                  (b/button {} "Left")
                  (b/button {} "Middle")
                  (b/button {} "Right")))

;;
(defn grid-example []
  (d/div
    {:class "grids-examples"}
    (g/grid {}
            (g/row {:class "show-grid"}
                   (g/col {:xs 12 :md 8}
                          (d/code {} "(g/col {:xs 12 :md 8})"))
                   (g/col {:xs 6 :md 2}
                          (d/code {} "(g/col {:xs 6 :md 2})")))
            (g/row {:class "show-grid"}
                   (g/col {:xs 6 :md 2}
                          (d/code {} "(g/col {:xs 6 :md 2})"))
                   (g/col {:xs 6 :md 4}
                          (d/code {} "(g/col {:xs 6 :md 4})"))
                   (g/col {:xs 6 :md 4}
                          (d/code {} "(g/col {:xs 6 :md 4})")))
            (g/row {:class "show-grid"}
                   (g/col {:xs 6 :xs-offset 6}
                          (d/code {} "(g/col {:xs 6 :xs-offset 6})")))
            (g/row {:class "show-grid"}
                   (g/col {:md 6 :md-push 6}
                          (d/code {} "(g/col {:md 6 :md-push 6})"))
                   (g/col {:md 6 :md-pull 6}
                          (d/code {} "(g/col {:md 6 :md-push 6})"))))))

(defn table-example []
  (table {:responsive? true}
         (d/thead
           (d/tr
             (d/th "#")
             (repeat 6 (d/th "Table heading")))
           (d/tbody
             (for [i (range 3)]
               (d/tr
                 (d/td (str (inc i)))
                 (repeat 6 (d/td "Table cell"))))))))


(om/root
  (fn [data owner]
    (om/component (table-example)))
  app-state
  {:target (. js/document (getElementById "app"))})