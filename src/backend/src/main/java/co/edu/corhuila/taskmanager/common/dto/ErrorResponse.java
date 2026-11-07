package co.edu.corhuila.taskmanager.common.dto;

import java.util.List;

/**
 * Uniform error body of every non-2xx response (07-api/error-handling.md).
 */
public record ErrorResponse(String code, String message, List<ErrorDetail> details) {

    public record ErrorDetail(String field, String message) {
    }
}
