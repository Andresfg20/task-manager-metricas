package co.edu.corhuila.taskmanager.auth.dto;

import co.edu.corhuila.taskmanager.users.dto.UserResponse;

/**
 * Token returned by register and login (07-api/authentication.md).
 */
public record AuthResponse(String token, String tokenType, long expiresIn, UserResponse user) {
}
