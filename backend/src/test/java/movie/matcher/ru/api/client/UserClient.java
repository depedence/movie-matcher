package movie.matcher.ru.api.client;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import lombok.RequiredArgsConstructor;
import movie.matcher.ru.models.request.CreateUserModel;
import movie.matcher.ru.models.request.EditUserModel;

import static io.restassured.RestAssured.given;

@RequiredArgsConstructor
public class UserClient {

    private final RequestSpecification authSpec;

    public Response getAllUsersRequest() {
        return given().spec(authSpec)
                .when().get("/api/users")
                .then()
                .extract().response();
    }

    public Response getUserRequest(Long id) {
        return given().spec(authSpec)
                .when().get("/api/users/{id}", id)
                .then()
                .extract().response();
    }

    public Response createUserRequest(CreateUserModel body) {
        return given().spec(authSpec)
                .body(body)
                .when().post("/api/users")
                .then()
                .extract().response();
    }

    public Response editUserRequest(Long id, EditUserModel body) {
        return given().spec(authSpec)
                .body(body)
                .when().put("/api/users/{id}", id)
                .then()
                .extract().response();
    }

    public Response deleteUserRequest(Long id) {
        return given().spec(authSpec)
                .when().delete("/api/users/{id}", id)
                .then()
                .extract().response();
    }
}