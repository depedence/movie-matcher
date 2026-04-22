package movie.matcher.ru.support.setup;

import lombok.AllArgsConstructor;
import movie.matcher.ru.api.client.UserClient;
import movie.matcher.ru.support.data.UserDataFactory;
import movie.matcher.ru.models.request.AuthModel;
import movie.matcher.ru.models.request.CreateUserModel;

@AllArgsConstructor
public class UserFixture {

    private UserClient userClient;

    public AuthModel createTestUser() {
        AuthModel user = UserDataFactory.randomAuthUser();

        userClient.createUserRequest(
                new CreateUserModel(user.getUsername(), user.getPassword())
        );

        return user;
    }
}