# Base image with Java runtime
FROM maven:3.9-eclipse-temurin-17

# Install Docker CLI inside this image
RUN apt-get update && apt-get install -y docker.io

# Copy built JAR from local to image
COPY target/*.jar app.jar

# Run the app
ENTRYPOINT ["java", "-jar", "app.jar"]
