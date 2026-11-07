package co.edu.corhuila.taskmanager.tasks.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;

/** Body of PATCH /tasks/{id}/due-date: reschedule (RF-17). */
public record TaskDueDateRequest(@NotNull(message = "must not be null") LocalDate dueDate) {
}
