# Use Java 21 base image
FROM eclipse-temurin:21-jdk-alpine

# Set working directory
WORKDIR /app

# Copy Maven wrapper and pom.xml first (for caching dependencies)
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Make Maven wrapper executable
RUN chmod +x mvnw

# Download dependencies offline (caching step)
RUN ./mvnw dependency:go-offline

# Copy the source code
COPY src ./src

# Build the application (skip tests to speed up)
RUN ./mvnw clean package -DskipTests

# Expose the port Spring Boot runs on
EXPOSE 8080

# Run the JAR dynamically (no hardcoded version)
CMD java -jar target/*.jar
