package movie.matcher.ru.ui.tests;

import movie.matcher.ru.infra.ui.BaseUiTest;
import movie.matcher.ru.models.request.AuthModel;
import movie.matcher.ru.models.request.CreateUserModel;
import movie.matcher.ru.repository.UserRepository;
import movie.matcher.ru.support.data.UserDataFactory;
import movie.matcher.ru.support.setup.UserFixture;
import movie.matcher.ru.ui.page.MainPage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

public class UserUiTest extends BaseUiTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private CreateUserModel user;
    private UserFixture userFixture;

    @BeforeEach
    void setup() {
        user = UserDataFactory.randomUser();
        userFixture = new UserFixture(userRepository, passwordEncoder);
    }

    @Test
    void userCanCreateNewTestUser() {
        new MainPage()
                .open()
                .pageIsLoaded()
                .createUser(user.getUsername(), user.getPassword())
                .successMsgIsVisible("User created")
                .shouldHaveUserInTable(user.getUsername());
    }

    @Test
    void userCanEditTestUser() {
        AuthModel testUser = userFixture.createTestUser();

        new MainPage()
                .open()
                .pageIsLoaded()
                .editUser(testUser.getUsername(), user.getUsername())
                .successMsgIsVisible("User updated")
                .shouldHaveUserInTable(user.getUsername());
    }

    @Test
    void userCanDeleteTestUser() {
        AuthModel testUser = userFixture.createTestUser();

        new MainPage()
                .open()
                .pageIsLoaded()
                .deleteUser(testUser.getUsername())
                .successMsgIsVisible("User deleted")
                .shouldNotHaveUserInTable(testUser.getUsername());
    }
}