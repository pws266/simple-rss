package main.java.com.dataart.rss.web;

import main.java.com.dataart.rss.data.User;
import main.java.com.dataart.rss.db.ChannelDAO;
import main.java.com.dataart.rss.db.FeedItemDAO;
import main.java.com.dataart.rss.db.UserDAO;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

/**
 * Created by newbie on 12.12.17.
 */
@WebServlet("/show")
public class ShowServlet extends HttpServlet {
    private UserDAO userDAO;
    private ChannelDAO channelDAO;
    private FeedItemDAO feedDAO;

    @Override
    public void init() {
        userDAO = UserDAO.getInstance();
        channelDAO = ChannelDAO.getInstance();
        feedDAO = FeedItemDAO.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            // TODO: make auxiliary class with error processing and user login verification
/*
            HttpSession currentSession = request.getSession(false);
            String userLogin = (String) currentSession.getAttribute("userLogin");

            User currentUser;

            try {
                currentUser = userDAO.findUser(userLogin);
            } catch (SQLException exc) {
                throw new ServletException(exc);
            }
*/
            boolean isDescSorting = "desc".equals(request.getSession().getAttribute("sortingState"));

            User currentUser = Helper.getUser(userDAO, request);
            int initialChannelId = Helper.setChannelsToJsp(currentUser.getId(), channelDAO, request);

            Helper.setItemsToJsp(currentUser.getId(), initialChannelId, 1, 1, isDescSorting /*false*/, feedDAO, request);

//            List<FeedChannel> userChannels = channelDAO.getUserChannels(currentUser.getId());
//
//            request.setAttribute("listChannel", userChannels);
            request.getRequestDispatcher("/channel.jsp").forward(request, response);
        } catch (SQLException exc) {
            throw new ServletException(exc);
        }
    }
}
