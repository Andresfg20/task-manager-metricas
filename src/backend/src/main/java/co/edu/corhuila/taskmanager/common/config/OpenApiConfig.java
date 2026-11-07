package co.edu.corhuila.taskmanager.common.config;

import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;

/**
 * Swagger / OpenAPI metadata (ADR-002). Every endpoint requires the bearer token
 * unless it declares otherwise.
 */
@Configuration
@OpenAPIDefinition(
        info = @Info(title = "Task Manager API", version = "1.0.0",
                description = "REST API of Task Manager. See 07-api/ in the documentation."),
        security = @SecurityRequirement(name = "bearerAuth"))
@SecurityScheme(name = "bearerAuth", type = SecuritySchemeType.HTTP, scheme = "bearer", bearerFormat = "JWT")
public class OpenApiConfig {
}
