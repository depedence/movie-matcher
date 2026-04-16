package movie.matcher.ru.data;

import movie.matcher.ru.models.request.AuthModel;
import movie.matcher.ru.models.request.CreateUserModel;
import movie.matcher.ru.models.request.EditUserModel;
import net.datafaker.Faker;

public class UserDataFactory {

    private static final Faker faker = new Faker();

    public static AuthModel randomAuthUser() {
        return AuthModel.builder()
                .username(faker.name().username())
                .password(faker.internet().password())
                .build();
    }

    public static CreateUserModel randomUser() {
        return CreateUserModel.builder()
                .username(faker.name().username())
                .password(faker.internet().password())
                .build();
    }

    public static EditUserModel randomUsername() {
        return EditUserModel.builder()
                .username(faker.name().username())
                .build();
    }
}