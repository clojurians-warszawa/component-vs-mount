(ns base-project.config
  (:require [clojure.java.io :as io]
            [clojure.edn :as edn]
            [clojure.tools.logging :as log])
  (:use [slingshot.slingshot :only [throw+ try+]]))

(defonce cfg (atom nil))

(defn load-config-from-file! [f]
  (let [config-file (io/file f)]
   (if (.exists config-file)
     (let [config-map (edn/read-string (slurp config-file))]
       (if (map? config-map)
         (swap! cfg merge config-map)
         (throw+ "Read config is not map.")))
     (throw+ (format "Config file %s does not exist." config-file)))))

(defn load-default-config! []
  (reset! cfg {:log-api-requests false})) ; place for extending defaults

(defn get-config-var [arg]
  (@cfg arg))

(defn set-config-var [arg value]
  (swap! cfg assoc arg value))

(defn log-config []
  (log/info (format "Startup configuration: %s" @cfg)))
