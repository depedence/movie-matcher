package movie.matcher.ru.data;

import lombok.AllArgsConstructor;
import movie.matcher.ru.repository.UserRepository;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class DatabaseCleaner {

    protected UserRepository userRepository;

    public void cleanDb() {
        userRepository.deleteAll();
    }

}