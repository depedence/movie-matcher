package movie.matcher.ru.base;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.response.Response;
import movie.matcher.ru.client.AuthClient;
import movie.matcher.ru.data.UserDataFactory;
import movie.matcher.ru.models.request.AuthModel;
import org.junit.jupiter.api.BeforeEach;

public abstract class AuthBaseTest extends BaseApiTest {

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