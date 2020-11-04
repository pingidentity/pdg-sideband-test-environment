FROM maven:3.6.3-openjdk-11 AS build-env

COPY . /app
WORKDIR /app
RUN mvn clean package

FROM openjdk:11
WORKDIR /app
COPY --from=build-env /app /app/
CMD java -jar target/smart-hub-application-1.0.0-SNAPSHOT.jar server config.yml
