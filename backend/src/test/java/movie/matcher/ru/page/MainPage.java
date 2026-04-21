package movie.matcher.ru.page;

import com.codeborne.selenide.Condition;

public class MainPage extends BasePage {

    public MainPage open() {
        open("/main");
        return this;
    }

    public MainPage pageIsLoaded() {
        $("#logout-btn").shouldBe(Condition.visible);
        return this;
    }

    public MainPage createUser(String username, String password) {
        $("#create-user-btn").click();

        $("#create-username").shouldBe(Condition.visible);
        $("#create-username").setValue(username);
        $("#create-password").setValue(password);

        $("#confirm-create-btn").click();
        return this;
    }

    public MainPage successMsgIsVisible() {
        $(".toast")
                .shouldBe(Condition.visible)
                .shouldHave(Condition.text("User created"));
        return this;
    }

    public MainPage shouldHaveUserInTable(String username) {
        $$("#users-table tbody tr")
                .findBy(Condition.text(username))
                .shouldBe(Condition.visible);
        return this;
    }
}