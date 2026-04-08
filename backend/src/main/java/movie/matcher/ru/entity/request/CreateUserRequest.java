package movie.matcher.ru.entity.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateUserRequest {

    @NotNull(message = "Username can not be null")
    @NotBlank(message = "Username can not be blank")
    String username;

    @NotBlank(message = "Password can not be blank")
    @NotNull(message = "Password can not be blank")
    String password;

}