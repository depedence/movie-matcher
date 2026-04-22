package movie.matcher.ru.support.auth;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import movie.matcher.ru.api.client.AuthClient;
import movie.matcher.ru.support.data.UserDataFactory;
import movie.matcher.ru.models.request.AuthModel;

public class UiAuthHelper {

    private final AuthClient authClient;

    public UiAuthHelper(RequestSpecification requestSpec) {
        this.authClient = new AuthClient(requestSpec);
    }

    public AuthModel registerAndGetUser() {
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