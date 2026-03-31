package movie.matcher.ru.service;

import lombok.RequiredArgsConstructor;
import movie.matcher.ru.entity.dto.MovieDto;
import movie.matcher.ru.omdb.OmdbClient;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MovieService {

    private final OmdbClient omdbClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public List<MovieDto> getFeed() {
        List<MovieDto> feed = new ArrayList<>();

        String keyword = "star";
        String json = omdbClient.searchMovies(keyword, 1);

        try {
            JsonNode root = objectMapper.readTree(json);
            JsonNode searchResults = root.path("Search");

            if (searchResults.isArray()) {
                for (JsonNode node : searchResults) {
                    MovieDto dto = new MovieDto();

                    dto.setTitle(node.path("Title").asString());
                    dto.setReleaseYear(parseYear(node.path("Year").asString()));
                    dto.setPosterUrl(node.path("Poster").asString());
                    dto.setTmdbId(node.path("imdbID").asString());

                    dto.setGenres(List.of());

                    feed.add(dto);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return feed;
    }

    private Integer parseYear(String yearStr) {
        try {
            return Integer.parseInt(yearStr.substring(0, 4));
        } catch (Exception e) {
            return null;
        }
    }

}