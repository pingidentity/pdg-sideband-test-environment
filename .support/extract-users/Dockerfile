FROM alpine:3.12.0

RUN apk add jq
COPY . /app
WORKDIR /app
CMD sh run.sh hub.json
