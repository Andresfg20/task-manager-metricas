# Task Manager — Métricas y Estándares de Calidad

Proyecto final de la asignatura **Métricas y Estándares de Calidad** — Corporación Universitaria del Huila (CORHUILA).
Docente: José Miguel Llanos Mosquera.

## 1. Descripción del proyecto

Task Manager es una aplicación web full-stack para gestionar tareas diarias. Cada usuario se registra, inicia sesión y
administra únicamente sus propias tareas: crearlas, editarlas, eliminarlas, marcarlas como completadas o reabrirlas,
reprogramarlas y verlas en una vista de calendario. Existe un rol `ADMIN` con un alcance académico mínimo: gestiona sus
propias tareas y consulta el listado de usuarios registrados.

Sobre esta aplicación se aplican métricas de calidad de software (complejidad ciclomática, métricas CK y esfuerzo de
mantenimiento), se identifican elementos críticos y se implementa una refactorización guiada por los resultados.

## 2. Integrantes y roles

| Integrante | Rol principal | Responsabilidades |
|---|---|---|
| Andrés Felipe Gómez Anacona | Backend y requisitos | Repositorio base, requisitos, arquitectura, backend (Spring Boot) y pruebas unitarias |
| Julio César Lozano Lozano | Frontend y pruebas | Issues y etiquetas, frontend (React), pruebas de integración, Postman, trazabilidad, Docker Compose |
| Juan José Horta Vanegas | Métricas y calidad | Script de medición, fichas de métricas, resultados, refactorización, ArchUnit, releases |

## 3. Requisitos

Los 10 requisitos funcionales (RF-01 a RF-10) y los 3 requisitos no funcionales (RNF-01 a RNF-03) están en
[docs/requisitos.md](docs/requisitos.md). La matriz de trazabilidad requisito → código → prueba está en
[docs/trazabilidad.md](docs/trazabilidad.md).

| ID | Requisito |
|---|---|
| RF-01 | Registro de usuario |
| RF-02 | Inicio de sesión con JWT |
| RF-03 | Crear tarea |
| RF-04 | Editar tarea |
| RF-05 | Eliminar tarea |
| RF-06 | Completar / reabrir tarea |
| RF-07 | Listar tareas paginadas con filtros (estado, vencidas) |
| RF-08 | Vista de calendario (rango de hasta 42 días) |
| RF-09 | Reprogramar fecha de vencimiento |
| RF-10 | Listado de usuarios para ADMIN |
| RNF-01 | Rendimiento: p95 ≤ 500 ms con 1.000 tareas |
| RNF-02 | Seguridad: BCrypt, JWT y 0 accesos a tareas ajenas |
| RNF-03 | Mantenibilidad: V(G) ≤ 10 por método y WMC ≤ 20 por clase |

## 4. Arquitectura

Monolito modular en capas. El backend (Spring Boot 3.5, Java 21) se organiza por módulos `auth`, `users`, `tasks` y
`common`, cada uno con las capas `controller → service → repository → entity` y `dto`. El frontend (React 18 + Vite)
consume la API REST `/api/v1` y la base de datos es PostgreSQL 16 con migraciones Flyway. Todo se levanta con Docker Compose.

Diagrama, justificación de decisiones y estructura de carpetas: [docs/arquitectura/README.md](docs/arquitectura/README.md).

```
task-manager-metricas/
├── README.md
├── docker-compose.yml
├── .env.example
├── docs/            # requisitos, arquitectura, trazabilidad, métricas e informe final (PDF)
├── src/backend/     # API Spring Boot (common, auth, users, tasks)
├── src/frontend/    # SPA React
├── tests/           # colección Postman
└── metricas/        # script y configuración de medición
```

## 5. Tecnologías

| Capa | Tecnología |
|---|---|
| Backend | Java 21, Spring Boot 3.5.7 (Web, Security, Data JPA, Validation), jjwt 0.12.6, springdoc-openapi 2.8.9 |
| Base de datos | PostgreSQL 16, Flyway (migraciones), H2 en modo PostgreSQL para pruebas |
| Frontend | React 18.3, Vite 5.4, React Router 6 |
| Pruebas | JUnit 5, Mockito, Spring MockMvc, ArchUnit 1.3, JaCoCo 0.8.12, Vitest 2.1, Testing Library, Postman/newman |
| Métricas | CK (métricas Chidamber & Kemerer), lizard (complejidad ciclomática y LOC) |
| Infraestructura | Docker, Docker Compose, nginx |

## 6. Instalación y ejecución

Requisitos: Docker Desktop y Git.

