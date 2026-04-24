package movie.matcher.ru.auth;

import lombok.RequiredArgsConstructor;
import movie.matcher.ru.exception.BusinessException;
import movie.matcher.ru.exception.ExceptionType;
import movie.matcher.ru.repository.UserRepository;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @NonNull
    public UserDetails loadUserByUsername(@NonNull String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException(ExceptionType.USER_NOT_FOUND, username));
    }
}