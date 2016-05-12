(ns base-project.app
  (:require [base-project.routes.home :as home-routes]
            [compojure.core :as compojure])
  (:use clojure.test
        ring.middleware.params
        ring.middleware.session
        ring.middleware.file-info
        ring.middleware.json
        ring.middleware.keyword-params
        base-project.server-helpers
        [base-project.middleware :only [custom-middleware]]))

(def wrap-custom-middleware (apply comp custom-middleware))

(def main-app
  (-> (compojure/routes home-routes/routes)
      wrap-params
      wrap-json-params
      wrap-keyword-params
      wrap-log-exceptions
      wrap-custom-middleware))
