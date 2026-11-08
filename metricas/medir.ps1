<#
  Mide el backend con CK (métricas Chidamber & Kemerer) y lizard (V(G) y LOC) usando Docker.
  Uso (desde la raíz del repositorio):
      .\metricas\medir.ps1 -Etiqueta antes
      .\metricas\medir.ps1 -Etiqueta despues
  Resultado: metricas/resultados/<Etiqueta>/ con class.csv, method.csv, lizard.csv y resumen.md
#>
param([Parameter(Mandatory = $true)][ValidateSet("antes", "despues")][string]$Etiqueta)

$ErrorActionPreference = "Stop"
$raiz = (Resolve-Path "$PSScriptRoot\..").Path
$salida = "metricas/resultados/$Etiqueta"
New-Item -ItemType Directory -Force -Path "$raiz/$salida" | Out-Null

Write-Host "1/3 CK $((Get-Content "$raiz/metricas/config.properties" | Select-String 'ck.version').ToString())"
docker run --rm -v "${raiz}:/work" -w /work maven:3.9-eclipse-temurin-21 bash metricas/ck.sh $salida
if ($LASTEXITCODE -ne 0) { throw "CK falló" }

Write-Host "2/3 lizard"
docker run --rm -v "${raiz}:/work" -w /work python:3.12-slim sh -c "pip install -q lizard==1.17.10 && lizard -l java --csv src/backend/src/main/java > $salida/lizard.csv"
if ($LASTEXITCODE -ne 0) { throw "lizard falló" }

Write-Host "3/3 resumen y verificación de umbrales (RNF-03)"
docker run --rm -v "${raiz}:/work" -w /work python:3.12-slim python metricas/resumen.py $salida
exit $LASTEXITCODE
