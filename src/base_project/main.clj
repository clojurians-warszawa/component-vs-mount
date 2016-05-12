(ns base-project.main
  (:gen-class)
  (:require [clojure.tools.cli :as cli]
            [clojure.tools.nrepl.server :as nrepl]
            [taoensso.encore :refer [parse-int]]
            [cider.nrepl :refer (cider-nrepl-handler)]
            [base-project.app :as app]
            [base-project.config :as config])
  (:use ring.adapter.jetty
        ring.middleware.params)
  (:import [java.util Locale]))

(defonce srv (atom nil))

(defn start-server [port]
  (when-not @srv
    (reset! srv
            (run-jetty
             (fn [req] (app/main-app req))
             {:port port :join? true}))))

(defn- start-thread [f name]
  (.start (Thread. f name)))

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
    (when nrepl (nrepl/start-server :port nrepl-port :handler cider-nrepl-handler))
    (start-server (parse-int port))
    ;; never reached (we hope)
    (System/exit 1)))
