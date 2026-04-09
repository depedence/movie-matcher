package movie.matcher.ru.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import movie.matcher.ru.entity.dto.UserDto;
import movie.matcher.ru.entity.request.CreateUserRequest;
import movie.matcher.ru.entity.request.EditUserRequest;
import movie.matcher.ru.entity.response.MessageResponse;
import movie.matcher.ru.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserRestController {

    private final UserService userService;

    @PostMapping
    public UserDto createUser(@RequestBody @Valid CreateUserRequest request) {
        return userService.createUser(request);
    }

    @GetMapping("/{id}")
    public UserDto getUser(@PathVariable Long id) {
        return userService.getUser(id);
    }

    @GetMapping
    public List<UserDto> getAllUsers() {
        return userService.getAllUsers();
    }

    @PutMapping("/{id}")
    public UserDto editUser(@PathVariable Long id, @RequestBody @Valid EditUserRequest request) {
        return userService.editUser(request, id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(new MessageResponse("User successfully deleted"));
    }

}