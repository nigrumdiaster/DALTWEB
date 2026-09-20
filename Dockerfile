# ==============================================================================
# Multi-stage Dockerfile for Spring Boot Application
# ==============================================================================

# Stage 1: Build the JAR package
FROM gradle:8.7-jdk17 AS builder
WORKDIR /app

# Copy gradle configuration files
COPY build.gradle settings.gradle ./
COPY gradle ./gradle

# Copy source code
COPY src ./src

# Build application without running tests during build
RUN gradle bootJar --no-daemon -x test

# Stage 2: Lightweight Runtime Environment
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Add a non-root user for security
RUN groupadd -r spring && useradd -r -g spring spring
USER spring:spring

# Copy built jar from stage 1
COPY --from=builder /app/build/libs/*.jar app.jar

# Expose default Spring Boot port
EXPOSE 8080

# Run Spring Boot application
ENTRYPOINT ["java", "-jar", "-Djava.security.egd=file:/dev/./urandom", "app.jar"]
