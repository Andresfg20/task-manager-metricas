package co.edu.corhuila.taskmanager.common.security;

import java.util.UUID;

/**
 * Identity carried by a valid JWT: user id, email and role name.
 */
public record AuthenticatedUser(UUID id, String email, String role) {
}
