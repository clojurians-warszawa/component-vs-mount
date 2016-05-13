(ns component-vs-mount.main
  (:gen-class)
  (:require [clojure.tools.cli :as cli]
            [clojure.tools.nrepl.server :as nrepl]
            [taoensso.encore :refer [parse-int]]
            [cider.nrepl :refer (cider-nrepl-handler)]
            [component-vs-mount.app :as app]
            [component-vs-mount.config :as config]
            [com.stuartsierra.component :as component]
            [component-vs-mount.model.db :as db])
  (:use ring.adapter.jetty
        ring.middleware.params)
  (:import [java.util Locale]))

;; ;;; TODO: remove when switched to component
;; (defonce srv (atom nil))

;; ;;; TODO: remove when switched to component
;; (defn start-server [port]
;;   (when-not @srv
;;     (reset! srv
;;             (run-jetty
;;              (fn [req] (app/main-app req))
;;              {:port port :join? false}))))

(defrecord WebServer [port http-server app-component]
  component/Lifecycle
  (start [component]
         (assoc component :http-server
                (run-jetty
                 (fn [req] ((app/main-app app-component) req))
                 {:port port :join? false})))
  (stop [component]
        (when http-server
          (.stop http-server))
        component))

(defn web-server [port]
  (map->WebServer {:port port}))

(defn ->system [port cfg]
  (component/system-map
   ;; 0. config - managing from db might also be sensible
   ;; 1. first and foremost: database
   :db (db/create-database (:db-file-path cfg))
   ;; 2. handlers depend on database
   :app (component/using
         {}
         {:database :db})
   ;; 3. and server depends on handler
   :server (component/using
            (web-server port)
            {:app-component :app})
   ))

;; any addidtional configuration should go here
(defn- handle-user-supplied-options! [options]
  )

(defn -main [& args]
  (let [[options remaining help]
        (cli/cli args
                 ["--port" "Port to listen on (default 8080)" :default 8080 :parse-fn parse-int]
                 ["--nrepl" "Start a nrepl server"  :flag true :default false]
                 ["--nrepl-port" "Port to start nrepl server on (default 7888)" :default 7888 :parse-fn parse-int]
                 ["--config" "Path to file with configuration in edn format."]
                 ;; add your own cli options here
                 )
        {:keys [port test nrepl nrepl-port config]}
        options]
    (assert (empty? remaining))
    (Locale/setDefault (Locale/US))
    (config/load-default-config!)
    (if config (config/load-config-from-file! config))
    (config/log-config)
    (handle-user-supplied-options! options)
    ;; TODO: modify as to use server from component
    (let [server (-> (->system port @config/cfg ) component/start :server :http-server)]
      (when nrepl (nrepl/start-server :port nrepl-port :handler cider-nrepl-handler))
      (.join server)
      ;; never reached (we hope)
      (System/exit 1))))
