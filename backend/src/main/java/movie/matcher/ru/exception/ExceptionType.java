package movie.matcher.ru.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ExceptionType {

    USERNAME_ALREADY_EXISTS("Username already exists", HttpStatus.CONFLICT),
    USER_NOT_FOUND("User not found", HttpStatus.NOT_FOUND),
    INVALID_CREDENTIALS("Invalid credentials", HttpStatus.UNAUTHORIZED),
    ACCESS_DENIED("Access denied", HttpStatus.FORBIDDEN);

    private final String message;
    private final HttpStatus status;

    ExceptionType(String message, HttpStatus status) {
        this.message = message;
        this.status = status;
    }

}