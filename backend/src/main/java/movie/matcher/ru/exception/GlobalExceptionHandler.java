package movie.matcher.ru.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException ex, WebRequest request) {
        ErrorResponse error = ErrorResponse.builder()
                .status(ex.getExceptionType().getStatus().value())
                .message(ex.getMessage())
                .errorCode(ex.getExceptionType().name())
                .timestamp(LocalDateTime.now())
                .path(getPath(request))
                .build();
        return ResponseEntity
                .status(ex.getExceptionType().getStatus())
                .body(error);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(WebRequest request) {
        ErrorResponse error = ErrorResponse.builder()
                .status(500)
                .message("Internal server error")
                .errorCode("INTERNAL_ERROR")
                .timestamp(LocalDateTime.now())
                .path(getPath(request))
                .build();
        return ResponseEntity
                .status(500)
                .body(error);
    }

    public String getPath(WebRequest request) {
        return request.getDescription(false).replace("uri=", "");
    }

}