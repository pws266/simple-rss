package main.java.com.dataart.rss.webform;

import static main.java.com.dataart.rss.data.Reference.EMPTY_CREDENTIAL_MSG;
import static main.java.com.dataart.rss.data.Reference.PASSWORD_MISMATCH_MSG;

/**
 * Created by newbie on 11.11.17.
 */
public class SignUpFormData implements IFormData {
    public LoginFormData loginForm;

    public String username;
    public String confirmation;

    public SignUpFormData() {
        loginForm = new LoginFormData();
    }

    private String errorOnEmptyUsername() {
        if (username.isEmpty()) {
            return "Username" + EMPTY_CREDENTIAL_MSG;
        }

        return "";
    }

    private String errorOnEmptyConfirmation() {
        if (confirmation.isEmpty()) {
            return "Confirmation" + EMPTY_CREDENTIAL_MSG;
        }

        return "";
    }

    private String errorOnCheckPassword() {
        if (!loginForm.password.equals(confirmation)) {
            return PASSWORD_MISMATCH_MSG;
        }

        return "";
    }

    @Override
    public String checkForm() {
        String errorMessage = errorOnEmptyUsername();

        if (!errorMessage.isEmpty())
            return errorMessage;

        errorMessage = loginForm.checkForm();

        if (!errorMessage.isEmpty())
            return errorMessage;

        errorMessage = errorOnEmptyConfirmation();

        if (!errorMessage.isEmpty())
            return errorMessage;

        return errorOnCheckPassword();
    }
}
