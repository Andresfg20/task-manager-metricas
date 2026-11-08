# Medición de métricas

Requisito: Docker Desktop en ejecución. No hace falta instalar Java, Maven ni Python.

```powershell
.\metricas\medir.ps1 -Etiqueta antes     # código antes de la refactorización
.\metricas\medir.ps1 -Etiqueta despues   # código después de la refactorización
```

En Git Bash, Linux o macOS: `bash metricas/medir.sh antes`.

| Archivo | Contenido |
|---|---|
| `config.properties` | Versiones de las herramientas y umbrales de RNF-03 |
| `ck.sh` | Descarga CK desde Maven Central y lo ejecuta sobre `src/backend/src/main/java` |
| `resumen.py` | Genera `resumen.md` y devuelve código de error si V(G) > 10 o WMC > 20 |
| `resultados/<etiqueta>/` | `class.csv`, `method.csv` (CK), `lizard.csv` y `resumen.md` |

En `method.csv` de CK, la columna `wmc` de un método es su complejidad ciclomática V(G).
