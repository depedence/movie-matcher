package movie.matcher.ru.data;

import movie.matcher.ru.models.request.AuthModel;
import movie.matcher.ru.models.request.UserModel;
import net.datafaker.Faker;

public class UserDataFactory {

    private static final Faker faker = new Faker();

    public static AuthModel randomAuthUser() {
        return AuthModel.builder()
                .username(faker.name().username())
                .password(faker.internet().password())
                .build();
    }

    public static UserModel randomUser() {
        return UserModel.builder()
                .username(faker.name().username())
                .password(faker.internet().password())
                .build();
    }
}