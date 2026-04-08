package movie.matcher.ru.api;

import movie.matcher.ru.base.BaseApiTest;
import movie.matcher.ru.fixture.AuthFixture;
import movie.matcher.ru.helpers.DatabaseCleaner;
import movie.matcher.ru.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AuthApiTest extends BaseApiTest {

    @Autowired
    private AuthFixture authFixture;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DatabaseCleaner dbCleaner;

    String random = UUID.randomUUID().toString().substring(0, 4);
    String username = "testUser" + random;
    String password = "testPass" + random;

    @BeforeEach
    void cleanDb() {
        dbCleaner.clean();
    }

    @Test
    void registerUser__success() {
        given(requestSpec)
                .body(authFixture.buildAuthRequest(username, password))
                .when().post("/api/auth/register")
                .then()
                .statusCode(200);

        assertTrue(userRepository.existsByUsername(username));
    }

    @Test
    void registerUser_usernameAlreadyExists__returns409() {
        authFixture.createUser(requestSpec, username, password);

        given(requestSpec)
                .body(authFixture.buildAuthRequest(username, password))
                .when().post("/api/auth/register")
                .then()
                .statusCode(409)
                .body("errorCode", equalTo("USERNAME_ALREADY_EXISTS"));
    }

    @Test
    void registerUser_usernameIsBlank__returns400() {
        given(requestSpec)
                .body(authFixture.buildAuthRequest("", "testPass123"))
                .when().post("/api/auth/register")
                .then()
                .statusCode(400)
                .body("errorCode", equalTo("VALIDATION_ERROR"));
    }

    @Test
    void loginUser__success() {
        authFixture.createUser(requestSpec, username, password);

        given(requestSpec)
                .body(authFixture.buildAuthRequest(username, password))
                .when().post("/api/auth/login")
                .then()
                .statusCode(200);
    }

    @Test
    void loginUser_invalidCredentials__returns401() {
        given(requestSpec)
                .body(authFixture.buildAuthRequest(username, password))
                .when().post("/api/auth/login")
                .then()
                .statusCode(401)
                .body("errorCode", equalTo("INVALID_CREDENTIALS"));
    }

}