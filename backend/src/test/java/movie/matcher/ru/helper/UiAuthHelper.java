package movie.matcher.ru.helper;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import movie.matcher.ru.client.AuthClient;
import movie.matcher.ru.data.UserDataFactory;
import movie.matcher.ru.models.request.AuthModel;
import movie.matcher.ru.models.request.CreateUserModel;

public class UiAuthHelper {

    private final AuthClient authClient;

    public UiAuthHelper(RequestSpecification requestSpec) {
        this.authClient = new AuthClient(requestSpec);
    }

    public AuthModel registerAndLogin() {
        AuthModel user = UserDataFactory.randomAuthUser();
        authClient.register(user);
        return user;
    }

    public String registerAndGetToken() {
        AuthModel user = UserDataFactory.randomAuthUser();
        Response response = authClient.register(user);
        return response.jsonPath().getString("token");
    }
}