package movie.matcher.ru.fixture;

import lombok.AllArgsConstructor;
import movie.matcher.ru.client.UserClient;
import movie.matcher.ru.data.UserDataFactory;
import movie.matcher.ru.helper.UiAuthHelper;
import movie.matcher.ru.models.request.AuthModel;
import movie.matcher.ru.models.request.CreateUserModel;

@AllArgsConstructor
public class UserFixture {

    private UiAuthHelper uiAuthHelper;
    private UserClient userClient;

    public AuthModel createTestUser() {
        AuthModel user = UserDataFactory.randomAuthUser();

        userClient.createUserRequest(
                new CreateUserModel(user.getUsername(), user.getPassword())
        );

        return user;
    }

    public String getToken() {
        return uiAuthHelper.registerAndGetToken();
    }
}