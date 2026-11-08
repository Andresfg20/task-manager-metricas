#!/usr/bin/env bash
# Se ejecuta dentro del contenedor maven. Descarga CK desde Maven Central y mide el código fuente principal.
set -euo pipefail
SALIDA="$1"
VERSION="$(grep '^ck.version=' metricas/config.properties | cut -d= -f2 | tr -d '\r')"
mvn -q dependency:copy -Dartifact="com.github.mauricioaniche:ck:${VERSION}:jar:jar-with-dependencies" -DoutputDirectory=/tmp/ck
# Argumentos de CK: <carpeta> <usar jars> <archivos por partición (0 = automático)> <métricas de variables> <salida>
java -jar "/tmp/ck/ck-${VERSION}-jar-with-dependencies.jar" src/backend/src/main/java false 0 false "${SALIDA}/"
