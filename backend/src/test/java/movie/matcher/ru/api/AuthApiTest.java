package movie.matcher.ru.api;

import movie.matcher.ru.base.BaseApiTest;
import movie.matcher.ru.client.AuthClient;
import movie.matcher.ru.data.UserDataFactory;
import movie.matcher.ru.models.request.AuthModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class AuthApiTest extends BaseApiTest {

    private AuthClient client;

    @BeforeEach
    void setUp() {
        client = new AuthClient(requestSpec);
    }

    @Test
    void register_success__returns200() {
        AuthModel body = UserDataFactory.randomUser();
        client.register(body)
                .then()
                .statusCode(200);
    }

    @Test
    void register_usernameAlreadyExists__returns409() {
        AuthModel body = UserDataFactory.randomUser();
        client.register(body)
                .then()
                .statusCode(200);
        client.register(body)
                .then()
                .statusCode(409);
    }

    @Test
    void register_withoutBody__returns400() {
        client.registerWithEmptyBody()
                .then()
                .statusCode(400);
    }
}