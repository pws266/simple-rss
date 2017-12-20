package main.java.com.dataart.rss.webform;
import static main.java.com.dataart.rss.data.Reference.EMPTY_CREDENTIAL_MSG;

/**
 * Created by newbie on 11.11.17.
 */
public class LoginFormData implements IFormData {
    public String login;
    public String password;

    private String errorOnEmptyLogin() {
        if (login.isEmpty()) {
            return "Login" + EMPTY_CREDENTIAL_MSG;
        }

        return "";
    }

    private String errorOnEmptyPassword() {
        if (password.isEmpty()) {
            return "Password" + EMPTY_CREDENTIAL_MSG;
        }

        return "";
    }

    @Override
    public String checkForm() {
        String errorMessage = errorOnEmptyLogin();

        if (!errorMessage.isEmpty())
            return errorMessage;

        return errorOnEmptyPassword();
    }
}
