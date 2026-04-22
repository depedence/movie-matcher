package movie.matcher.ru.infra.ui;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.WebDriverRunner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("tests")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class BaseUiTest extends BaseUiTestNoAuth {

    @BeforeEach
    void setupSession() {
        Selenide.open("/login");

        WebDriverRunner.getWebDriver().manage().deleteAllCookies();
        Selenide.localStorage().clear();

        String token = uiAuthHelper.registerAndGetToken();
        Selenide.localStorage().setItem("token", token);
        Selenide.refresh();
    }
}