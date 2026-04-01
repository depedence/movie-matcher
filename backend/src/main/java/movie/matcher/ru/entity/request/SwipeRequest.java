package movie.matcher.ru.entity.request;

import lombok.Data;
import movie.matcher.ru.entity.SwipeType;

@Data
public class SwipeRequest {

    private Long userId;
    private String imdbId;
    private SwipeType swipeType;

}