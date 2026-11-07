package co.edu.corhuila.taskmanager.common.exception;

import org.springframework.http.HttpStatus;

/**
 * Wrong email or password. The message never says which one (BR-61).
 */
public class InvalidCredentialsException extends ApiException {

    public InvalidCredentialsException() {
        super(HttpStatus.UNAUTHORIZED, "INVALID_CREDENTIALS", "Invalid email or password.");
    }
}
