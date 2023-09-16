# First stage: Prepare the environment with Python 3
FROM openjdk:17.0.1-jdk-slim AS python

# Second stage: Build your Java application
FROM maven:3.8.5-openjdk-17 AS build
COPY . .
RUN mvn clean package -DskipTests

# Third stage: Set up the runtime environment
FROM openjdk:17.0.1-jdk-slim
COPY --from=build /target/YoutubeConverter-0.0.1-SNAPSHOT.jar YoutubeConverter.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "YoutubeConverter.jar"]
