package movie.matcher.ru.api;

import io.restassured.response.Response;
import movie.matcher.ru.base.BaseApiTest;
import movie.matcher.ru.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserApiTest extends BaseApiTest {

    @Autowired
    private UserRepository userRepository;

    String username = "testRA";

    @Test
    void createUser__Success() {
        Response response = given().spec(requestSpec)
                .body("""
                        {
                            "username": "%s"
                        }
                        """.formatted(username))
                .when().post("/api/users")
                .then().statusCode(200)
                .body("id", notNullValue())
                .body("username", equalTo(username))
                .extract().response();

        Long userId = response.jsonPath().getLong("id");
        assertTrue(userRepository.existsById(userId), "User should exist in database");
    }

}