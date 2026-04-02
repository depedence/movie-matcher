package movie.matcher.ru.omdb;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class OmdbClient {

    @Value("${omdb.apiKey}")
    private String apiKey;

    @Value("${omdb.url}")
    private String url;

    private final RestTemplate restTemplate = new RestTemplate();

    public String searchMovies(String keyword, int page) {
        String requestUrl = String.format(
                "%s/?apikey=%s&s=%s&type=movie&page=%d",
                url, apiKey, keyword, page
        );

        return restTemplate.getForObject(requestUrl, String.class);
    }

    public String getMovieById(String imdbId) {
        String requestUrl = String.format(
                "%s/?apikey=%s&i=%s&plot=full",
                url, apiKey, imdbId
        );

        return restTemplate.getForObject(requestUrl, String.class);
    }

}