(ns sandbox
  "For development purposes."
  (:require [service :refer [create-server]]
            [io.pedestal.http :as http]))

(def server (create-server))

(defn restart []
  (http/start server)
  (http/stop server))

;; Evaluate to start and restart server in REPL
(restart)
(http/start server)
