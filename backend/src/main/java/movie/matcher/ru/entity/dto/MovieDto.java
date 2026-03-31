package movie.matcher.ru.entity.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import movie.matcher.ru.entity.Genre;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class MovieDto {
    private Long id;
    private String title;
    private Integer releaseYear;
    private String description;
    private List<Genre> genres;
    private String posterUrl;
    private Double rating;
    private String tmdbId;
}