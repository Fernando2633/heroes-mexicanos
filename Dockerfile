# ─────────────────────────────────────────────────────────────
# Dockerfile – Heroes Mexicanos (Spring Boot)
# Multi-stage build: Maven → JRE Alpine
# Incluye entrypoint.sh para compatibilidad con Render PostgreSQL
# ─────────────────────────────────────────────────────────────

# ── Stage 1: Build ──────────────────────────────────────────
FROM eclipse-temurin:17-jdk-alpine AS builder

WORKDIR /app

COPY mvnw .
COPY .mvn/ .mvn/
RUN chmod +x mvnw

# Descargar dependencias primero (aprovecha caché de capas)
COPY pom.xml .
RUN ./mvnw dependency:go-offline -B --no-transfer-progress

# Compilar
COPY src/ src/
RUN ./mvnw package -DskipTests -B --no-transfer-progress

# ── Stage 2: Runtime ─────────────────────────────────────────
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

LABEL maintainer="zeriklabs"
LABEL description="Sistema Hexagonal - Gestión de Héroes Mexicanos"

# Instalar bash/sh mínimo para el entrypoint
RUN apk add --no-cache bash

# Usuario no-root
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

# Copiar JAR y script de entrypoint
COPY --from=builder /app/target/*.jar app.jar
COPY entrypoint.sh entrypoint.sh
RUN chmod +x entrypoint.sh && chown appuser:appgroup app.jar entrypoint.sh

USER appuser

EXPOSE 8080

ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -Djava.security.egd=file:/dev/./urandom"

# El entrypoint convierte DATABASE_URL → SPRING_DATASOURCE_* antes de arrancar
ENTRYPOINT ["./entrypoint.sh"]
