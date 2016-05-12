(ns base-project.middleware
    (:require [clojure.tools.logging :as log]))

;;; FIXME: custom handlers
(defn test-handler [app]
  (fn [req]
   (log/info "Test")
   (app req)))

;;; here handlers must be declared
(def custom-middleware [test-handler])


