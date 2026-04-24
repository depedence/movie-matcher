package movie.matcher.ru.service;

import lombok.RequiredArgsConstructor;
import movie.matcher.ru.entity.User;
import movie.matcher.ru.entity.dto.UserDto;
import movie.matcher.ru.entity.enums.UserRole;
import movie.matcher.ru.entity.request.CreateUserRequest;
import movie.matcher.ru.entity.request.EditUserRequest;
import movie.matcher.ru.exception.BusinessException;
import movie.matcher.ru.exception.ExceptionType;
import movie.matcher.ru.mapper.UserMapper;
import movie.matcher.ru.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserDto createUser(CreateUserRequest request) {
        if (repository.existsByUsername(request.getUsername())) {
            throw new BusinessException(ExceptionType.USERNAME_ALREADY_EXISTS, request.getUsername());
        }
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(UserRole.DEFAULT);
        repository.save(user);
        return userMapper.toDto(user);
    }

    public UserDto getUser(Long id) {
        User user = repository.findById(id)
                .orElseThrow(() -> new BusinessException(ExceptionType.USER_NOT_FOUND, "id: " + id));
        return userMapper.toDto(user);
    }

    public List<UserDto> getAllUsers() {
        return repository.findAll().stream()
                .map(userMapper::toDto)
                .toList();
    }

    public UserDto editUser(EditUserRequest request, Long id) {
        if (getCurrentUserId().equals(id)) {
            throw new BusinessException(ExceptionType.ACCESS_DENIED);
        }
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
        if (getCurrentUserId().equals(id)) {
            throw new BusinessException(ExceptionType.ACCESS_DENIED);
        }
        repository.deleteById(id);
    }

    private Long getCurrentUserId() {
        String username = Objects.requireNonNull(
                SecurityContextHolder
                        .getContext()
                        .getAuthentication())
                .getName();
        return repository.findByUsername(username)
                .orElseThrow(() -> new BusinessException(ExceptionType.USER_NOT_FOUND, username))
                .getId();
    }
}