```bash
git clone https://github.com/Andresfg20/task-manager-metricas.git
cd task-manager-metricas
cp .env.example .env          # en PowerShell: Copy-Item .env.example .env
# Edita .env y define JWT_SECRET (mínimo 32 caracteres) y las contraseñas
docker compose up --build
```

| Servicio | URL |
|---|---|
| Frontend | http://localhost:3000 |
| API | http://localhost:8080/api/v1 |
| Swagger UI | http://localhost:8080/swagger-ui.html |

Usuario administrador inicial: el definido en `ADMIN_EMAIL` / `ADMIN_PASSWORD` del archivo `.env`.

## 7. Métricas aplicadas
 
| Métrica | Tipo | Herramienta | Umbral |
|---|---|---|---|
| Complejidad ciclomática V(G) | Estructural | CK, lizard | ≤ 10 por método |
| LOC / NLOC | Estructural (tamaño) | lizard, CK | ≤ 30 por método |
| WMC, CBO, RFC, LCOM, DIT, NOC (suite CK) | Diseño OO | CK | WMC ≤ 20, CBO ≤ 10 |
| Esfuerzo por tipo de mantenimiento | Evolución | Issues de GitHub | Referencia 21/25/50/4 % |
 
Fichas completas en [docs/metricas/fichas.md](docs/metricas/fichas.md). Para medir (requiere Docker):
 
```powershell
.\metricas\medir.ps1 -Etiqueta antes
```
 
## 8. Resultados y refactorización
 
- 0 métodos con V(G) > 10 y V(G) promedio 1,2. RNF-03 no se cumple a nivel de clase: la entidad `Task` tiene WMC 22 (límite 20),
  casi todo por accesores; se registra como hallazgo abierto.
- Elemento refactorizado: `TaskService` (WMC 18 de 20 y CBO 11). Se aplicó **Extraer clase** (`TaskCalendarService`) e
  **Introducir objeto de valor** (`CalendarRange`).
- Resultado: en `TaskService`, WMC 18 → 15 (−16,7 %), CBO 11 → 10, RFC 31 → 25 y NLOC 100 → 84. A cambio, el LCOM sube de 2 a 6,
  el CBO de `TaskController` de 26 a 27 y el total crece 30 NLOC por las clases nuevas.
Tabla completa (Anexo C) en [docs/metricas/resultados.md](docs/metricas/resultados.md) y detalle en
[docs/metricas/refactorizacion.md](docs/metricas/refactorizacion.md).

## 9. Pruebas

| Tipo | Ubicación | Comando |
|---|---|---|
| Unitarias backend | `src/backend/src/test/.../service` | `mvn -B verify` (en `src/backend`) |
| Integración backend | `src/backend/src/test/.../controller/*IT.java` | `mvn -B verify` |
| Arquitectura | `ArchitectureTest.java` | `mvn -B verify` |
| Frontend | `src/frontend/src/**/*.test.js(x)` | `npm test` (en `src/frontend`) |
| API (Postman) | `tests/task-manager.postman_collection.json` | `npx newman run tests/task-manager.postman_collection.json` |

Sin Maven instalado, el backend se prueba con Docker (PowerShell, desde `src/backend`):

```powershell
docker run --rm -v "${PWD}:/app" -w /app maven:3.9-eclipse-temurin-21 mvn -B verify
```

El reporte de cobertura queda en `src/backend/target/site/jacoco/index.html` (regla: ≥ 80 % de líneas en los servicios).
La relación de cada prueba con su requisito está en [docs/trazabilidad.md](docs/trazabilidad.md).

## 10. Entregables y conclusiones
 
| Entregable | Ubicación |
|---|---|
| Informe final (PDF) | [docs/informe-final.pdf](docs/informe-final.pdf) |
| Requisitos y trazabilidad | `docs/requisitos.md`, `docs/trazabilidad.md` |
| Arquitectura | `docs/arquitectura/` |
| Fichas y resultados de métricas | `docs/metricas/` |
| Script de medición | `metricas/` |
| Versiones | Tags `v0.1.0` a `v1.0.0` y release `v1.0.0` |
 
Conclusiones:
 
1. Medir antes de refactorizar permitió elegir la clase con mayor riesgo con datos y no por intuición.
2. Una complejidad ciclomática baja no basta: las métricas de diseño (WMC, CBO) muestran concentración de responsabilidades.
3. La refactorización reduce la complejidad y el acoplamiento de la clase crítica a cambio de un aumento pequeño del tamaño total y de una cohesión algo menor.
4. Las herramientas tienen límites: CK 0.7.0 no analiza los `record` y, sin las librerías en el classpath, subestima el acoplamiento (CBO).
