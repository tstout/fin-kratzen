(ns ^:figwheel-always kratzen.main
  (:require[om.core :as om :include-macros true]
           [om.dom :as dom :include-macros true]))

;; TODO - add stuff---
(enable-console-print!)

(println "Edits to this text should show up in your developer console.")

;; define your app data so that it doesn't get over-written on reload

(defonce app-state (atom {:text "Some Custom Text3!"}))

(om/root
  (fn [data owner]
    (reify om/IRender
      (render [_]
        (dom/p nil (:text data)))))
  app-state
  {:target (. js/document (getElementById "app"))})


(defn on-js-reload []
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
  )
