package movie.matcher.ru.exception;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ErrorResponse {

    private int status;
    private String message;
    private String errorCode;
    private LocalDateTime timestamp;
    private String path;

}