package movie.matcher.ru.support.setup;

import lombok.AllArgsConstructor;
import movie.matcher.ru.entity.User;
import movie.matcher.ru.models.request.AuthModel;
import movie.matcher.ru.repository.UserRepository;
import movie.matcher.ru.support.data.UserDataFactory;
import org.springframework.security.crypto.password.PasswordEncoder;

@AllArgsConstructor
public class UserFixture {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthModel createTestUser() {
        AuthModel user = UserDataFactory.randomAuthUser();

        User entity = new User();
        entity.setUsername(user.getUsername());
        entity.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(entity);

        return user;
    }
}