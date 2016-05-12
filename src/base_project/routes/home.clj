(ns base-project.routes.home
  (:use base-project.server-helpers)
  (:require [compojure.core :refer [defroutes GET POST]]
            [compojure.route :as route]))

(defroutes routes
  (GET "/version" [] (constantly (json-response (read-version))))
  (route/not-found "Not Found"))
