package movie.matcher.ru.client;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import movie.matcher.ru.fixture.UserFixture;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

import static io.restassured.RestAssured.given;

public class UserApiClient {

    String random = UUID.randomUUID().toString().substring(0, 4);
    String username = "testUser" + random;
    String password = "testPass" + random;

    private RequestSpecification requestSpec;

    @Autowired
    private UserFixture userFixture;

    public Response createUserRequest(String username, String password) {
        return given(requestSpec)
                .body(userFixture.buildCreateUserRequest(username, password))
                .when().post("/api/users");
    }

}