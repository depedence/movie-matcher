package movie.matcher.ru.service;

import lombok.RequiredArgsConstructor;
import movie.matcher.ru.entity.User;
import movie.matcher.ru.entity.dto.UserDto;
import movie.matcher.ru.entity.request.UserRequest;
import movie.matcher.ru.exception.BusinessException;
import movie.matcher.ru.exception.ExceptionType;
import movie.matcher.ru.mapper.UserMapper;
import movie.matcher.ru.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;
    private final UserMapper userMapper;

    public UserDto createUser(UserRequest request) {
        if (repository.existsByUsername(request.getUsername())) {
            throw new BusinessException(ExceptionType.USERNAME_ALREADY_EXISTS, request.getUsername());
        }
        User user = new User();
        user.setUsername(request.getUsername());
        repository.save(user);
        return userMapper.toDto(user);
    }

    public UserDto getUser(Long id) {
        User user = repository.findById(id)
                .orElseThrow(() -> new BusinessException(ExceptionType.USER_NOT_FOUND, "id: " + id));
        return userMapper.toDto(user);
    }

    public UserDto editUser(UserRequest request, Long id) {
        User user = repository.findById(id)
                .orElseThrow(() -> new BusinessException(ExceptionType.USER_NOT_FOUND, "id: " + id));
        if (repository.existsByUsername(request.getUsername())) {
            throw new BusinessException(ExceptionType.USERNAME_ALREADY_EXISTS, request.getUsername());
        }
        user.setUsername(request.getUsername());
        repository.save(user);
        return userMapper.toDto(user);
    }

    public void deleteUser(Long id) {
        repository.deleteById(id);
    }

}