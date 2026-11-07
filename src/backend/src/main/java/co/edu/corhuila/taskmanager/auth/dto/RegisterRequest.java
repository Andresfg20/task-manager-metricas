package co.edu.corhuila.taskmanager.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Registration data (BR-60, BR-61).
 */
public record RegisterRequest(
        @NotBlank(message = "must not be blank")
        @Email(message = "must be a valid email address")
        @Size(max = 254, message = "must have at most 254 characters")
        String email,

        @NotBlank(message = "must not be blank")
        @Size(min = 8, max = 72, message = "must have 8 to 72 characters")
        @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d).+$",
                message = "must contain at least one letter and one digit")
        String password) {
}
