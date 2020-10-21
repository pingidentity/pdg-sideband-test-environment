FROM maven:3.6.3-openjdk-11

COPY . /app
WORKDIR /app
RUN mvn clean package

# TODO: Use an intermediate image for compilation to improve build-time performance and decrease image output size.
CMD java -jar target/backend-rest-application-1.0.0-SNAPSHOT.jar server config.yml
