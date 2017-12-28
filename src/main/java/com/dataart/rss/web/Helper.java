package main.java.com.dataart.rss.web;

import main.java.com.dataart.rss.data.FeedChannel;
import main.java.com.dataart.rss.data.FeedItem;
import main.java.com.dataart.rss.data.User;
import main.java.com.dataart.rss.db.ChannelDAO;
import main.java.com.dataart.rss.db.FeedItemDAO;
import main.java.com.dataart.rss.db.UserDAO;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by newbie on 22.12.17.
 */
class Helper {
    static User getUser(UserDAO userDAO, HttpServletRequest request) throws SQLException {
        HttpSession currentSession = request.getSession(false);
        String userLogin = (String) currentSession.getAttribute("userLogin");

        return userDAO.findUser(userLogin);
    }

    // returns ID of the first channel in list
    static int setChannelsToJsp(int userId, ChannelDAO channelDAO, HttpServletRequest request) throws SQLException {
        List<FeedChannel> userChannels = channelDAO.getUserChannels(userId);
        request.setAttribute("listChannel", userChannels);

        return userChannels.isEmpty() ? 0 : userChannels.get(0).getId();
    }

    static void setItemsToJsp(int userId, int channelId, int pageNumber, boolean isDesc, FeedItemDAO feedDAO,
                              HttpServletRequest request) throws SQLException {
        List<FeedItem> feeds = feedDAO.getChannelFeedsForUser(userId, channelId, pageNumber, isDesc);
        request.setAttribute("listFeed", feeds);
    }

}
