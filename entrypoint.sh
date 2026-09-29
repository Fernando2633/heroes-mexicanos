#!/bin/sh
# entrypoint.sh
# Convierte DATABASE_URL de Render (postgresql://user:pass@host/db)
# al formato JDBC que necesita Spring Boot (jdbc:postgresql://host/db)
# y exporta las variables de entorno que Spring Boot lee automáticamente.

set -e

if [ -n "$DATABASE_URL" ]; then
    echo "==> Convirtiendo DATABASE_URL al formato JDBC..."

    # Eliminar el esquema postgresql:// o postgres://
    STRIPPED=$(echo "$DATABASE_URL" | sed 's|^postgresql://||' | sed 's|^postgres://||')

    # Separar userinfo (user:pass) del hostpath (host:port/db)
    USERINFO=$(echo "$STRIPPED" | cut -d'@' -f1)
    HOSTPATH=$(echo "$STRIPPED"  | cut -d'@' -f2)

    DB_USER=$(echo "$USERINFO" | cut -d':' -f1)
    DB_PASS=$(echo "$USERINFO" | cut -d':' -f2)

    # Construir la URL JDBC
    JDBC_URL="jdbc:postgresql://${HOSTPATH}"

    # Exportar como variables que Spring Boot lee vía relaxed binding
    export SPRING_DATASOURCE_URL="$JDBC_URL"
    export SPRING_DATASOURCE_USERNAME="$DB_USER"
    export SPRING_DATASOURCE_PASSWORD="$DB_PASS"

    # Dialecto PostgreSQL — sobreescribe el default H2 de application.properties
    export DB_PLATFORM="org.hibernate.dialect.PostgreSQLDialect"
    export SPRING_JPA_DATABASE_PLATFORM="org.hibernate.dialect.PostgreSQLDialect"

    # Desactivar H2 console y seed en producción
    export SPRING_H2_CONSOLE_ENABLED="false"
    export SPRING_SQL_INIT_MODE="never"

    echo "==> Conectando a PostgreSQL: ${HOSTPATH}"
else
    echo "==> DATABASE_URL no encontrada, usando configuración local (H2)."
fi

exec java $JAVA_OPTS -jar app.jar
