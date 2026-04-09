package movie.matcher.ru.base;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class BaseUiTest {

    @LocalServerPort
    protected int port;
    protected String baseUrl;

    @BeforeAll
    void setupAll() {
        baseUrl = "http://localhost:" + port;

        Configuration.baseUrl = baseUrl;
        Configuration.headless = false;
        Configuration.browser = "chrome";
        Configuration.timeout = 10_000;
    }

    @AfterEach
    void tearDown() {
        Selenide.closeWebDriver();
    }
}