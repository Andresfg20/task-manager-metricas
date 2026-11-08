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

_Pendiente — sección a cargo de Juan Horta._

## 8. Resultados y refactorización

_Pendiente — sección a cargo de Juan Horta._

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

_Pendiente — sección a cargo de Juan Horta._
