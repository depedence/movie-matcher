package movie.matcher.ru.controller;

import lombok.RequiredArgsConstructor;
import movie.matcher.ru.entity.Movie;
import movie.matcher.ru.entity.dto.MovieDto;
import movie.matcher.ru.entity.response.MessageResponse;
import movie.matcher.ru.service.MovieService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/movies")
@RequiredArgsConstructor
public class MovieRestController {

    private final MovieService movieService;

    @GetMapping("/feed")
    public List<MovieDto> getFeed(@RequestParam Long userId) {
        return movieService.getFeed(userId);
    }

    @GetMapping
    public List<Movie> getAllMovies() {
        return movieService.getAllMovies();
    }

    @DeleteMapping
    public ResponseEntity<MessageResponse> deleteAllMovies() {
        movieService.deleteAllMovies();
        return ResponseEntity.ok(new MessageResponse("Movies successfully deleted"));
    }
}