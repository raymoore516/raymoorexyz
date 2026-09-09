# syntax=docker/dockerfile:1

FROM node:24.20.0-bookworm-slim AS frontend-build

WORKDIR /workspace/frontend

COPY frontend/package.json frontend/package-lock.json ./
RUN npm ci

COPY frontend/ ./
RUN npm run build


FROM maven:3.9.11-eclipse-temurin-25 AS backend-build

WORKDIR /workspace/backend

COPY backend/.mvn/ .mvn/
COPY backend/mvnw backend/pom.xml ./
RUN ./mvnw --batch-mode dependency:go-offline

COPY backend/src/ src/
COPY --from=frontend-build /workspace/frontend/dist/ src/main/resources/static/
RUN ./mvnw --batch-mode package -DskipTests


FROM eclipse-temurin:25.0.4_7-jre-ubi10-minimal AS runtime

WORKDIR /app

ENV PORT=10000

COPY --from=backend-build /workspace/backend/target/raymoore-xyz-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 10000

USER 10001:10001

CMD ["sh", "-c", "exec java -Dserver.port=${PORT} -jar /app/app.jar"]
