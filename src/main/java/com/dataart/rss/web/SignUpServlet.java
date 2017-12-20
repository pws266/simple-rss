/**
 * Created by newbie on 01.11.17.
 */
package main.java.com.dataart.rss.web;

import main.java.com.dataart.rss.data.User;
import main.java.com.dataart.rss.db.UserDAO;
import main.java.com.dataart.rss.webform.SignUpFormData;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;

@WebServlet("/signup")
public class SignUpServlet extends HttpServlet {
    // private FeedDAO feedDAO;
    private UserDAO userDAO;

    @Override
    public void init() {
//        try {
            // feedDAO = FeedDAOFactory.getInstance();
        userDAO = UserDAO.getInstance();
/*        } catch (IOException exc) {
            // to logger
        }
*/
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        SignUpFormData formData = new SignUpFormData();

        // reading sign-up form data
        formData.username = request.getParameter("username");
        formData.loginForm.login = request.getParameter("login");

        formData.loginForm.password = request.getParameter("password");
        formData.confirmation = request.getParameter("confirm");

        // checking read content
        String errorMessage = formData.checkForm();

        if (!errorMessage.isEmpty()) {
            sendErrorMessage(errorMessage, formData, request, response);
            return;
        }

        // looking for existing user with the same login
        User user = null;

        try {
            user = userDAO.findUser(formData.loginForm.login);
        } catch (SQLException exc) {
            // logger
            throw new ServletException(exc);
        }

        if (user != null) {
            errorMessage = "User with login \"" + formData.loginForm.login +
                           "\" is also registered. Please choose another login";
            sendErrorMessage(errorMessage, formData, request, response);
            return;
        }

        // registering new user
        user = new User(formData.username, formData.loginForm.login, formData.loginForm.password);

        // encrypting user password
        try {
            user.encryptPassword();
        } catch (NoSuchAlgorithmException exc) {
            throw new ServletException(exc);
        }

        try {
            userDAO.addUser(user);
        } catch (SQLException exc) {
            throw new ServletException(exc);
        }

        errorMessage = "User \"" + formData.username + "\" @" + formData.loginForm.login + " is successfully " +
                       "registered. Login now!";
        sendMessageToForm(errorMessage, "green", "/login.jsp", request, response);
    }

    private void sendMessageToForm(String message, String color, String jspPageName, HttpServletRequest request,
                                   HttpServletResponse response) throws ServletException, IOException {
        request.setAttribute("errorMessage", message);
        request.setAttribute("messageColor", color);

        request.getRequestDispatcher(jspPageName).forward(request, response);
    }


    private void sendErrorMessage(String message, SignUpFormData formData, HttpServletRequest request,
                                  HttpServletResponse response) throws ServletException, IOException {
        request.setAttribute("username", formData.username);
        request.setAttribute("login", formData.loginForm.login);
        request.setAttribute("password", formData.loginForm.password);
        request.setAttribute("confirm", formData.confirmation);

        sendMessageToForm(message, "indianred", "/sign-up.jsp", request, response);
    }
}
