package movie.matcher.ru.ui.page;

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

        $("#create-username").setValue(username);
        $("#create-password").setValue(password);

        $("#confirm-create-btn").click();
        return this;
    }

    public MainPage editUser(String oldUsername, String newUsername) {
        $$("tr").findBy(Condition.text(oldUsername)).$(".btn-edit").click();

        $("#edit-username").setValue(newUsername);

        $("#confirm-edit-btn").click();
        return this;
    }

    public MainPage deleteUser(String username) {
        $$("tr").findBy(Condition.text(username)).$(".btn-delete").click();

        $("#confirm-delete-btn").click();
        return this;
    }

    public MainPage successMsgIsVisible(String msgText) {
        $(".toast")
                .shouldBe(Condition.visible)
                .shouldHave(Condition.text(msgText));
        return this;
    }

    public MainPage shouldHaveUserInTable(String username) {
        $$("#users-table tbody tr")
                .findBy(Condition.text(username))
                .shouldBe(Condition.visible);
        return this;
    }

    public MainPage shouldNotHaveUserInTable(String username) {
        $$("#users-table tbody tr")
                .findBy(Condition.text(username))
                .shouldNotBe(Condition.visible);
        return this;
    }
}