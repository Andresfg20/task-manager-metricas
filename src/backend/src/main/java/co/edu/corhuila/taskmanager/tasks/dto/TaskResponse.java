package co.edu.corhuila.taskmanager.tasks.dto;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

import co.edu.corhuila.taskmanager.tasks.entity.Task;
import co.edu.corhuila.taskmanager.tasks.entity.TaskStatus;

/**
 * Public view of a task. "overdue" is computed with today's date, never stored (BR-20).
 */
public record TaskResponse(
        UUID id,
        String title,
        String description,
        TaskStatus status,
        LocalDate dueDate,
        Instant completedAt,
        boolean overdue,
        Instant createdAt,
        Instant updatedAt) {

    public static TaskResponse from(Task task, LocalDate today) {
        return new TaskResponse(task.getId(), task.getTitle(), task.getDescription(), task.getStatus(),
                task.getDueDate(), task.getCompletedAt(), task.isOverdue(today),
                task.getCreatedAt(), task.getUpdatedAt());
    }
}
