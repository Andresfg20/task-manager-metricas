package co.edu.corhuila.taskmanager.common.exception;

import org.springframework.http.HttpStatus;

/**
 * Base of the expected errors a service can raise. The handler turns it into
 * the error body with its code and HTTP status.
 */
public class ApiException extends RuntimeException {

    private final HttpStatus status;
    private final String code;

    public ApiException(HttpStatus status, String code, String message) {
        super(message);
        this.status = status;
        this.code = code;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getCode() {
        return code;
    }
}
