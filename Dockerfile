# ---- Stage 1: build the Angular frontend ----
FROM node:22-alpine AS frontend
WORKDIR /app/frontend
COPY frontend/package*.json ./
RUN npm ci
COPY frontend/ ./
RUN npm run build

# ---- Stage 2: build the Spring Boot jar with the frontend baked into static/ ----
FROM eclipse-temurin:17-jdk AS backend
WORKDIR /app
COPY gradlew ./
COPY gradle ./gradle
COPY build.gradle.kts settings.gradle.kts ./
COPY src ./src
# Drop in the freshly built frontend so Spring serves it at "/"
COPY --from=frontend /app/frontend/dist/frontend/browser/ ./src/main/resources/static/
RUN chmod +x gradlew && ./gradlew bootJar --no-daemon

# ---- Stage 3: slim runtime image ----
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=backend /app/build/libs/*-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
