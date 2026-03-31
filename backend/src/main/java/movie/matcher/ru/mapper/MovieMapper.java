package movie.matcher.ru.mapper;

import movie.matcher.ru.entity.Movie;
import movie.matcher.ru.entity.dto.MovieDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MovieMapper {

    MovieDto toDto(Movie movie);

    Movie toEntity(MovieDto dto);

    List<MovieDto> toDtoList(List<Movie> movies);

}