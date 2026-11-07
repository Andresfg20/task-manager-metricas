package co.edu.corhuila.taskmanager.common.security;

import java.io.IOException;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import co.edu.corhuila.taskmanager.common.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Writes 401 and 403 responses raised by the security filter chain with the
 * uniform error body (07-api/error-handling.md).
 */
@Component
public class JsonSecurityErrorHandler implements AuthenticationEntryPoint, AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    public JsonSecurityErrorHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException authException) throws IOException {
        write(response, HttpStatus.UNAUTHORIZED,
                new ErrorResponse("UNAUTHORIZED", "Authentication is required.", List.of()));
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
            AccessDeniedException accessDeniedException) throws IOException {
        write(response, HttpStatus.FORBIDDEN,
                new ErrorResponse("FORBIDDEN", "You do not have permission to perform this action.", List.of()));
    }

    private void write(HttpServletResponse response, HttpStatus status, ErrorResponse body) throws IOException {
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getOutputStream(), body);
    }
}
