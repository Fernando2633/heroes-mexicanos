# ─────────────────────────────────────────────────────────────
# Dockerfile – Heroes Mexicanos (Spring Boot)
# Multi-stage build: build con Maven → imagen mínima JRE
# ─────────────────────────────────────────────────────────────

# ── Stage 1: Build ──────────────────────────────────────────
FROM eclipse-temurin:17-jdk-alpine AS builder

WORKDIR /app

# Copiamos el wrapper de Maven primero para aprovechar caché de capas
COPY mvnw .
COPY .mvn/ .mvn/
RUN chmod +x mvnw

# Descargamos dependencias (se cachean si pom.xml no cambia)
COPY pom.xml .
RUN ./mvnw dependency:go-offline -B --no-transfer-progress

# Compilamos el proyecto
COPY src/ src/
RUN ./mvnw package -DskipTests -B --no-transfer-progress

# ── Stage 2: Runtime ─────────────────────────────────────────
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Metadatos
LABEL maintainer="zeriklabs"
LABEL description="Sistema de Gestion de Heroes Mexicanos - Arquitectura Hexagonal"

# Usuario no-root por seguridad
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

# Copiamos solo el JAR generado
COPY --from=builder /app/target/*.jar app.jar

# Render inyecta la variable PORT; por defecto 8080
EXPOSE 8080

# JVM optimizada para contenedores pequeños
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -Djava.security.egd=file:/dev/./urandom"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
