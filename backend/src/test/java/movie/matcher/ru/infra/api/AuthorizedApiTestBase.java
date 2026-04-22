package movie.matcher.ru.infra.api;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.response.Response;
import movie.matcher.ru.api.client.AuthClient;
import movie.matcher.ru.support.data.UserDataFactory;
import movie.matcher.ru.models.request.AuthModel;
import org.junit.jupiter.api.BeforeEach;

public abstract class AuthorizedApiTestBase extends BaseApiTest {

    protected AuthClient authClient;
    protected AuthModel activeUser;

    @BeforeEach
    void setUpAuth() {
        databaseCleaner.cleanDb();

        authClient = new AuthClient(requestSpec);
        activeUser = UserDataFactory.randomAuthUser();

        authClient.register(activeUser);
        Response response = authClient.login(activeUser);
        String token = response.jsonPath().getString("token");

        authSpec = new RequestSpecBuilder()
                .addRequestSpecification(requestSpec)
                .addHeader("Authorization", "Bearer " + token)
                .build();
    }

}