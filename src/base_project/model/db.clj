(ns base-project.model.db
  (:require korma.db
            [korma.core :as korma]
            [clojure.java.jdbc :as jdbc]))

(def db (korma.db/h2 {:db "resources/db/temp.db"}))

(defn setup-db! [conn]
  (jdbc/db-do-commands conn
                         (jdbc/create-table-ddl :test_table
                                                [:id "int primary key"]
                                                [:value "varchar(255)"]))
  (jdbc/insert! conn :test_table
                {:id 1, :value "Wartość testowa"}))

(defn unsetup-db! [conn]
  (jdbc/db-do-commands conn
                       (jdbc/drop-table-ddl :test_table)))

(defn get-test-table-values [conn]
  (jdbc/query conn ["select * from test_table"]))
