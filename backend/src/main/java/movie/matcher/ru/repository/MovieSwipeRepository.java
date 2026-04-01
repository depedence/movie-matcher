package movie.matcher.ru.repository;

import movie.matcher.ru.entity.MovieSwipe;
import movie.matcher.ru.entity.SwipeType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MovieSwipeRepository extends JpaRepository<MovieSwipe, Long> {

    List<MovieSwipe> findByUserId(Long userId);

    List<MovieSwipe> findByUserIdAndSwipeType(Long userId, SwipeType swipeType);

    Optional<MovieSwipe> findByUserIdAndImdbId(Long userId, String imdbId);

}