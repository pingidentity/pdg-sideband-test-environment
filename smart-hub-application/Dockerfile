FROM maven:3.6.3-openjdk-11 AS build-env

COPY . /app
WORKDIR /app
RUN mvn clean package -Prun -DskipTests

FROM openjdk:11
COPY ./hub.json /app/hub.json
COPY ./config.yml /app/config.yml
COPY --from=build-env /app/target/smart-hub-application-1.0.0-SNAPSHOT.jar /app/smart-hub.jar
WORKDIR /app
CMD java -jar ./smart-hub.jar server ./config.yml
