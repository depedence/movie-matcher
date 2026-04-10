package movie.matcher.ru.api;

import movie.matcher.ru.base.BaseApiTest;
import movie.matcher.ru.fixture.AuthFixture;
import movie.matcher.ru.fixture.UserFixture;
import movie.matcher.ru.helpers.DatabaseCleaner;
import movie.matcher.ru.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserApiTest extends BaseApiTest {

    // TODO: вынести given() в отдельный Client .java class
    // header вставить в requestSpec

    
    @Autowired
    private UserFixture userFixture;

    @Autowired
    private AuthFixture authFixture;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DatabaseCleaner dbCleaner;

    String token;

    String random = UUID.randomUUID().toString().substring(0, 4);
    String username = "testUser" + random;
    String password = "testPass" + random;

    @BeforeEach
    void dbClean() {
        dbCleaner.clean();
        token = authFixture.login(requestSpec, "admin", "adminPass");
    }

    @Test
    void createUser__success() {
        given(requestSpec)
                .header("Authorization", "Bearer " + token)
                .body(userFixture.buildCreateUserRequest(username, password))
                .when().post("/api/users")
                .then()
                .statusCode(200);

        assertTrue(userRepository.existsByUsername(username));
    }

    @Test
    void createUser_usernameAlreadyExists__returns409() {
        given(requestSpec)
                .header("Authorization", "Bearer " + token)
                .body(userFixture.buildCreateUserRequest("admin", "adminPass"))
                .when().post("/api/users")
                .then()
                .statusCode(409)
                .body("errorCode", equalTo("USERNAME_ALREADY_EXISTS"));
    }

    @Test
    void editUser__success() {
        Long id = userRepository.findByUsername("admin").orElseThrow().getId();

        given(requestSpec)
                .header("Authorization", "Bearer " + token)
                .pathParam("id", id)
                .body(userFixture.buildEditUserRequest(username))
                .when().put("/api/users/{id}")
                .then()
                .statusCode(200)
                .body("username", equalTo(username));

        assertTrue(userRepository.existsByUsername(username));
    }

    @Test
    void editUser_userNotFound__returns404() {
        given(requestSpec)
                .header("Authorization", "Bearer " + token)
                .pathParam("id", 10)
                .body(userFixture.buildEditUserRequest(username))
                .when().put("/api/users/{id}")
                .then()
                .statusCode(404)
                .body("errorCode", equalTo("USER_NOT_FOUND"));
    }

    @Test
    void editUser_usernameAlreadyExists__returns409() {
        Long id = userRepository.findByUsername("admin").orElseThrow().getId();

        given(requestSpec)
                .header("Authorization", "Bearer " + token)
                .pathParam("id", id)
                .body(userFixture.buildEditUserRequest("admin"))
                .when().put("/api/users/{id}")
                .then()
                .statusCode(409)
                .body("errorCode", equalTo("USERNAME_ALREADY_EXISTS"));
    }

    @Test
    void deleteUser__success() {
        Long id = userRepository.findByUsername("admin").orElseThrow().getId();

        given(requestSpec)
                .header("Authorization", "Bearer " + token)
                .pathParam("id", id)
                .body(userFixture.buildEditUserRequest(username))
                .when().delete("/api/users/{id}")
                .then()
                .statusCode(200)
                .body("message", equalTo("User successfully deleted"));

        assertFalse(userRepository.existsByUsername(username));
    }

}
