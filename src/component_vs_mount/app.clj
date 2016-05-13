(ns component-vs-mount.app
  (:require [component-vs-mount.routes.home :as home-routes]
            [compojure.core :as compojure])
  (:use clojure.test
        ring.middleware.params
        ring.middleware.session
        ring.middleware.file-info
        ring.middleware.json
        ring.middleware.keyword-params
        component-vs-mount.server-helpers
        [component-vs-mount.middleware :only [custom-middleware]]))

(def wrap-custom-middleware (apply comp custom-middleware))

;;; TODO: add passing of component
#_(def main-app
  (-> (compojure/routes home-routes/routes)
      wrap-params
      wrap-json-params
      wrap-keyword-params
      wrap-log-exceptions
      wrap-custom-middleware))

(defn main-app [app-component]
  (-> (compojure/routes (home-routes/routes app-component))
      wrap-params
      wrap-json-params
      wrap-keyword-params
      wrap-log-exceptions
      wrap-custom-middleware))
