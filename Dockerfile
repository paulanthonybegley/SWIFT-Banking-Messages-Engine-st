# Stage 1: Build the application
# We use a Maven image to build the project from source
FROM maven:3.9-eclipse-temurin-17-alpine AS build
WORKDIR /app

# Copy the pom.xml and source code
COPY pom.xml .
COPY src ./src
COPY .mvn ./.mvn
COPY mvnw ./mvnw

# Fix line endings for mvnw in case of development on Windows
RUN chmod +x mvnw && ./mvnw clean package -DskipTests

# Stage 2: Run the application
# We use a lightweight JRE image for the final container
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copy the jar file from the build stage
COPY --from=build /app/target/*.jar app.jar

# Expose the standard Spring Boot port
EXPOSE 8080

# Execute the application
ENTRYPOINT ["java", "-jar", "app.jar"]
