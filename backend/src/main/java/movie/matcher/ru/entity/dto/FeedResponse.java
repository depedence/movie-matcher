package movie.matcher.ru.entity.dto;

import java.util.List;

public record FeedResponse(
        MovieDto currentMovie,
        List<MovieDto> recentLikes
) {}