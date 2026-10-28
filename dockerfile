FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

COPY target/backendPortifolio-1.0-SNAPSHOT.jar app.jar

# Expose the port your app runs on
EXPOSE 7070

# Command to run your app
CMD ["java", "-jar", "/app.jar"]