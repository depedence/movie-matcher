package movie.matcher.ru.support.data;

import movie.matcher.ru.models.request.AuthModel;
import movie.matcher.ru.models.request.CreateUserModel;
import movie.matcher.ru.models.request.EditUserModel;
import net.datafaker.Faker;

public class UserDataFactory {

    private static final Faker faker = new Faker();

    public static AuthModel randomAuthUser() {
        return AuthModel.builder()
                .username(generateUsername())
                .password(generatePassword())
                .build();
    }

    public static CreateUserModel randomUser() {
        return CreateUserModel.builder()
                .username(generateUsername())
                .password(generatePassword())
                .build();
    }

    public static EditUserModel randomUsername() {
        return EditUserModel.builder()
                .username(generateUsername())
                .build();
    }

    private static String generateUsername() {
        return faker.name().username();
    }

    private static String generatePassword() {
        return faker.internet().password();
    }
}