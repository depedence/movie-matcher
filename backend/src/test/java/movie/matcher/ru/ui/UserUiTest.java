package movie.matcher.ru.ui;

import com.codeborne.selenide.Selenide;
import movie.matcher.ru.base.BaseUiTest;
import movie.matcher.ru.client.UserClient;
import movie.matcher.ru.data.UserDataFactory;
import movie.matcher.ru.fixture.UserFixture;
import movie.matcher.ru.models.request.AuthModel;
import movie.matcher.ru.models.request.CreateUserModel;
import movie.matcher.ru.page.MainPage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class UserUiTest extends BaseUiTest {

    private CreateUserModel user;
    private UserFixture userFixture;

    @BeforeEach
    void setup() {
        user = UserDataFactory.randomUser();
        userFixture = new UserFixture(uiAuthHelper, new UserClient(requestSpec));
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
        String token = userFixture.getToken();
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