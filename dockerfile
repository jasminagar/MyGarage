FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Kopier den præcise fil, som Maven har bygget (backendPortifolio-1.0-SNAPSHOT.jar)
COPY target/backendPortifolio-1.0-SNAPSHOT.jar app.jar

EXPOSE 7070

# Kør app.jar direkte fra /app mappen (uden skråstreg foran app.jar)
ENTRYPOINT ["java", "-jar", "app.jar"]
