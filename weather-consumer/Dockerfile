FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY target/weather-consumer-0.0.1-SNAPSHOT.jar app.jar

# Install curl for healthchecks
RUN apk add --no-cache curl

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
