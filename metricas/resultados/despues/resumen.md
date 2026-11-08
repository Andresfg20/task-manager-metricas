# Resumen de métricas — despues

Clases medidas: 28 · Métodos medidos: 106 · LOC total: 509

## Clases (ordenadas por WMC)

| Clase | WMC | CBO | RFC | LCOM | DIT | NOC | LOC | Estado |
|---|---|---|---|---|---|---|---|---|
| Task | 22 | 10 | 2 | 93 | 1 | 0 | 80 | CRÍTICO |
| TaskService | 15 | 10 | 25 | 6 | 1 | 0 | 59 | OK |
| UserService | 10 | 10 | 20 | 1 | 1 | 0 | 35 | OK |
| User | 10 | 10 | 1 | 31 | 1 | 0 | 41 | OK |
| GlobalExceptionHandler | 9 | 16 | 18 | 36 | 1 | 0 | 32 | CRÍTICO |
| TaskController | 9 | 27 | 15 | 0 | 1 | 0 | 35 | CRÍTICO |
| JwtService | 6 | 5 | 23 | 0 | 1 | 0 | 30 | OK |
| AdminSeeder | 5 | 7 | 4 | 0 | 1 | 0 | 20 | OK |
| JwtAuthenticationFilter | 4 | 8 | 12 | 0 | 2 | 0 | 19 | OK |
| AuthService | 4 | 8 | 10 | 0 | 1 | 0 | 18 | OK |
| JsonSecurityErrorHandler | 4 | 11 | 7 | 4 | 1 | 0 | 17 | CRÍTICO |
| TaskRepository | 3 | 7 | 0 | 3 | 1 | 0 | 5 | OK |
| ApiException | 3 | 1 | 0 | 0 | 4 | 4 | 15 | OK |
| AuthController | 3 | 13 | 2 | 0 | 1 | 0 | 12 | CRÍTICO |
| SecurityConfig | 3 | 16 | 28 | 3 | 1 | 0 | 22 | CRÍTICO |
| CurrentUser | 3 | 3 | 4 | 0 | 1 | 0 | 9 | OK |
| UserController | 2 | 11 | 1 | 0 | 1 | 0 | 9 | CRÍTICO |
| UserRepository | 2 | 2 | 0 | 1 | 1 | 0 | 4 | OK |
| TaskCalendarService | 2 | 5 | 8 | 0 | 1 | 0 | 13 | OK |
| ConflictException | 1 | 1 | 0 | 0 | 5 | 0 | 5 | OK |
| ClockConfig | 1 | 3 | 2 | 0 | 1 | 0 | 5 | OK |
| InvalidCredentialsException | 1 | 1 | 0 | 0 | 5 | 0 | 5 | OK |
| TaskManagerApplication | 1 | 3 | 1 | 0 | 1 | 0 | 5 | OK |
| NotFoundException | 1 | 1 | 0 | 0 | 5 | 0 | 5 | OK |
| BadRequestException | 1 | 1 | 0 | 0 | 5 | 0 | 5 | OK |
| TaskStatus | 0 | 0 | 0 | 0 | 1 | 0 | 1 | OK |
| Role | 0 | 0 | 0 | 0 | 1 | 0 | 1 | OK |
| OpenApiConfig | 0 | 5 | 0 | 0 | 1 | 0 | 2 | OK |

## Métodos con mayor complejidad ciclomática V(G)

| Clase | Método | V(G) | LOC |
|---|---|---|---|
| AdminSeeder | run/1[co.edu.corhuila.taskmanager.users.service.ApplicationArguments] | 4 | 9 |
| JwtAuthenticationFilter | doFilterInternal/3[co.edu.corhuila.taskmanager.common.security.HttpServletRequest,co.edu.corhuila.taskmanager.common.security.HttpServletResponse,co.edu.corhuila.taskmanager.common.security.FilterChain] | 3 | 12 |
| CurrentUser | id/0 | 3 | 7 |
| TaskService | normalizeDescription/1[java.lang.String] | 3 | 6 |
| Task | isOverdue/1[java.time.LocalDate] | 3 | 3 |
| UserService | createAdminIfMissing/2[java.lang.String,java.lang.String] | 2 | 7 |
| UserService | normalizeEmail/1[java.lang.String] | 2 | 3 |
| UserService | create/3[java.lang.String,java.lang.String,co.edu.corhuila.taskmanager.users.entity.Role] | 2 | 7 |
| TaskService | changeStatus/3[java.util.UUID,java.util.UUID,co.edu.corhuila.taskmanager.tasks.entity.TaskStatus] | 2 | 10 |
| JwtService | parse/1[java.lang.String] | 2 | 9 |
| JwtService | JwtService/3[java.lang.String,long,java.time.Clock] | 2 | 9 |
| Task | complete/1[java.time.Instant] | 2 | 7 |
| Task | reopen/0 | 2 | 7 |
| TaskRepository | findByIdAndOwnerId/2[java.util.UUID,java.util.UUID] | 1 | 1 |
| TaskRepository | search/6[java.util.UUID,co.edu.corhuila.taskmanager.tasks.entity.TaskStatus,boolean,co.edu.corhuila.taskmanager.tasks.entity.TaskStatus,java.time.LocalDate,co.edu.corhuila.taskmanager.tasks.repository.Pageable] | 1 | 1 |

Umbrales: V(G) ≤ 10 por método, WMC ≤ 20 por clase (CBO > 10 se marca como crítico, sin fallar).

Resultado RNF-03: NO CUMPLE — WMC 22 > 20 en co.edu.corhuila.taskmanager.tasks.entity.Task
