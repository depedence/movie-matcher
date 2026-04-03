package movie.matcher.ru.exception;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {

    private final ExceptionType exceptionType;

    public BusinessException(ExceptionType exceptionType) {
        super(exceptionType.getMessage());
        this.exceptionType = exceptionType;
    }

    public BusinessException(ExceptionType exceptionType, String additionalInfo) {
        super(exceptionType.getMessage() + ": " + additionalInfo);
        this.exceptionType = exceptionType;
    }

}