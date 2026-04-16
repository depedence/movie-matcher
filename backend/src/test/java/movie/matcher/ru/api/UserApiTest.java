package movie.matcher.ru.api;

import io.restassured.response.Response;
import jakarta.transaction.Transactional;
import movie.matcher.ru.base.AuthBaseTest;
import movie.matcher.ru.client.UserClient;
import movie.matcher.ru.data.UserDataFactory;
import movie.matcher.ru.models.request.CreateUserModel;
import movie.matcher.ru.models.request.EditUserModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@Transactional
public class UserApiTest extends AuthBaseTest {

    private UserClient client;
    private CreateUserModel createUserModel;
    private EditUserModel editUserModel;

    @BeforeEach
    void setUp() {
        client = new UserClient(authSpec);
        createUserModel = UserDataFactory.randomUser();
        editUserModel = UserDataFactory.randomUsername();
    }

    // -- Create User -------

    @Test
    void createUser_success__returns200() {
        client.createUserRequest(createUserModel)
                .then()
                .log().all()
                .statusCode(200);
    }

    @Test
    void createUser_usernameAlreadyExists__returns409() {
        client.createUserRequest(createUserModel)
                .then()
                .statusCode(200);
        client.createUserRequest(createUserModel)
                .then()
                .body("errorCode", equalTo("USERNAME_ALREADY_EXISTS"))
                .statusCode(409);
    }

    // -- Get User ----------

    @Test
    void getUser_success__returns200() {
        Long id = createTestUserAndGetId();
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
        client.createUserRequest(createUserModel)
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
        Long id = createTestUserAndGetId();
        client.editUserRequest(id, editUserModel)
                .then()
                .statusCode(200);
    }

    @Test
    void editUser_userNotFound__returns404() {
        client.editUserRequest(32L, editUserModel)
                .then()
                .body("errorCode", equalTo("USER_NOT_FOUND"))
                .statusCode(404);
    }

    @Test
    void editUser_usernameAlreadyExists__returns409() {
        Response response = client.createUserRequest(createUserModel);
        Long id = response.jsonPath().getLong("id");
        EditUserModel body = EditUserModel.builder()
                .username(response.jsonPath().getString("username"))
                .build();
        client.editUserRequest(id, body)
                .then()
                .body("errorCode", equalTo("USERNAME_ALREADY_EXISTS"))
                .statusCode(409);
    }

    // -- Delete User -------

    @Test
    void deleteUser_success__returns200() {
        Long id = createTestUserAndGetId();
        client.deleteUserRequest(id)
                .then()
                .body("message", equalTo("User successfully deleted"))
                .statusCode(200);
    }

    // -- Helpers -----------

    private Long createTestUserAndGetId() {
        return client.createUserRequest(createUserModel)
                .then()
                .statusCode(200)
                .extract().jsonPath().getLong("id");
    }
}
