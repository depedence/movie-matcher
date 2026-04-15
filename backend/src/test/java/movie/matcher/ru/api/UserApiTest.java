package movie.matcher.ru.api;

import io.restassured.response.Response;
import jakarta.transaction.Transactional;
import movie.matcher.ru.base.AuthBaseTest;
import movie.matcher.ru.client.UserClient;
import movie.matcher.ru.data.UserDataFactory;
import movie.matcher.ru.models.request.UserModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@Transactional
public class UserApiTest extends AuthBaseTest {

    private UserClient client;
    private UserModel body;

    @BeforeEach
    void setUp() {
        client = new UserClient(authSpec);
        body = UserDataFactory.randomUser();
    }

    // -- Create User -------

    @Test
    void createUser_success__returns200() {
        client.createUserRequest(body)
                .then()
                .log().all()
                .statusCode(200);
    }

    @Test
    void createUser_usernameAlreadyExists__returns409() {
        client.createUserRequest(body)
                .then()
                .statusCode(200);
        client.createUserRequest(body)
                .then()
                .body("errorCode", equalTo("USERNAME_ALREADY_EXISTS"))
                .statusCode(409);
    }

    // -- Get User ----------

    @Test
    void getUser_success__returns200() {
        Long id = client.createUserRequest(body).jsonPath().getLong("id");
        client.getUserRequest(id)
                .then()
                .statusCode(200);
    }

    @Test
    void getUser_userNotFound__returns404() {
        client.getUserRequest(32L)
                .then()
                .body("errorCode", equalTo("USER_NOT_FOUND"))
                .statusCode(404);
    }

    @Test
    void getUsers_success__returns200() {
        client.createUserRequest(body)
                .then()
                .statusCode(200);
        client.getAllUsersRequest()
                .then()
                .body(notNullValue())
                .statusCode(200);
    }

    // -- Edit User ---------

    @Test
    void editUser_success__returns200() {
        UserModel newBody = UserDataFactory.randomUser();
        Long id = client.createUserRequest(body).jsonPath().getLong("id");
        client.editUserRequest(id, newBody)
                .then()
                .statusCode(200);
    }

    // TODO: add full editUser suite and add deleteUser suite(negative+positive)

}
