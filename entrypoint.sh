#!/bin/sh
# entrypoint.sh
# Normaliza la URL de BD de Render al formato JDBC que necesita Spring Boot.
# Maneja tanto DATABASE_URL como DB_URL, y tanto postgresql:// como jdbc:postgresql://

set -e

# ── 1. Determinar cuál variable tiene la URL de BD ─────────────────────────
# Prioridad: DATABASE_URL > DB_URL
RAW_URL="${DATABASE_URL:-$DB_URL}"

if [ -n "$RAW_URL" ]; then

    echo "==> URL de BD detectada, convirtiendo al formato JDBC..."

    case "$RAW_URL" in

        jdbc:postgresql://* | jdbc:postgres://*)
            # Ya viene en formato JDBC — solo extraemos usuario y contraseña si no están separados
            echo "==> Formato JDBC detectado, usándolo directamente."
            export SPRING_DATASOURCE_URL="$RAW_URL"
            ;;

        postgresql://* | postgres://*)
            # Formato Render: postgresql://user:password@host:port/dbname
            # Eliminamos el esquema
            STRIPPED=$(echo "$RAW_URL" | sed 's|^postgresql://||;s|^postgres://||')
            # Separamos userinfo del hostpath
            USERINFO=$(echo "$STRIPPED" | cut -d'@' -f1)
            HOSTPATH=$(echo "$STRIPPED"  | cut -d'@' -f2)
            # Extraemos usuario y contraseña
            DB_USER=$(echo "$USERINFO" | cut -d':' -f1)
            DB_PASS=$(echo "$USERINFO" | cut -d':' -f2-)

            export SPRING_DATASOURCE_URL="jdbc:postgresql://${HOSTPATH}"
            export SPRING_DATASOURCE_USERNAME="$DB_USER"
            export SPRING_DATASOURCE_PASSWORD="$DB_PASS"
            ;;

        *)
            echo "==> ADVERTENCIA: formato de URL desconocido: $RAW_URL"
            ;;
    esac

    # Dialecto PostgreSQL — sobreescribe el default H2 de application.properties
    export DB_PLATFORM="org.hibernate.dialect.PostgreSQLDialect"
    export SPRING_JPA_DATABASE_PLATFORM="org.hibernate.dialect.PostgreSQLDialect"

    # Desactivar H2 console y seed SQL en producción
    export SPRING_H2_CONSOLE_ENABLED="false"
    export SPRING_SQL_INIT_MODE="never"

    echo "==> Conectando a: ${SPRING_DATASOURCE_URL}"
    echo "==> Usuario     : ${SPRING_DATASOURCE_USERNAME}"

else
    echo "==> Sin URL de BD (DATABASE_URL / DB_URL). Usando H2 en memoria (desarrollo local)."
fi

exec java $JAVA_OPTS -jar app.jar
