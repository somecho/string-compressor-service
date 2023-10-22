# String Compressor Service

A simple web application that compresses strings as base64 encoded bytes. 

## Usage

| Endpoint | Method | Description |
|-|-|-|
| `/algorithms` | `GET` | return a list of compression algorithms used by the service |
| `/compress` | `POST` | post a string to get back the compressed base64 encoding |
| `/decompress` | `POST` | post a base64 encoding to get back the decompressed string |

### Compress / Decompress 

Both endpoints have a required URL query param `algorithm`. The `Content-Type` must be set to `text/plain` in the header. The available algorithms are:
- lzma
- gz
- bzip2
- snappy-framed
- deflate
- lz4-frame
- xz

```sh
 curl https://string-compressor.onrender.com/compress\?algorithm=deflate -X POST -H "Content-Type: text/plain" -d "abc"
```

## Gotcha's

The service is hosted as a free-tier Render webservice. If, despite using the API properly, you get `Internal server error`, it's because the webservice has run out of memory. Try again with a different algorithm. 

## Running Locally
Run these commands in the project root.
### Clojure
```sh
clojure -M -m service
```
### Docker
You can build the image locally with `docker build .` and running the service. If you are not familiar with Docker, here's an example workflow:
```sh
docker build -t compression-service .
# wait for image to be done building
docker run -p 3000:3000 -d --rm compression-service
```

The webservice in both cases listens to port `3000`.




