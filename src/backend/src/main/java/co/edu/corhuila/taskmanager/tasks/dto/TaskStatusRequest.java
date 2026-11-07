package co.edu.corhuila.taskmanager.tasks.dto;

import co.edu.corhuila.taskmanager.tasks.entity.TaskStatus;
import jakarta.validation.constraints.NotNull;

/** Body of PATCH /tasks/{id}/status: complete or reopen (RF-08, RF-09). */
public record TaskStatusRequest(@NotNull(message = "must not be null") TaskStatus status) {
}
