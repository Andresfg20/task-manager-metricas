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

_Pendiente — sección a cargo de Julio Lozano._

## 6. Instalación y ejecución

_Pendiente — sección a cargo de Julio Lozano._

## 7. Métricas aplicadas

_Pendiente — sección a cargo de Juan Horta._

## 8. Resultados y refactorización

_Pendiente — sección a cargo de Juan Horta._

## 9. Pruebas

_Pendiente — sección a cargo de Julio Lozano._

## 10. Entregables y conclusiones

_Pendiente — sección a cargo de Juan Horta._
