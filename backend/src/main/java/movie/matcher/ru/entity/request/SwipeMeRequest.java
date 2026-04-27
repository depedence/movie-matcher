package movie.matcher.ru.entity.request;

import lombok.Data;
import movie.matcher.ru.entity.enums.SwipeType;

@Data
public class SwipeMeRequest {

    private String imdbId;
    private SwipeType swipeType;
}
