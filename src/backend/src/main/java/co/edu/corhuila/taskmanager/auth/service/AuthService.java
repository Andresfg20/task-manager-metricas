package co.edu.corhuila.taskmanager.auth.service;

import org.springframework.stereotype.Service;

import co.edu.corhuila.taskmanager.auth.dto.AuthResponse;
import co.edu.corhuila.taskmanager.auth.dto.LoginRequest;
import co.edu.corhuila.taskmanager.auth.dto.RegisterRequest;
import co.edu.corhuila.taskmanager.common.security.JwtService;
import co.edu.corhuila.taskmanager.users.dto.UserResponse;
import co.edu.corhuila.taskmanager.users.service.UserService;

/**
 * Registers and logs in users and issues their JWT (RF-01, RF-02, ADR-003).
 * It stores nothing itself: users belong to the users module.
 */
@Service
public class AuthService {

    private final UserService userService;
    private final JwtService jwtService;

    public AuthService(UserService userService, JwtService jwtService) {
        this.userService = userService;
        this.jwtService = jwtService;
    }

    public AuthResponse register(RegisterRequest request) {
        return toAuthResponse(userService.register(request.email(), request.password()));
    }

    public AuthResponse login(LoginRequest request) {
        return toAuthResponse(userService.authenticate(request.email(), request.password()));
    }

    private AuthResponse toAuthResponse(UserResponse user) {
        String token = jwtService.generateToken(user.id(), user.email(), user.role().name());
        return new AuthResponse(token, "Bearer", jwtService.getExpirationSeconds(), user);
    }
}
