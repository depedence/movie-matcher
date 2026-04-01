package movie.matcher.ru.controller;

import lombok.RequiredArgsConstructor;
import movie.matcher.ru.entity.MovieSwipe;
import movie.matcher.ru.entity.request.SwipeRequest;
import movie.matcher.ru.service.SwipeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/swipes")
@RequiredArgsConstructor
public class SwipeRestController {

    private final SwipeService swipeService;

    @PostMapping
    public ResponseEntity<Void> saveSwipe(@RequestBody SwipeRequest request) {
        swipeService.saveSwipe(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<MovieSwipe>> getUserSwipes(@PathVariable Long userId) {
        return ResponseEntity.ok(swipeService.getUserSwipes(userId));
    }

    @GetMapping("/user/{userId}/likes")
    public ResponseEntity<List<MovieSwipe>> getUserLikes(@PathVariable Long userId) {
        return ResponseEntity.ok(swipeService.getUserLikes(userId));
    }

}