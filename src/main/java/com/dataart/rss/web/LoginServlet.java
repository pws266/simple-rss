package main.java.com.dataart.rss.web;

import main.java.com.dataart.rss.data.FeedChannel;
import main.java.com.dataart.rss.data.User;
import main.java.com.dataart.rss.db.UserDAO;
import main.java.com.dataart.rss.webform.LoginFormData;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by newbie on 01.11.17.
 */
@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    // private FeedDAO feedDAO;
    private UserDAO userDAO;

    @Override
    public void init() {
//        try {
            // feedDAO = FeedDAOFactory.getInstance();
            userDAO = UserDAO.getInstance();
/*        } catch (IOException exc) {
            // to logger
        }*/
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        LoginFormData formData = new LoginFormData();

        // reading login form data
        formData.login = request.getParameter("login");
        formData.password = request.getParameter("password");

        // checking read content
        String errorMessage = formData.checkForm();

        if (!errorMessage.isEmpty()) {
            sendErrorMessage(errorMessage, formData, request, response);
            return;
        }

        // looking for existing user with the specified login
        User user = null;

        try {
            user = userDAO.findUser(formData.login);
        } catch (SQLException exc) {
            // logger
        }

        if (user == null) {
            errorMessage = "User @" + formData.login + " isn't registered. Please choose another user or sign-up";
            sendErrorMessage(errorMessage, formData, request, response);
            return;
        }

        // verifying user password
        boolean isCorrect = false;

        try {
            isCorrect = user.isCorrectPassword(formData.password);
        } catch (NoSuchAlgorithmException exc) {
            // to logger
        }

        if (isCorrect) {
            HttpSession currentSession = request.getSession();
            currentSession.setAttribute("userName", user.getName());
            currentSession.setAttribute("userLogin", user.getLogin());

            request.setAttribute("userName", user.getName());
            request.setAttribute("userLogin", user.getLogin());

//            request.getRequestDispatcher("/channel.jsp").forward(request, response);
            response.sendRedirect("show");
        } else {
            errorMessage = "Incorrect password for user @" + formData.login + ". Please check your credentials";
            sendErrorMessage(errorMessage, formData, request, response);
        }
/*
        try {
            if (!user.isCorrectPassword(formData.password)) {
                errorMessage = "Incorrect password for user @" + formData.login + ". Please check your credentials";
                sendErrorMessage(errorMessage, formData, request, response);
                return;
            }
        } catch (NoSuchAlgorithmException exc) {

        }
*/
    }

    private void sendErrorMessage(String message, LoginFormData formData, HttpServletRequest request,
                                  HttpServletResponse response) throws ServletException, IOException {
        request.setAttribute("login", formData.login);
        request.setAttribute("password", formData.password);

        request.setAttribute("errorMessage", message);
        request.setAttribute("messageColor", "indianred");

        request.getRequestDispatcher("/login.jsp").forward(request, response);
    }
}
