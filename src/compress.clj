(ns compress
  (:require [clj-compress.core :as c])
  (:import (java.io ByteArrayOutputStream)
           java.util.Base64))

(defn compress [input-string encoding]
  (let [input-bytes (.getBytes input-string)
        output-buffer (ByteArrayOutputStream.)]
    (c/compress-data input-bytes output-buffer encoding)
    (.encodeToString (Base64/getEncoder) (.toByteArray output-buffer))))

(defn decompress [input-string encoding]
  (let [input-bytes (.decode (Base64/getDecoder) input-string)
        output-buffer (ByteArrayOutputStream.)]
    (c/decompress-data input-bytes output-buffer encoding)
    (.toString output-buffer)))
