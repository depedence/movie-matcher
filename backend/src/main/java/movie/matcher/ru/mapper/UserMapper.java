package movie.matcher.ru.mapper;

import movie.matcher.ru.entity.User;
import movie.matcher.ru.entity.dto.UserDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDto toDto(User user);

    User toEntity(UserDto dto);

}