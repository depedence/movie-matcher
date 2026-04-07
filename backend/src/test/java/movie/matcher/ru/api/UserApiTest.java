package movie.matcher.ru.api;

import movie.matcher.ru.base.BaseApiTest;
import movie.matcher.ru.fixture.UserFixture;
import movie.matcher.ru.helpers.DatabaseCleaner;
import movie.matcher.ru.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;

public class UserApiTest extends BaseApiTest {

    // TODO: написать новые тесты под новую авторизацию

    @Autowired
    private UserFixture userFixture;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DatabaseCleaner databaseCleaner;

    @BeforeEach
    void cleanDb() {
        databaseCleaner.clean();
    }

    @Test
    void createUser__success() {
        given(requestSpec)
                .body(userFixture.buildUserBody("bank"))
                .when().post("/api/users")
                .then().spec(responseSpec)
                .statusCode(200)
                .body("username", equalTo("bank"))
                .extract().response();

        assertTrue(userRepository.existsByUsername("bank"));
    }

    @Test
    void createUser_usernameNull__returns400() {
        given(requestSpec)
                .body(userFixture.buildUserBody(null))
                .when().post("/api/users")
                .then().spec(responseSpec)
                .statusCode(400)
                .body("errorCode", equalTo("VALIDATION_ERROR"));
    }

    @Test
    void createUser_usernameBlank__returns400() {
        given(requestSpec)
                .body(userFixture.buildUserBody(""))
                .when().post("/api/users")
                .then().spec(responseSpec)
                .statusCode(400)
                .body("errorCode", equalTo("VALIDATION_ERROR"));
    }

    @Test
    void createUser_usernameAlreadyExists__returns409() {
        userFixture.createUser(requestSpec, "bank");

        given(requestSpec)
                .body(userFixture.buildUserBody("bank"))
                .when().post("/api/users")
                .then().spec(responseSpec)
                .statusCode(409)
                .body("errorCode", equalTo("USERNAME_ALREADY_EXISTS"));
    }

    @Test
    void getUser__success() {
        Long id = userFixture.createUser(requestSpec, "bank").jsonPath().getLong("id");

        given(requestSpec)
                .pathParam("id", id)
                .when().get("/api/users/{id}")
                .then().spec(responseSpec)
                .statusCode(200)
                .body("id", equalTo(id))
                .body("username", equalTo("bank"));

        assertTrue(userRepository.existsByUsername("bank"));
    }

    @Test
    void getUser_userNotFound__returns404() {
        given(requestSpec)
                .pathParam("id", 1)
                .when().get("/api/users/{id}")
                .then().spec(responseSpec)
                .statusCode(404)
                .body("errorCode", equalTo("USER_NOT_FOUND"));
    }

    @Test
    void editUser__success() {
        Long id = userFixture.createUser(requestSpec, "bank").jsonPath().getLong("id");

        given(requestSpec)
                .pathParam("id", id)
                .body(userFixture.buildUserBody("bank_edit"))
                .when().put("/api/users/{id}")
                .then().spec(responseSpec)
                .statusCode(200)
                .body("id", equalTo(id))
                .body("username", equalTo("bank_edit"));

        assertTrue(userRepository.existsByUsername("bank_edit"));
        assertFalse(userRepository.existsByUsername("bank"));
    }

    @Test
    void editUser_userNotFound__returns404() {
        given(requestSpec)
                .pathParam("id", 1)
                .body(userFixture.buildUserBody("bank_edit"))
                .when().put("/api/users/{id}")
                .then().spec(responseSpec)
                .statusCode(404)
                .body("errorCode", equalTo("USER_NOT_FOUND"));
    }

    @Test
    void editUser_usernameAlreadyExists__returns409() {
        userFixture.createUser(requestSpec, "bank");
        Long id = userFixture.createUser(requestSpec, "bank2").jsonPath().getLong("id");

        given(requestSpec)
                .pathParam("id", id)
                .body(userFixture.buildUserBody("bank"))
                .when().put("/api/users/{id}")
                .then().spec(responseSpec)
                .statusCode(409)
                .body("errorCode", equalTo("USERNAME_ALREADY_EXISTS"));
    }

    @Test
    void deleteUser__success() {
        Long id = userFixture.createUser(requestSpec, "bank").jsonPath().getLong("id");

        given(requestSpec)
                .pathParam("id", id)
                .when().delete("/api/users/{id}")
                .then().statusCode(200);

        assertFalse(userRepository.existsById(id));
    }

}
