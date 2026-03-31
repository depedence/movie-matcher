package movie.matcher.ru.controller;

import lombok.RequiredArgsConstructor;
import movie.matcher.ru.entity.dto.MovieDto;
import movie.matcher.ru.service.MovieService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/movies")
@RequiredArgsConstructor
public class MovieRestController {

    private final MovieService movieService;

    @GetMapping("/feed")
    public List<MovieDto> getFeed() {
        return movieService.getFeed();
    }

}