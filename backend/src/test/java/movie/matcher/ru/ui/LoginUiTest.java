package movie.matcher.ru.ui;

import movie.matcher.ru.base.BaseUiTest;
import movie.matcher.ru.page.LoginPage;
import org.junit.jupiter.api.Test;

public class LoginUiTest extends BaseUiTest {

    String username = "test";
    String password = "testPass";

    @Test
    void userCanLogin() {
        new LoginPage()
                .open()
                .fillUsername(username)
                .fillPassword(password)
                .clickLoginBtn();
    }

}