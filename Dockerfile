FROM openjdk:17-jdk-alpine

COPY ./target/restapi-0.0.1-SNAPSHOT.jar restapi.jar

ENTRYPOINT java -jar restapi.jar