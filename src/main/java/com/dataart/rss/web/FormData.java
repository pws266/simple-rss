package main.java.com.dataart.rss.web;

import static main.java.com.dataart.rss.data.Reference.EMPTY_CREDENTIAL_MSG;
import static main.java.com.dataart.rss.data.Reference.PASSWORD_MISMATCH_MSG;

/**
 * Created by newbie on 02.11.17.
 */

interface IFormData {
    String checkForm();
}

class LoginFormData implements IFormData{
    String login;
    String password;

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

class SignUpFormData implements IFormData{
    LoginFormData loginForm;

    String username;
    String confirmation;

    SignUpFormData() {
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
