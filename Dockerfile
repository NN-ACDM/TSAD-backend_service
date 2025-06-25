# Use Maven + Java 17 base image
FROM maven:3.9-eclipse-temurin-17

# Run as root
USER root

# Install Docker CLI (optional for inner-Docker use)
RUN apt-get update && apt-get install -y docker.io

# Copy built JAR
COPY target/*.jar app.jar

# create app configuration directory
RUN mkdir -p /app/config
WORKDIR /app

# Run the JAR and include config path
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.config.additional-location=optional:file:./config/"]
