package movie.matcher.ru.api.tests;

import movie.matcher.ru.infra.api.BaseApiTest;
import movie.matcher.ru.api.client.AuthClient;
import movie.matcher.ru.support.data.UserDataFactory;
import movie.matcher.ru.models.request.AuthModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class AuthApiTest extends BaseApiTest {

    private AuthClient client;
    private AuthModel body;

    @BeforeEach
    void setUp() {
        databaseCleaner.cleanDb();
        client = new AuthClient(requestSpec);
        body = UserDataFactory.randomAuthUser();
    }

    // -- Register -----

    @Test
    void register_success__returns200() {
        client.register(body)
                .then()
                .statusCode(200);
    }

    @Test
    void register_usernameAlreadyExists__returns409() {
        registerBeforeTest();
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

    // -- Login --------

    @Test
    void login_success__returns200() {
        registerBeforeTest();
        client.login(body)
                .then()
                .statusCode(200);
    }

    @Test
    void login_invalidCredentials__returns401() {
        client.loginInvalidCredentials()
                .then()
                .statusCode(401);
    }

    // -- Helpers ------

    private void registerBeforeTest() {
        body = UserDataFactory.randomAuthUser();
        client.register(body)
                .then()
                .statusCode(200);
    }

}