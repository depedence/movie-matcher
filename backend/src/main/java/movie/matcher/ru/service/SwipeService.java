package movie.matcher.ru.service;

import lombok.RequiredArgsConstructor;
import movie.matcher.ru.entity.MovieSwipe;
import movie.matcher.ru.entity.SwipeType;
import movie.matcher.ru.entity.request.SwipeRequest;
import movie.matcher.ru.repository.MovieSwipeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SwipeService {

    private final MovieSwipeRepository repository;

    public void saveSwipe(SwipeRequest request) {
        MovieSwipe swipe = repository
                .findByUserIdAndImdbId(request.getUserId(), request.getImdbId())
                .orElseGet(MovieSwipe::new);

        swipe.setUserId(request.getUserId());
        swipe.setImdbId(request.getImdbId());
        swipe.setSwipeType(request.getSwipeType());

        repository.save(swipe);
    }

    public List<MovieSwipe> getUserSwipes(Long userId) {
        return repository.findByUserId(userId);
    }

    public List<MovieSwipe> getUserLikes(Long userId) {
        return repository.findByUserIdAndSwipeType(userId, SwipeType.LIKE);
    }

}