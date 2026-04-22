package movie.matcher.ru.infra.db;

import lombok.AllArgsConstructor;
import movie.matcher.ru.repository.UserRepository;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class DatabaseCleaner {

    private UserRepository userRepository;

    public void cleanDb() {
        userRepository.deleteAll();
    }
}