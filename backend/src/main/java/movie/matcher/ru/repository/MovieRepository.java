package movie.matcher.ru.repository;

import movie.matcher.ru.entity.Genre;
import movie.matcher.ru.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface MovieRepository extends JpaRepository<Movie, Long> {

    List<Movie> findByGenresContains(Genre genre);

    Optional<Movie> findByImdbId(String imdbId);

    @Modifying
    @Query(value = "DELETE FROM movie_genres", nativeQuery = true)
    void deleteAllGenres();

}