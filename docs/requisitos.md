# Requisitos — Task Manager

Convenciones: cada requisito tiene un identificador único, una descripción verificable y un criterio de aceptación.
Los requisitos se relacionan con el código y las pruebas en [trazabilidad.md](trazabilidad.md).

## Requisitos funcionales

| ID | Nombre | Descripción | Criterio de aceptación | Prioridad |
|---|---|---|---|---|
| RF-01 | Registro de usuario | El sistema permite registrar un usuario con nombre, correo y contraseña. El rol asignado es `USER`. | `POST /api/v1/auth/register` responde 201; un correo repetido responde 409; la contraseña se guarda cifrada con BCrypt. | Alta |
| RF-02 | Inicio de sesión | El usuario inicia sesión con correo y contraseña y recibe un token JWT válido por 60 minutos. | `POST /api/v1/auth/login` responde 200 con el token; credenciales inválidas responden 401 sin indicar cuál dato falló. | Alta |
| RF-03 | Crear tarea | El usuario crea una tarea con título (obligatorio), descripción y fecha de vencimiento opcionales. Nace en estado `PENDING`. | `POST /api/v1/tasks` responde 201; título vacío responde 400; el título se guarda sin espacios sobrantes. | Alta |
| RF-04 | Editar tarea | El usuario modifica título, descripción y fecha de una tarea propia. | `PUT /api/v1/tasks/{id}` responde 200; una tarea ajena responde 404. | Alta |
| RF-05 | Eliminar tarea | El usuario elimina definitivamente una tarea propia. | `DELETE /api/v1/tasks/{id}` responde 204; la tarea ya no se puede consultar. | Alta |
| RF-06 | Completar / reabrir | El usuario cambia el estado de una tarea entre `PENDING` y `COMPLETED`; al completar se registra la fecha de finalización. | `PATCH /api/v1/tasks/{id}/status` responde 200; repetir el mismo estado no cambia nada. | Alta |
| RF-07 | Listar tareas | El usuario lista sus tareas paginadas, filtradas por estado o solo vencidas, ordenadas por fecha de vencimiento. | `GET /api/v1/tasks?status=&overdue=&page=&size=` devuelve solo tareas propias; una tarea vencida es `PENDING` con fecha anterior a hoy. | Media |
| RF-08 | Vista de calendario | El usuario consulta sus tareas con fecha entre dos días (inclusive), con un rango máximo de 42 días. | `GET /api/v1/tasks/calendar?from=&to=` responde 200; rango invertido o mayor a 42 días responde 400 `INVALID_DATE_RANGE`. | Media |
| RF-09 | Reprogramar tarea | El usuario cambia solo la fecha de vencimiento de una tarea propia. | `PATCH /api/v1/tasks/{id}/due-date` responde 200 y no altera título, descripción ni estado. | Media |
| RF-10 | Listado de usuarios | Un `ADMIN` consulta el listado paginado de usuarios registrados (sin contraseñas). Requisito académico: el `ADMIN` no ve tareas de otros. | `GET /api/v1/users` responde 200 para `ADMIN` y 403 para `USER`. | Baja |

## Requisitos no funcionales

| ID | Atributo (ISO/IEC 25010) | Descripción | Métrica y umbral | Cómo se verifica |
|---|---|---|---|---|
| RNF-01 | Eficiencia de desempeño | Las consultas de tareas responden rápido con un volumen realista de datos. | Percentil 95 del tiempo de respuesta ≤ 500 ms con 1.000 tareas por usuario. | Ejecución de la colección Postman con newman (`tests/`). |
| RNF-02 | Seguridad | Contraseñas cifradas, acceso con JWT y aislamiento total de las tareas de cada usuario. | 100 % de contraseñas con BCrypt; 0 accesos exitosos a tareas ajenas. | Pruebas de integración `*IT` (respuestas 401/403/404). |
| RNF-03 | Mantenibilidad | El código se mantiene simple y con bajo acoplamiento para facilitar cambios. | Complejidad ciclomática V(G) ≤ 10 por método y WMC ≤ 20 por clase. | Script `metricas/medir.ps1` (CK + lizard) y prueba ArchUnit de dependencias entre módulos. |

## Reglas de alcance

- Entidades únicas: `User` y `Task`. Enumeraciones: `Role {ADMIN, USER}` y `TaskStatus {PENDING, COMPLETED}`.
- La vista de calendario es una consulta; no existe entidad ni módulo de calendario.
- Fuera de alcance: categorías, etiquetas, prioridades, notificaciones, recordatorios, adjuntos, comentarios, equipos y proyectos.
