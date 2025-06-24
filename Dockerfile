# Base image with Java runtime
FROM eclipse-temurin:17-jre

# Copy built JAR from local to image
COPY target/*.jar app.jar

# Run the app
ENTRYPOINT ["java", "-jar", "app.jar"]
