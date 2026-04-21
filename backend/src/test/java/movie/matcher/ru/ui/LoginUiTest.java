package movie.matcher.ru.ui;

import movie.matcher.ru.base.BaseUiTest;
import movie.matcher.ru.data.UserDataFactory;
import movie.matcher.ru.models.request.AuthModel;
import movie.matcher.ru.models.request.CreateUserModel;
import movie.matcher.ru.page.LoginPage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class LoginUiTest extends BaseUiTest {

    private CreateUserModel user;
    private AuthModel authUser;

    @BeforeEach
    void setupTest() {
        user = UserDataFactory.randomUser();
        authUser = authHelper.registerAndLogin();
    }

    @Test
    void login_success() {
        new LoginPage()
                .open()
                .fillUsername(authUser.getUsername())
                .fillPassword(authUser.getPassword())
                .clickLoginBtn()
                .pageIsLoaded();
    }

    @Test
    void login_invalidCredentials() {
        new LoginPage()
                .open()
                .fillUsername(user.getUsername())
                .fillPassword(user.getPassword())
                .clickLoginBtnExpectError()
                .errorBannerIsVisible("Invalid credentials");
    }
}