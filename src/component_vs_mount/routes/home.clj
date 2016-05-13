(ns component-vs-mount.routes.home
  (:use component-vs-mount.server-helpers)
  (:require [compojure.core :as compojure :refer [defroutes GET POST]]
            [compojure.route :as route]
            [component-vs-mount.model.db :as db]))

;;; TODO: replace with component version
;; (defroutes routes
;;   (GET "/version" [] (constantly (json-response (read-version))))
;;   (GET "/test-db" [] (fn [req] (json-response (db/get-test-table-values))))
;;   (route/not-found "Not Found"))

(defn test-db-handler [{:keys [database] :as app-component} req]
  (json-response "bla" #_(db/get-test-table-values database)))

(defn routes [app-component]
  (compojure/routes
   (GET "/version" [] (constantly (json-response (read-version))))
   (GET "/test-db" [] (partial test-db-handler app-component))
   (route/not-found "Not Found")))
