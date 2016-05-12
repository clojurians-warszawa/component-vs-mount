(ns base-project.server-helpers
  (:require [clojure.tools.logging :as log]
            [clojure.data.json :as json]
            [clojure.java.io :as io]
            [ring.util.response :as response])
  (:use ring.middleware.session
        [slingshot.slingshot :only [throw+ try+]]
        clojure.test))

(defn json-response [body]
  {:status 200
   :headers {"Content-Type" "application/json; charset=utf-8"}
   :body (json/json-str body)})

(defn error [msg]
  {:status 500
   :headers {"Content-Type" "application/json; charset=utf-8"}
   :body msg})

(defn error-404 [msg]
  {:status 404
   :headers {"Content-Type" "application/json; charset=utf-8"}
   :body msg})

(defn error!
  "Can be thrown anywhere. Throws an exception that gets caught by wrap-log-exceptions and is returned using error."  [msg]
  (throw+ msg))

(defn read-version []
  (slurp (io/resource "version.txt")))

(defn wrap-file-response [file-path]
  (fn [req] (response/file-response file-path)))

(defn wrap-log-exceptions
  [app]
  (fn [req]
    (try+
     (app req)
     (catch string? e
       (log/error (format "Exception (String) encountered: returning %s " e))
       (error e))
     (catch map? e
       (log/error (format "Exception (map) encountered: returning %s " e))
       (error (json/write-str e)))
     (catch Throwable e
       (log/error (format "Exception (Throwable) of %s encountered: %s" (class e) (.getMessage ^Throwable e)))
       (.printStackTrace ^Throwable e)
       (error (pr-str (.getMessage ^Throwable e))))
     (catch Object o
       (log/error (format "Exception (Object) of %s encountered: %s" (class o) (.toString ^Object o)))
       (error (pr-str (.toString ^Object o)))))))
