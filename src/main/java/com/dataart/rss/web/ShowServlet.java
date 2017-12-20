package main.java.com.dataart.rss.web;

import main.java.com.dataart.rss.data.FeedChannel;
import main.java.com.dataart.rss.data.User;
import main.java.com.dataart.rss.db.ChannelDAO;
import main.java.com.dataart.rss.db.UserDAO;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by newbie on 12.12.17.
 */
@WebServlet("/show")
public class ShowServlet extends HttpServlet {
    private UserDAO userDAO;
    private ChannelDAO channelDAO;

    @Override
    public void init() {
        userDAO = UserDAO.getInstance();
        channelDAO = ChannelDAO.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            // TODO: make auxiliary class with error processing and user login verification
            HttpSession currentSession = request.getSession(false);
            String userLogin = (String) currentSession.getAttribute("userLogin");

            User currentUser;

            try {
                currentUser = userDAO.findUser(userLogin);
            } catch (SQLException exc) {
                throw new ServletException(exc);
            }


            List<FeedChannel> userChannels = channelDAO.getUserChannels(currentUser.getId());

            request.setAttribute("listChannel", userChannels);
            request.getRequestDispatcher("/channel.jsp").forward(request, response);
        } catch (SQLException exc) {
            throw new ServletException(exc);
        }
    }
}
