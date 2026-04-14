package movie.matcher.ru.data;

import movie.matcher.ru.models.request.AuthModel;
import net.datafaker.Faker;

public class UserDataFactory {

    private static final Faker faker = new Faker();

    public static AuthModel randomUser() {
        return AuthModel.builder()
                .username(faker.name().username())
                .password(faker.internet().password())
                .build();
    }
}