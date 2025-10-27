# Multi-stage build for optimized image size

# Stage 1: Build Frontend
FROM node:20-alpine AS frontend-build
WORKDIR /frontend

# Copy frontend package files
COPY frontend/package*.json ./

# Install dependencies
RUN npm install

# Copy all frontend files (including public/ folder)
COPY frontend/src ./src
COPY frontend/public ./public
COPY frontend/index.html ./
COPY frontend/vite.config.js ./
COPY frontend/eslint.config.js ./

# Build frontend
RUN npm run build

# Stage 2: Build Backend
FROM eclipse-temurin:21-jdk-alpine AS backend-build
WORKDIR /app

# Copy Maven wrapper and pom.xml
COPY cheaterbuster/mvnw .
COPY cheaterbuster/.mvn .mvn
COPY cheaterbuster/pom.xml .

# Download dependencies (cached layer)
RUN ./mvnw dependency:go-offline

# Copy source code
COPY cheaterbuster/src src

# Copy frontend build into Spring Boot static resources
COPY --from=frontend-build /frontend/dist src/main/resources/static/

# Build the application
RUN ./mvnw clean package -DskipTests

# Stage 3: Runtime
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copy the built JAR from build stage
COPY --from=backend-build /app/target/cheaterbuster-*.jar app.jar

# Expose port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]