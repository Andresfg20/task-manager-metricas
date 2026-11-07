package co.edu.corhuila.taskmanager.users.dto;

import java.time.Instant;
import java.util.UUID;

import co.edu.corhuila.taskmanager.users.entity.Role;
import co.edu.corhuila.taskmanager.users.entity.User;

/**
 * Public view of a user. It never includes the password hash (BR-33).
 */
public record UserResponse(UUID id, String email, Role role, Instant createdAt) {

    public static UserResponse from(User user) {
        return new UserResponse(user.getId(), user.getEmail(), user.getRole(), user.getCreatedAt());
    }
}
