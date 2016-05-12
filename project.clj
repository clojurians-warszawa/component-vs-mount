(defproject component-vs-mount "unused"
  :description "FIXME: write description"
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [org.clojure/tools.logging "0.2.6"]
                 [org.clojure/data.json "0.2.4"]
                 [org.clojure/tools.cli "0.3.1"]
                 [slingshot "0.10.3"]
                 [ring "1.2.2"]
                 [compojure "1.5.0"]
                 [lib-noir "0.8.3"]
                 [log4j
                  "1.2.17"
                  :exclusions
                  [javax.mail/mail
                   javax.jms/jms
                   com.sun.jdmk/jmxtools
                   com.sun.jmx/jmxri]]
                 [commons-io/commons-io "2.4"]
                 [commons-codec "1.9"]
                 [org.clojure/tools.nrepl "0.2.5"]
                 [ring/ring-json "0.3.1"]
                 [com.taoensso/encore "1.6.0"]
                 ;; database handling
                 [korma "0.4.0"]
                 [com.h2database/h2 "1.3.170"]
                 [org.clojure/java.jdbc "0.3.5"]
                 [com.stuartsierra/component "0.3.1"]]
  :repl-options {:init-ns user}
  :plugins [[lein-git-version "0.0.10"]
            [cider/cider-nrepl "0.9.1"]]
  :global-vars {*warn-on-reflection* true}
  :hooks [leiningen.hooks.git-version]
  :main component-vs-mount.main
  :profiles {:uberjar {:aot :all}
             :production {:ring {:open-browser? false
                                 :stacktraces?  false
                                 :auto-reload?  false}}
             :dev {:dependencies [[org.clojure/tools.namespace "0.2.11"]]
                   :source-paths ["dev" "src"]}}
  :min-lein-version "2.0.0")
