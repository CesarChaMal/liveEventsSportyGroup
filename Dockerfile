# Use Maven to build the backend
FROM maven:3.8.6-openjdk-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Use OpenJDK runtime
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=0 /app/target/live-events-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
