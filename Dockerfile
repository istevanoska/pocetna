# ---- Stage 1: compile the backend and export the link directory as JSON ----
# The frontend is prerendered to static HTML, and that happens before any backend is
# running, so the renderer reads the links from this snapshot instead of the API.
FROM eclipse-temurin:17-jdk AS backend-build
WORKDIR /app
COPY gradlew ./
COPY gradle ./gradle
COPY build.gradle.kts settings.gradle.kts ./
COPY src ./src
RUN chmod +x gradlew && ./gradlew exportLinks --no-daemon

# ---- Stage 2: build the Angular frontend, prerendering every route ----
FROM node:22-alpine AS frontend
WORKDIR /app/frontend
COPY frontend/package*.json ./
RUN npm ci
COPY frontend/ ./
# Overwrite the committed snapshot with the one just generated from the Kotlin list,
# so the prerendered pages can never be out of date with what the API serves.
COPY --from=backend-build /app/frontend/src/links.snapshot.json ./src/links.snapshot.json
RUN npm run build

# ---- Stage 3: build the Spring Boot jar with the frontend baked into static/ ----
# Continues from stage 1, so the backend classes are already compiled.
FROM backend-build AS backend
WORKDIR /app
# Drop in the freshly built frontend so Spring serves it at "/"
COPY --from=frontend /app/frontend/dist/frontend/browser/ ./src/main/resources/static/
RUN ./gradlew bootJar --no-daemon

# ---- Stage 4: slim runtime image ----
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=backend /app/build/libs/*-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
