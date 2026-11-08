# Refactorización guiada por métricas

## Problema detectado

`TaskService` tenía el mayor WMC entre las clases con lógica de negocio (18 de un umbral de 20) y un CBO de 11 (límite 10).
La entidad `Task` tiene un WMC más alto (22), pero casi todo viene de accesores triviales y no se trató en esta iteración
(ver [resultados.md](resultados.md)). Además de los comandos sobre tareas, `TaskService` contenía la consulta de la vista de
calendario (RF-08) con su regla de validación de rango (máximo 42 días), uno de los dos métodos con mayor V(G) de la clase (3).

## Técnica aplicada

1. **Extraer clase:** la consulta del calendario pasa a `TaskCalendarService`.
2. **Introducir objeto de valor:** la regla del rango pasa a `CalendarRange.of(from, to)`, un `record` que solo puede
   crearse con un rango válido.
3. `TaskController` delega `GET /api/v1/tasks/calendar` en `TaskCalendarService`. El contrato de la API no cambia.

## Antes

```java
// TaskService (WMC 18, 13 métodos)
@Transactional(readOnly = true)
public List<TaskResponse> findCalendar(UUID ownerId, LocalDate from, LocalDate to) {
    if (to.isBefore(from) || ChronoUnit.DAYS.between(from, to) + 1 > MAX_CALENDAR_DAYS) {
        throw new BadRequestException("INVALID_DATE_RANGE", "...");
    }
    LocalDate today = today();
    return taskRepository.findByOwnerIdAndDueDateBetweenOrderByDueDateAscCreatedAtAsc(ownerId, from, to)
            .stream().map(task -> TaskResponse.from(task, today)).toList();
}
```

## Después

```java
// CalendarRange (WMC 3): la regla de negocio en un solo lugar
public record CalendarRange(LocalDate from, LocalDate to) {
    static final int MAX_DAYS = 42;
    public static CalendarRange of(LocalDate from, LocalDate to) {
        if (to.isBefore(from) || ChronoUnit.DAYS.between(from, to) + 1 > MAX_DAYS) {
            throw new BadRequestException("INVALID_DATE_RANGE", "...");
        }
        return new CalendarRange(from, to);
    }
}

// TaskCalendarService (WMC 2): solo la consulta del calendario
public List<TaskResponse> findCalendar(UUID ownerId, LocalDate from, LocalDate to) {
    CalendarRange range = CalendarRange.of(from, to);
    LocalDate today = LocalDate.now(clock);
    return taskRepository
            .findByOwnerIdAndDueDateBetweenOrderByDueDateAscCreatedAtAsc(ownerId, range.from(), range.to())
            .stream().map(task -> TaskResponse.from(task, today)).toList();
}
// TaskService queda con WMC 15 y 12 métodos.
```

## Verificación

- `mvn -B verify` en verde (42 pruebas): pruebas unitarias, de integración (`TaskControllerIT.calendar_returnsOnlyDatedTasksInRange`)
  y `ArchitectureTest`.
- Nuevas pruebas: `TaskCalendarServiceTest` (rango inválido, rango de 42 días aceptado, límites de `CalendarRange`).
- Nueva medición con `.\metricas\medir.ps1 -Etiqueta despues`; comparación en [resultados.md](resultados.md).

## Resultado

| Métrica de `TaskService` | Antes | Después |
|---|---|---|
| WMC | 18 | 15 |
| CBO | 11 | 10 |
| RFC | 31 | 25 |
| LCOM | 2 | 6 |

El WMC máximo del backend sigue siendo 22 (`Task`), por lo que RNF-03 sigue sin cumplirse. CK 0.7.0 no reporta `CalendarRange`
por ser un `record`; su WMC (3) se toma de lizard.
