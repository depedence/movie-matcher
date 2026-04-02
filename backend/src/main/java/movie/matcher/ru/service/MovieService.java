package movie.matcher.ru.service;

import lombok.RequiredArgsConstructor;
import movie.matcher.ru.entity.Genre;
import movie.matcher.ru.entity.Movie;
import movie.matcher.ru.entity.MovieSwipe;
import movie.matcher.ru.entity.SwipeType;
import movie.matcher.ru.entity.dto.MovieDto;
import movie.matcher.ru.mapper.MovieMapper;
import movie.matcher.ru.omdb.OmdbClient;
import movie.matcher.ru.repository.MovieRepository;
import movie.matcher.ru.repository.MovieSwipeRepository;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.*;

@Service
@RequiredArgsConstructor
public class MovieService {

    private final MovieSwipeRepository swipeRepository;
    private final MovieRepository movieRepository;
    private final MovieMapper movieMapper;
    private final OmdbClient omdbClient;
    private final ObjectMapper objectMapper;

    public List<MovieDto> getFeed(Long userId) {
        Set<String> seenMovies = getSeenMovies(userId);
        List<MovieSwipe> likes = swipeRepository.findByUserIdAndSwipeType(userId, SwipeType.LIKE);

        if (likes.isEmpty()) {
            return randomFeed(seenMovies);
        } else {
            return preferenceFeed(likes, seenMovies);
        }
    }

    private List<MovieDto> randomFeed(Set<String> seenMovies) {
        List<MovieDto> feed = new ArrayList<>();

        String keyword = randomKeyword();
        String json = omdbClient.searchMovies(keyword, 1);

        try {
            JsonNode root = objectMapper.readTree(json);

            if (!root.has("Search")) {
                return feed;
            }

            JsonNode searchResults = root.path("Search");

            if (searchResults.isArray()) {
                for (JsonNode node : searchResults) {

                    if (feed.size() >= 10) {
                        break;
                    }

                    String imdbId = node.path("imdbID").asString();
                    if (seenMovies.contains(imdbId)) {
                        continue;
                    }
                    String detailJson = omdbClient.getMovieById(imdbId);
                    JsonNode details = objectMapper.readTree(detailJson);

                    if (!"True".equals(details.path("Response").asString())) {
                        continue;
                    }

                    feed.add(buildMovieDto(details, imdbId));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return feed;
    }

    private List<MovieDto> preferenceFeed(List<MovieSwipe> likes, Set<String> seenMovies) {
        Map<Genre, Integer> genreCount = new HashMap<>();

        for (MovieSwipe swipe : likes) {
            try {
                String json = omdbClient.getMovieById(swipe.getImdbId());
                JsonNode details = objectMapper.readTree(json);

                List<Genre> genres = parseGenres(details.path("Genre").asString());

                for (Genre genre : genres) {
                    genreCount.put(genre, genreCount.getOrDefault(genre, 0) + 1);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (genreCount.isEmpty()) {
            return randomFeed(seenMovies);
        }

        List<Genre> topGenres = genreCount.entrySet()
                .stream()
                .sorted(Map.Entry.<Genre, Integer>comparingByValue().reversed())
                .limit(3)
                .map(Map.Entry::getKey)
                .toList();
        Genre selectedGenre = topGenres.get((int) (Math.random() * topGenres.size()));

        return genreFeed(selectedGenre, seenMovies);
    }

    private List<MovieDto> genreFeed(Genre genre, Set<String> seenMovies) {
        String keyword = genreKeyword(genre);

        if (keyword == null) {
            return randomFeed(seenMovies);
        }

        List<MovieDto> feed = new ArrayList<>();

        String json = omdbClient.searchMovies(keyword, 1);

        try {
            JsonNode root = objectMapper.readTree(json);

            if (!root.has("Search")) {
                return randomFeed(seenMovies);
            }

            JsonNode searchResults = root.path("Search");

            for (JsonNode node : searchResults) {
                if (feed.size() >= 10) {
                    break;
                }

                String imdbId = node.path("imdbID").asString();
                if (seenMovies.contains(imdbId)) {
                    continue;
                }
                String detailsJson = omdbClient.getMovieById(imdbId);
                JsonNode details = objectMapper.readTree(detailsJson);

                if (!"True".equals(details.path("Response").asString())) {
                    continue;
                }

                feed.add(buildMovieDto(details, imdbId));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return feed;
    }

    private Set<String> getSeenMovies(Long userId) {
        List<MovieSwipe> swipes = swipeRepository.findByUserId(userId);
        Set<String> seen = new HashSet<>();
        for (MovieSwipe swipe : swipes) {
            seen.add(swipe.getImdbId());
        }
        return seen;
    }

    private String genreKeyword(Genre genre) {
        return switch (genre) {
            case ACTION -> "war";
            case ADVENTURE -> "quest";
            case COMEDY -> "fun";
            case DRAMA -> "life";
            case HORROR -> "dead";
            case THRILLER -> "killer";
            case FANTASY -> "magic";
            case ANIMATION -> "cartoon";
            case CRIME -> "crime";
            default -> randomKeyword();
        };
    }

    private MovieDto buildMovieDto(JsonNode details, String imdbId) {
        Movie movie = movieRepository.findByImdbId(imdbId).orElse(null);
        if (movie == null) {
            movie = new Movie();
            movie.setImdbId(imdbId);
            movie.setTitle(details.path("Title").asString());
            movie.setReleaseYear(parseYear(details.path("Year").asString()));
            movie.setDescription(details.path("Plot").asString());
            movie.setPosterUrl(details.path("Poster").asString());
            movie.setRating(parseRating(details.path("imdbRating").asString()));
            movie.setGenres(parseGenres(details.path("Genre").asString()));
            movieRepository.save(movie);
        }
        return movieMapper.toDto(movie);
    }

    private Integer parseYear(String yearStr) {
        try {
            return Integer.parseInt(yearStr.substring(0, 4));
        } catch (Exception e) {
            return null;
        }
    }

    private Double parseRating(String ratingStr) {
        try {
            if (ratingStr == null || ratingStr.equals("N/A")) {
                return null;
            }
            return Double.parseDouble(ratingStr);
        } catch (Exception e) {
            return null;
        }
    }

    private List<Genre> parseGenres(String genresStr) {
        try {
            if (genresStr == null || genresStr.equals("N/A")) {
                return List.of();
            }
            List<Genre> genreList = new ArrayList<>();
            String[] genreNames = genresStr.split(",\\s*");
            for (String genreName : genreNames) {
                Genre genre = GENRE_MAP.get(genreName.toUpperCase());
                if (genre != null) {
                    genreList.add(genre);
                }
            }
            return genreList;
        } catch (Exception e) {
            return List.of();
        }
    }

    private static final Map<String, Genre> GENRE_MAP = Map.of(
            "ACTION", Genre.ACTION,
            "ADVENTURE", Genre.ADVENTURE,
            "COMEDY", Genre.COMEDY,
            "DRAMA", Genre.DRAMA,
            "HORROR", Genre.HORROR,
            "THRILLER", Genre.THRILLER,
            "FANTASY", Genre.FANTASY,
            "ANIMATION", Genre.ANIMATION,
            "CRIME", Genre.CRIME
    );

    private String randomKeyword() {
        List<String> keywords = List.of("love", "star", "man", "woman", "dead", "night", "day", "time");
        return keywords.get((int) (Math.random() * keywords.size()));
    }

}