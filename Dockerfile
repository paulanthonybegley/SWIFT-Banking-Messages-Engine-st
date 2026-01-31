# Stage 1: Build the application
# We use a Maven image to build the project from source
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# Copy the pom.xml and source code
COPY pom.xml .
COPY src ./src

# Build the application using the pre-installed Maven
RUN mvn clean package -DskipTests

# Stage 2: Run the application
# We use a lightweight JRE image for the final container
FROM eclipse-temurin:17-jre
WORKDIR /app

# Copy the jar file from the build stage
COPY --from=build /app/target/*.jar app.jar

# Expose the standard Spring Boot port (usually overridden by PORT env var)
EXPOSE 8080

# Execute the application with memory optimization for small instances (e.g., Koyeb 512MB)
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -Xmx384m -jar app.jar"]
