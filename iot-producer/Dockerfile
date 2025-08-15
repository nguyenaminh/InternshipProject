FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
# Install curl for testing
RUN apk add --no-cache curl
COPY target/iot-producer-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
