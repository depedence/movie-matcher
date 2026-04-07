package movie.matcher.ru.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

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

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex, WebRequest request) {
        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));

        ErrorResponse error = ErrorResponse.builder()
                .status(400)
                .message(message)
                .errorCode("VALIDATION_ERROR")
                .timestamp(LocalDateTime.now())
                .path(getPath(request))
                .build();
        return ResponseEntity
                .badRequest()
                .body(error);
    }

    public String getPath(WebRequest request) {
        return request.getDescription(false).replace("uri=", "");
    }

}