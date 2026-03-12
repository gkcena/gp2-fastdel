# ============================================
# Stage 1: Build — compile with Maven
# ============================================
FROM eclipse-temurin:21-jdk AS build

WORKDIR /app

# Copy Maven wrapper and config first (layer caching for dependencies)
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Ensure the Maven wrapper is executable
RUN chmod +x mvnw

# Download dependencies first (cached unless pom.xml changes)
RUN ./mvnw dependency:go-offline -B

# Copy source code
COPY src src

# Build the application (skip tests for faster Docker builds)
RUN ./mvnw package -DskipTests -B

# ============================================
# Stage 2: Run — lightweight JRE image
# ============================================
FROM eclipse-temurin:21-jre

WORKDIR /app

# Copy the fat JAR from build stage
COPY --from=build /app/target/backend-0.0.1-SNAPSHOT.jar app.jar

# Expose the default Spring Boot port
EXPOSE 8080

# Run the application with container-aware JVM settings
ENTRYPOINT ["java", "-jar", "app.jar"]
