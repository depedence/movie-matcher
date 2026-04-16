package movie.matcher.ru.ui;

import movie.matcher.ru.base.BaseUiTest;
import movie.matcher.ru.page.LoginPage;
import org.junit.jupiter.api.Test;

public class LoginUiTest extends BaseUiTest {

    @Test
    void userCanLogin() {
        String username = "test";
        String password = "testPass";

        new LoginPage()
                .open()
                .fillUsername(username)
                .fillPassword(password)
                .clickLoginBtn()
                .pageIsLoaded();
    }

    // TODO: add all suites (negative + positive)
    // after this rework MainPage
    // and create full suite MainUiTest

}
