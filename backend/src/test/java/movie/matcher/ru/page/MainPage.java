package movie.matcher.ru.page;

import com.codeborne.selenide.Condition;

public class MainPage extends BasePage {

    public MainPage pageIsLoaded() {
        $("#logout-btn").shouldBe(Condition.visible);
        return this;
    }

}