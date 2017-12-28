package main.java.com.dataart.rss.web;

import main.java.com.dataart.rss.data.FeedItem;
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
import java.util.List;

/**
 * Created by newbie on 26.12.17.
 */
@WebServlet("/show-feeds")
public class ShowFeedsServlet extends HttpServlet {
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
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int channelId = Integer.parseInt(request.getParameter("displayChannelId"));
        try {
            User currentUser = Helper.getUser(userDAO, request);

            // getting current user channels list and setting it to *.jsp
            Helper.setChannelsToJsp(currentUser.getId(), channelDAO, request);

            // getting current RSS-channel feeds
            List<FeedItem> feeds = feedDAO.getChannelFeedsForUser(currentUser.getId(), channelId, 1, false);
            request.setAttribute("listFeed", feeds);

            request.getRequestDispatcher("/channel.jsp").forward(request, response);
        } catch (SQLException exc) {
            throw new ServletException(exc);
        }

    }
}
