#!/usr/bin/env bash
# Igual que medir.ps1 para Linux/macOS/Git Bash. Uso: bash metricas/medir.sh antes|despues
set -euo pipefail
ETIQUETA="${1:?usa: antes | despues}"
RAIZ="$(cd "$(dirname "$0")/.." && pwd)"
SALIDA="metricas/resultados/$ETIQUETA"
mkdir -p "$RAIZ/$SALIDA"
docker run --rm -v "$RAIZ:/work" -w /work maven:3.9-eclipse-temurin-21 bash metricas/ck.sh "$SALIDA"
docker run --rm -v "$RAIZ:/work" -w /work python:3.12-slim sh -c "pip install -q lizard==1.17.10 && lizard -l java --csv src/backend/src/main/java > $SALIDA/lizard.csv"
docker run --rm -v "$RAIZ:/work" -w /work python:3.12-slim python metricas/resumen.py "$SALIDA"
