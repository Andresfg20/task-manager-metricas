package co.edu.corhuila.taskmanager.tasks.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Body of POST and PUT /tasks (BR-02 to BR-04). Any status sent is ignored (BR-05).
 */
public record TaskRequest(
        @NotBlank(message = "must not be blank")
        @Size(max = 120, message = "must have at most 120 characters")
        String title,

        @Size(max = 1000, message = "must have at most 1000 characters")
        String description,

        LocalDate dueDate) {
}
