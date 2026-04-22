package movie.matcher.ru.infra.ui;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import movie.matcher.ru.support.auth.UiAuthHelper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("tests")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BaseUiTestNoAuth {

    @LocalServerPort
    protected int port;

    protected String baseUrl;
    protected RequestSpecification requestSpec;
    protected UiAuthHelper uiAuthHelper;

    @BeforeAll
    void setupAll() {
        baseUrl = "http://localhost:" + port;

        Configuration.browser = "chrome";
        Configuration.headless = false;
        Configuration.timeout = 10_000;
        Configuration.baseUrl = baseUrl;

        requestSpec = new RequestSpecBuilder()
                .setBaseUri(baseUrl)
                .setContentType(ContentType.JSON)
                .build();

        uiAuthHelper = new UiAuthHelper(requestSpec);
    }

    @AfterEach
    void tearDown() {
        Selenide.closeWebDriver();
    }
}