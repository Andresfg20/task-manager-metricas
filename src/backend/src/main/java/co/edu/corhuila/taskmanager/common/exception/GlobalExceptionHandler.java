package co.edu.corhuila.taskmanager.common.exception;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import co.edu.corhuila.taskmanager.common.dto.ErrorResponse;
import co.edu.corhuila.taskmanager.common.dto.ErrorResponse.ErrorDetail;

/**
 * Maps every exception to the uniform error body (07-api/error-handling.md).
 * Unexpected errors are logged and answered without internals (RNF-14).
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ErrorResponse> handleApi(ApiException ex) {
        return build(ex.getStatus(), ex.getCode(), ex.getMessage(), List.of());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        List<ErrorDetail> details = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> new ErrorDetail(error.getField(), error.getDefaultMessage()))
                .toList();
        return build(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "The request has invalid fields.", details);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingParameter(MissingServletRequestParameterException ex) {
        return build(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "The request has invalid fields.",
                List.of(new ErrorDetail(ex.getParameterName(), "is required")));
    }

    @ExceptionHandler({HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class})
    public ResponseEntity<ErrorResponse> handleMalformed(Exception ex) {
        return build(HttpStatus.BAD_REQUEST, "MALFORMED_REQUEST",
                "The request could not be read. Check the JSON, enum values and date format (YYYY-MM-DD).",
                List.of());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex) {
        return build(HttpStatus.FORBIDDEN, "FORBIDDEN", "You do not have permission to perform this action.",
                List.of());
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoResource(NoResourceFoundException ex) {
        return build(HttpStatus.NOT_FOUND, "NOT_FOUND", "The requested route does not exist.", List.of());
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMethodNotAllowed(HttpRequestMethodNotSupportedException ex) {
        return build(HttpStatus.METHOD_NOT_ALLOWED, "METHOD_NOT_ALLOWED",
                "This HTTP method is not supported on this route.", List.of());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpected(Exception ex) {
        log.error("Unexpected error", ex);
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR", "An unexpected error occurred.",
                List.of());
    }

    private ResponseEntity<ErrorResponse> build(HttpStatus status, String code, String message,
            List<ErrorDetail> details) {
        return ResponseEntity.status(status).body(new ErrorResponse(code, message, details));
    }
}
