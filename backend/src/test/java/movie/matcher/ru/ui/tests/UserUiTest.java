package movie.matcher.ru.ui.tests;

import com.codeborne.selenide.Selenide;
import movie.matcher.ru.infra.ui.BaseUiTest;
import movie.matcher.ru.api.client.UserClient;
import movie.matcher.ru.support.data.UserDataFactory;
import movie.matcher.ru.support.setup.UserFixture;
import movie.matcher.ru.models.request.AuthModel;
import movie.matcher.ru.models.request.CreateUserModel;
import movie.matcher.ru.ui.page.MainPage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class UserUiTest extends BaseUiTest {

    private CreateUserModel user;
    private UserFixture userFixture;

    @BeforeEach
    void setup() {
        user = UserDataFactory.randomUser();
        userFixture = new UserFixture(new UserClient(requestSpec));
    }

    @Test
    void userCanCreateNewTestUser() {
        new MainPage()
                .open()
                .pageIsLoaded()
                .createUser(user.getUsername(), user.getPassword())
                .successMsgIsVisible()
                .shouldHaveUserInTable(user.getUsername());
    }

    @Test
    void userCanEditTestUser() {
        String token = uiAuthHelper.registerAndGetToken();
        AuthModel user = userFixture.createTestUser();
        Selenide.open("/login");
        Selenide.localStorage().setItem("token", token);

        new MainPage()
                .open()
                // BUG: он создал, но не нашел
                .shouldHaveUserInTable(user.getUsername());

        Selenide.sleep(10000);
    }
}