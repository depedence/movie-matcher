package movie.matcher.ru.page;

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

}