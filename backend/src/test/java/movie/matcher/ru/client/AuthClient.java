package movie.matcher.ru.client;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import movie.matcher.ru.models.request.AuthModel;

import static io.restassured.RestAssured.given;

@RequiredArgsConstructor
public class AuthClient {

    private final RequestSpecification requestSpec;

    public Response register(AuthModel body) {
        return given().spec(requestSpec)
                .body(body)
                .when().post("/api/auth/register")
                .then()
                .extract().response();
    }

    public Response registerWithEmptyBody() {
        return given().spec(requestSpec)
                .body("{}")
                .when().post("/api/auth/register")
                .then()
                .extract().response();
    }

    public Response login(AuthModel body) {
        return given().spec(requestSpec)
                .body(body)
                .when().post("/api/auth/login")
                .then()
                .extract().response();
    }

    public Response loginInvalidCredentials() {
        return given().spec(requestSpec)
                .body("""
                        {
                            "username": "invalidUser",
                            "password": "invalidPass"
                        }
                        """)
                .when().post("/api/auth/login")
                .then()
                .extract().response();
    }
}