package movie.matcher.ru.api;

import movie.matcher.ru.base.BaseApiTest;
import movie.matcher.ru.fixture.UserFixture;
import movie.matcher.ru.helpers.DatabaseCleaner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class UserApiTest extends BaseApiTest {

    @Autowired
    private UserFixture userFixture;

    @Autowired
    private DatabaseCleaner databaseCleaner;

    @BeforeEach
    void cleanDb() {
        databaseCleaner.clean();
    }

    // TODO: add full CRUD methods

    @Test
    void createUser__success() {
        given(requestSpec)
                .body(userFixture.buildUserBody("bank"))
                .when().post("/api/users")
                .then().spec(responseSpec)
                .statusCode(200)
                .body("username", equalTo("bank"));
    }

    @Test
    void editUser__success() {
        long id = userFixture.createUser(requestSpec, "bank").jsonPath().getLong("id");

        given(requestSpec)
                .pathParam("id", id)
                .body(userFixture.buildUserBody("bank_edit"))
                .when().put("/api/users/{id}")
                .then().spec(responseSpec)
                .statusCode(200)
                .body("username", equalTo("bank_edit"));
    }

    @Test
    void deleteUser__success() {
        long id = userFixture.createUser(requestSpec, "bank").jsonPath().getLong("id");

        given(requestSpec)
                .pathParam("id", id)
                .when().delete("/api/users/{id}")
                .then().statusCode(200);
    }

}
