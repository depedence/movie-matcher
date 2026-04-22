package movie.matcher.ru.ui.page;

import com.codeborne.selenide.Condition;

public class LoginPage extends BasePage {

    public LoginPage open() {
        open("/login");
        return this;
    }

    public LoginPage fillUsername(String username) {
        $("#login-input").setValue(username);
        return this;
    }

    public LoginPage fillPassword(String password) {
        $("#password-input").setValue(password);
        return this;
    }

    public MainPage clickLoginBtn() {
        $("#login-btn").click();
        return new MainPage();
    }

    public LoginPage clickLoginBtnExpectError() {
        $("#login-btn").click();
        return this;
    }

    public LoginPage errorBannerIsVisible(String expectedMessage) {
        $("#error-banner")
                .shouldBe(Condition.visible)
                .should(Condition.text(expectedMessage));
        return this;
    }

}