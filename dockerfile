FROM clojure:temurin-21-tools-deps-alpine
WORKDIR /service
COPY deps.edn .
RUN clojure -P
COPY . .
EXPOSE 3000
CMD ["clojure", "-M", "-m", "service"]
