package movie.matcher.ru.fixture;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;

@Component
public class AuthFixture {

    public Map<String, String> buildAuthRequest(String username, String password) {
        Map<String, String> body = new HashMap<>();
        body.put("username", username);
        body.put("password", password);
        return body;
    }

    public ExtractableResponse<Response> createUser(RequestSpecification requestSpec, String username, String password) {
        return given(requestSpec)
                .body(buildAuthRequest(username, password))
                .when().post("/api/auth/register")
                .then()
                .statusCode(200)
                .extract();
    }

    public String login(RequestSpecification requestSpec, String username, String password) {
        createUser(requestSpec, username, password);

        return given(requestSpec)
                .body(buildAuthRequest(username, password))
                .when().post("/api/auth/login")
                .then()
                .statusCode(200)
                .extract().path("token");
    }

}