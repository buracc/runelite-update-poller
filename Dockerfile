#FROM openjdk:17 AS build
#COPY . /home/app
#WORKDIR /home/app
#RUN chmod +x gradlew && ./gradlew bootJar --no-daemon

FROM openjdk:17
COPY build/libs/*.jar app.jar
ENTRYPOINT java -jar app.jar
