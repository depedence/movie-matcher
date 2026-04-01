package movie.matcher.ru.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "movie_swipes")
@Getter
@Setter
@NoArgsConstructor
public class MovieSwipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "imdb_id", nullable = false)
    private String imdbId;

    @Column(name = "swipe_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private SwipeType swipeType;

}