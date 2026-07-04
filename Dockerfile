# Stage 1: Build the application using Maven
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Run the application
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
# This copies the JAR generated in Stage 1 based on your POM configuration
COPY --from=build /app/target/india-travel-api-0.0.1-SNAPSHOT.jar app.jar
# Expose the standard Spring Boot port
EXPOSE 8080
# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]