FROM openjdk:11
COPY ./hub.json /app/hub.json
COPY ./config.yml /app/config.yml
COPY ./smart-hub.jar /app/smart-hub.jar
WORKDIR /app
CMD java -jar ./smart-hub.jar server ./config.yml
