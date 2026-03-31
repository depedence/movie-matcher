package movie.matcher.ru.repository;

import movie.matcher.ru.entity.Genre;
import movie.matcher.ru.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MovieRepository extends JpaRepository<Movie, Long> {

    List<Movie> findByGenresContains(Genre genre);

}