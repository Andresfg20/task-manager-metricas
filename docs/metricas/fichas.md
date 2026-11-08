# Fichas de métricas (Anexo B)

Se definen cuatro métricas: dos estructurales, una de diseño orientado a objetos y una de evolución.
Todas se obtienen con el script `metricas/medir.ps1` salvo la ficha 4, que se calcula con los issues de GitHub.

## Ficha 1 — Complejidad ciclomática V(G) por método (estructural)

| Campo | Valor |
|---|---|
| Nombre | Complejidad ciclomática de McCabe, V(G) |
| Tipo | Estructural (flujo de control) |
| Atributo ISO/IEC 25010 | Mantenibilidad → Analizabilidad y Capacidad de ser probado |
| Objetivo | Detectar métodos con demasiados caminos de ejecución, difíciles de entender y de probar |
| Fórmula | V(G) = E − N + 2P (aristas − nodos + 2·componentes) = número de decisiones + 1 |
| Unidad / escala | Número entero ≥ 1, escala de razón |
| Herramienta | CK (`method.csv`, columna `wmc`) y lizard (columna `CCN`) |
| Umbral | ≤ 10 aceptable · 11–20 riesgo moderado · > 20 crítico (RNF-03 exige ≤ 10) |
| Frecuencia | En cada versión etiquetada (v0.3.0 antes y v0.4.0 después de refactorizar) |
| Responsable | Juan Horta |
| Interpretación | Un valor alto indica el mínimo de casos de prueba necesarios para cubrir todos los caminos del método |

## Ficha 2 — Líneas de código (LOC / NLOC) (estructural, tamaño)

| Campo | Valor |
|---|---|
| Nombre | Líneas de código sin comentarios ni líneas en blanco (NLOC) |
| Tipo | Estructural (tamaño) |
| Atributo ISO/IEC 25010 | Mantenibilidad → Modificabilidad |
| Objetivo | Conocer el tamaño del sistema y detectar métodos o clases demasiado largos |
| Fórmula | Conteo de líneas físicas con código ejecutable o declaraciones |
| Unidad / escala | Líneas, escala de razón |
| Herramienta | lizard (`NLOC`) y CK (`loc`) |
| Umbral | Método ≤ 30 NLOC · clase ≤ 300 NLOC |
| Frecuencia | En cada versión etiquetada |
| Responsable | Juan Horta |
| Interpretación | Sirve como base para normalizar otras métricas (por ejemplo, defectos o esfuerzo por cada 1.000 LOC) |

## Ficha 3 — Suite CK: WMC y CBO por clase (diseño orientado a objetos)

| Campo | Valor |
|---|---|
| Nombre | WMC (Weighted Methods per Class) y CBO (Coupling Between Objects) |
| Tipo | Diseño (Chidamber & Kemerer) |
| Atributo ISO/IEC 25010 | Mantenibilidad → Modularidad, Reusabilidad y Modificabilidad |
| Objetivo | Detectar clases con demasiadas responsabilidades (WMC alto) o muy acopladas (CBO alto) |
| Fórmula | WMC = Σ V(G) de los métodos de la clase · CBO = número de clases distintas con las que la clase se acopla |
| Unidad / escala | Números enteros, escala de razón |
| Herramienta | CK (`class.csv`, columnas `wmc` y `cbo`); también se registran RFC, LCOM, DIT y NOC |
| Umbral | WMC ≤ 20 (RNF-03) · CBO ≤ 10 |
| Frecuencia | En cada versión etiquetada |
| Responsable | Juan Horta |
| Interpretación | La clase con mayor WMC/CBO es la primera candidata a refactorización (extraer clase o método) |

## Ficha 4 — Distribución del esfuerzo por tipo de mantenimiento (evolución)

| Campo | Valor |
|---|---|
| Nombre | Porcentaje de esfuerzo por tipo de mantenimiento |
| Tipo | Evolución / proceso |
| Atributo ISO/IEC 25010 | Mantenibilidad (vista del proceso de mantenimiento, ISO/IEC 14764) |
| Objetivo | Saber en qué tipo de mantenimiento se invierte el esfuerzo del equipo |
| Fórmula | % tipo = horas de los issues con la etiqueta del tipo ÷ horas totales de mantenimiento × 100 |
| Unidad / escala | Porcentaje |
| Herramienta | Issues de GitHub con etiquetas `correctivo`, `adaptativo`, `perfectivo`, `preventivo` y horas registradas |
| Umbral / referencia | Distribución de referencia: correctivo 21 %, adaptativo 25 %, perfectivo 50 %, preventivo 4 % |
| Frecuencia | Al cierre del proyecto (v1.0.0) |
| Responsable | Juan Horta, con las horas que registra cada integrante en sus issues |
| Interpretación | Un correctivo muy por encima de la referencia indica problemas de calidad; un preventivo alto indica inversión en mantenibilidad |
