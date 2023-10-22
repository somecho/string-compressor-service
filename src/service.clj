(ns service
  (:require [io.pedestal.http :as http]
            [io.pedestal.http.route :as route]
            [clj-compress.core :as c]
            [compress :refer [compress
                              decompress]]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; REPONSES
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn ok [body type]
  {:status 200
   :headers {"Content-Type" type}
   :body body})

(defn unsupported-media-response [type]
  {:status 415
   :headers {"Content-Type" "text/plain"}
   :body (format "Unsupported media type: %s\nOnly text/plain type allowed"
                 type)})

(defn invalid-algorithm-response [algo]
  {:status 415
   :headers {"Content-Type" "text/plain"}
   :body (format "Invalid algorithm: %s\nFor a list of valid algorithms, go to /algorithms"
                 algo)})

(def missing-algorithm
  {:status 400
   :headers {"Content-Type" "text/plain"}
   :body "Algorithm query parameter missing. For a list of valid algorithms, go to /algorithms"})

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; INTERCEPTORS
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def content-intc
  {:name ::content-interceptor
   :enter (fn [context]
            (let [req (:request context)
                  type (:content-type req)]
              (if (= type "text/plain")
                context
                (assoc context :response (unsupported-media-response type)))))})

(def algorithms-intc
  {:name ::algorithms-interceptor
   :enter (fn [context]
            (let [algorithm (get-in context [:request
                                             :query-params
                                             :algorithm])]
              (cond
                (nil? algorithm)
                (assoc context :response missing-algorithm)
                (nil? (some #{algorithm} c/compressors))
                (assoc context :response (invalid-algorithm-response algorithm))
                :else context)))})

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; HANDLERS
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn compress-handler
  [{:keys [body query-params]}]
  (let [encoding (:algorithm query-params)
        compressed (compress (slurp body) encoding)]
    (ok compressed "text/plain")))

(defn decompress-handler
  [{:keys [body query-params]}]
  (let [algorithm (:algorithm query-params)
        decompressed (decompress (slurp body) algorithm)]
    (ok decompressed "text/plain")))

(defn algorithms-handler [_]
  (ok c/compressors "application/json"))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; SERVER
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def routes
  #{["/compress" :post [`content-intc
                        `algorithms-intc
                        `compress-handler]]
    ["/decompress" :post [`content-intc
                          `algorithms-intc
                          `decompress-handler]]
    ["/algorithms" :get `algorithms-handler]})

(defn create-server []
  (http/create-server
   {::http/routes #(route/expand-routes routes)
    ::http/type :jetty
    ::http/host "0.0.0.0"
    ::http/port 3000}))

(defn -main []
  (http/start (create-server)))
