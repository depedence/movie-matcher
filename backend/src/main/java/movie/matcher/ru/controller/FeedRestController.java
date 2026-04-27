package movie.matcher.ru.controller;

import lombok.RequiredArgsConstructor;
import movie.matcher.ru.entity.dto.FeedResponse;
import movie.matcher.ru.entity.request.SwipeMeRequest;
import movie.matcher.ru.service.MovieService;
import movie.matcher.ru.service.SwipeService;
import movie.matcher.ru.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/feed")
@RequiredArgsConstructor
public class FeedRestController {

    private final MovieService movieService;
    private final SwipeService swipeService;
    private final UserService userService;

    @GetMapping("/me")
    public FeedResponse getFeed(Authentication auth) {
        Long userId = userService.getCurrentUserId();
        return movieService.getFeedMe(userId);
    }

    @PostMapping("/me/swipe")
    public FeedResponse swipe(@RequestBody SwipeMeRequest request) {
        Long userId = userService.getCurrentUserId();
        swipeService.saveSwipeForUser(userId, request.getImdbId(), request.getSwipeType());
        return movieService.swipeAndGetNext(userId, request.getImdbId());
    }
}