package movie.matcher.ru.fixture;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;

@Component
public class UserFixture {

    public Map<String, Object> buildUserBody(Object username) {
        Map<String, Object> body = new HashMap<>();
        body.put("username", username);
        return body;
    }

    public ExtractableResponse<Response> createUser(RequestSpecification requestSpec, String username) {
        return given(requestSpec)
                .body(buildUserBody(username))
                .when()
                .post("/api/users")
                .then()
                .statusCode(200)
                .extract();
    }

}