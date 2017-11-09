package main.java.com.dataart.rss.web;

import main.java.com.dataart.rss.data.User;
import main.java.com.dataart.rss.db.UserDAO;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;

/**
 * Created by newbie on 09.11.17.
 */
@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {
    private UserDAO userDAO;

    @Override
    public void init() {
        userDAO = UserDAO.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();

        String userLogin = (String)session.getAttribute("name");
        session.invalidate();

        User user;

        try {
            user = userDAO.findUser(userLogin);
        } catch (SQLException exc) {
            throw new ServletException(exc);
        }

        String message = "User @" + user.getLogin() + " logged out successfully. Goodbye, " + user.getName();
        request.setAttribute("errorMessage", message);
        request.setAttribute("messageColor", "green");

        request.getRequestDispatcher("/login.jsp").forward(request, response);
    }
}
