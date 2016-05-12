(ns component-vs-mount.model.db
  (:require korma.db
            [korma.core :as korma]
            [clojure.java.jdbc :as jdbc]
            [clojure.tools.logging :as log]
            [com.stuartsierra.component :as component]))

;;; TODO: move to component
(def db (korma.db/h2 {:db "resources/db/temp.db"}))

;;; test helpers

(defn setup-db! []
    (jdbc/db-do-commands db
                         (jdbc/create-table-ddl :test_table
                                                [:id "int primary key"]
                                                [:value "varchar(255)"]))
    (jdbc/insert! db :test_table
                  {:id 1, :value "Wartość testowa"}))

;;; new version, customized to use component
#_(defn setup-db! [{:keys [db-spec] :as database}]
    (jdbc/db-do-commands db-spec
                         (jdbc/create-table-ddl :test_table
                                                [:id "int primary key"]
                                                [:value "varchar(255)"]))
    (jdbc/insert! db-spec :test_table
                  {:id 1, :value "Wartość testowa"}))

(defn unsetup-db! []
  (jdbc/db-do-commands db
                       (jdbc/drop-table-ddl :test_table)))

;;; new version, customized to use component
#_(defn unsetup-db! [{:keys [db-spec] :as database}]
  (jdbc/db-do-commands db-spec
                       (jdbc/drop-table-ddl :test_table)))

;;; database connection must be created here
(defrecord Database [db-file-path db-spec]
  component/Lifecycle
  (start [component]
         (log/info (format "Starting database, file path: %s" db-file-path))
         (let [db (korma.db/h2 {:db db-file-path})
               db-connection (jdbc/get-connection db)
               db-spec-with-connection (assoc db :connection db-connection)])
         ;; TODO: add connection to component
         )
  (stop [component]
        (log/info "Closing database")
        ;; TODO: retrieve connection
        ;; close it using (.close connection) method
        ;; clear association in component
        ))

(defn create-database [db-file-path]
  (map->Database {:db-file-path db-file-path}))

;;; actual database-operating functions

;;; TODO: replace with component version
(defn get-test-table-values []
  (jdbc/query db ["select * from test_table"]))

#_(defn get-test-table-values [{:keys [db-spec] :as database}]
  (jdbc/query db-spec ["select * from test_table"]))
