# Resultados de la medición (Anexo C)

Medición sobre `src/backend/src/main/java` (código de producción; sin pruebas).
Los valores de lizard (NLOC, métodos, V(G)) y los de CK (WMC, CBO, RFC, LCOM, LOC) salen de `.\metricas\medir.ps1`.
Los archivos originales están en `metricas/resultados/antes` y `metricas/resultados/despues`.

## 1. Resumen general (antes de la refactorización — v0.3.0)

| Indicador | Valor | Umbral | Estado |
|---|---|---|---|
| NLOC total del backend | 1.073 | — | — |
| Métodos medidos | 111 (lizard) · 105 (CK) | — | — |
| V(G) promedio por método | 1,2 | ≤ 10 | Cumple |
| V(G) máximo | 4 (`AdminSeeder.run`) | ≤ 10 | Cumple |
| Métodos con V(G) > 10 | 0 | 0 | Cumple |
| WMC máximo | 22 (`Task`); `TaskService` 18 | ≤ 20 | **No cumple** (110 % del umbral, entidad `Task`) |
| Clases con CBO > 10 (CK) | 7 | 0 | Crítico relativo |
| Método más largo | 17 NLOC (`SecurityConfig.securityFilterChain`) | ≤ 30 | Cumple |

## 2. Métricas por clase (top 10 por WMC, antes de la refactorización)

Métodos y NLOC provienen de lizard; WMC, CBO, RFC, LCOM y el estado, de CK.

| Clase | Métodos | WMC | NLOC | CBO | RFC | LCOM | Estado (CK) |
|---|---|---|---|---|---|---|---|
| Task | 18 | 22 | 112 | 10 | 2 | 93 | **Crítico** (WMC > 20) |
| TaskService | 13 | 18 | 100 | 11 | 31 | 2 | **Crítico** (CBO > 10) |
| UserService | 7 | 10 | 58 | 10 | 20 | 1 | OK |
| User | 10 | 10 | 69 | 10 | 1 | 31 | OK |
| GlobalExceptionHandler | 9 | 9 | 67 | 16 | 18 | 36 | **Crítico** (CBO > 10) |
| TaskController | 9 | 9 | 89 | 26 | 15 | 0 | **Crítico** (CBO > 10) |
| JwtService | 4 | 6 | 56 | 5 | 23 | 0 | OK |
| AdminSeeder | 2 | 5 | 31 | 7 | 4 | 0 | OK |
| AuthService | 4 | 4 | 27 | 8 | 10 | 0 | OK |
| JwtAuthenticationFilter | 2 | 4 | 32 | 8 | 12 | 0 | OK |

Otras clases críticas por CBO fuera del top 10: `SecurityConfig` (16), `AuthController` (13), `JsonSecurityErrorHandler` (11) y `UserController` (11).

## 3. Elementos críticos identificados

Ningún método supera V(G) = 10. A nivel de clase, `Task` supera el WMC de 20 y `TaskService` supera el CBO de 10, así que el criterio de selección combina el **incumplimiento de umbrales**, la **cercanía al umbral** y la **concentración de responsabilidades**:

| # | Elemento | Evidencia | Riesgo | Decisión |
|---|---|---|---|---|
| 1 | `TaskService` | WMC 18 de 20 (90 %); CBO 11 (límite 10); 13 métodos; mezcla comandos (crear, editar, borrar) con la consulta del calendario y su validación de rango | Cualquier nuevo requisito de tareas lo lleva por encima del umbral de RNF-03 | **Refactorizar** (Extraer clase) |
| 2 | `TaskService.findCalendar` | V(G) 3 y 11 NLOC (empatado con `normalizeDescription` como el de mayor V(G) de la clase); valida, consulta y transforma | Regla de negocio (rango ≤ 42 días) escondida dentro del servicio | **Refactorizar** junto con el elemento 1 |
| 3 | `Task` (entidad) | WMC 22 de 20 (110 %): 9 accesores triviales suman 9 y la lógica 13 (V(G) máximo 3); LCOM 93 | Incumple RNF-03 aunque su lógica es simple | **Hallazgo abierto**: no se refactoriza en esta iteración; se consulta con el docente si los accesores cuentan para WMC |
| 4 | `TaskController` | CBO 26, el mayor del backend | Alto acoplamiento propio de un controlador | Vigilar |
| 5 | `SecurityConfig.securityFilterChain` | Método más largo (17 NLOC), V(G) 1 | Bajo: es configuración declarativa | Aceptar |
| 6 | `UserService` | WMC 10 | Bajo | Vigilar en la siguiente medición |

## 4. Refactorización implementada — antes y después

Técnica: **Extraer clase** + **Introducir objeto de valor** (Fowler). Detalle en [refactorizacion.md](refactorizacion.md).

| Métrica | Antes (v0.3.0) | Después (v0.4.0) | Variación |
|---|---|---|---|
| WMC `TaskService` | 18 | 15 | −3 (−16,7 %) |
| Métodos `TaskService` | 13 | 12 | −1 |
| NLOC `TaskService` | 100 | 84 | −16 (−16 %) |
| V(G) máximo en `TaskService` | 3 | 3 (`normalizeDescription`) | = |
| WMC `TaskCalendarService` (nueva) | — | 2 | — |
| WMC `CalendarRange` (nueva) | — | 3 (lizard)* | — |
| CBO `TaskService` | 11 | 10 | −1 |
| RFC `TaskService` | 31 | 25 | −6 |
| LCOM `TaskService` | 2 | 6 | +4 (menor cohesión) |
| LOC `TaskService` (CK) | 67 | 59 | −8 |
| CBO `TaskController` | 26 | 27 | +1 |
| Clases críticas según CK | 8 | 7 | −1 (`TaskService`) |
| WMC máximo del backend | 22 (`Task`) | 22 (`Task`) | = |
| NLOC total del backend | 1.073 | 1.103 | +30 (+2,8 %) |
| V(G) promedio | 1,2 | 1,2 | = |
| Métodos con V(G) > 10 | 0 | 0 | = |

Lectura: `TaskService` deja de ser crítica en CK. Su WMC baja de 90 % a 75 % del umbral, su CBO pasa de 11 a 10 y su RFC de 31 a 25.
Hay costos: el tamaño total sube 30 NLOC por las dos clases nuevas, el LCOM de `TaskService` sube de 2 a 6 (sigue siendo un valor bajo)
y parte del acoplamiento pasa al controlador, cuyo CBO sube de 26 a 27. La refactorización no cambia el incumplimiento de RNF-03,
porque el WMC de 22 corresponde a la entidad `Task`, que no se modificó. `mvn -B verify` termina en verde con 42 pruebas y se agregan
pruebas unitarias para `TaskCalendarService` y `CalendarRange`.

\* CK 0.7.0 no reporta `CalendarRange` porque es un `record` de Java; su WMC se toma de lizard.

## 5. Esfuerzo por tipo de mantenimiento (ficha 4)

| Tipo | Issues | Horas | % equipo | % referencia | Diferencia |
|---|---|---|---|---|---|
| Correctivo | | | | 21 % | |
| Adaptativo | | | | 25 % | |
| Perfectivo | | | | 50 % | |
| Preventivo | | | | 4 % | |
| **Total** | | | 100 % | 100 % | |

Completar al cierre con los issues cerrados de GitHub (filtrar por etiqueta y sumar las horas registradas).
