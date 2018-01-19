package main.java.com.dataart.rss.web;

import main.java.com.dataart.rss.data.FeedChannel;
import main.java.com.dataart.rss.data.FeedItem;
import main.java.com.dataart.rss.data.User;
import main.java.com.dataart.rss.db.ChannelDAO;
import main.java.com.dataart.rss.db.FeedItemDAO;
import main.java.com.dataart.rss.db.UserDAO;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.sql.SQLException;
import java.util.List;

import static main.java.com.dataart.rss.data.Reference.DEFAULT_SORT_STATE;
import static main.java.com.dataart.rss.data.Reference.FEEDS_PER_PAGE;

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

    static void setItemsToJsp(int userId, int channelId, int channelRow, int pageNumber, boolean isDesc,
                              FeedItemDAO feedDAO, HttpServletRequest request) throws SQLException {
/*
        List<FeedItem> feeds = feedDAO.getChannelFeedsForUser(userId, channelId, pageNumber, isDesc);
        request.setAttribute("listFeed", feeds);
*/
        // getting current RSS-channel feeds
        List<FeedItem> feeds = feedDAO.getChannelFeedsForUser(userId, channelId, pageNumber, isDesc);
        request.setAttribute("listFeed", feeds);

        int feedsNumber = feedDAO.getFeedsNumberForUser(userId, channelId);
        request.setAttribute("feedsNumber", feedsNumber);

        request.setAttribute("feedsPerPage", FEEDS_PER_PAGE);
        //request.setAttribute("currentPageNumber", pageNumber);

        request.setAttribute("pageNumber", pageNumber);

        request.setAttribute("currentChannelRow", channelRow);

        String sortingState = isDesc ? "desc" : "asc";

        request.setAttribute("sorting", sortingState);
        request.setAttribute("sortRBForAdd", sortingState);
        request.setAttribute("sortRBForDelete", sortingState);
    }

    // saves "sorting" radiogroup state in session variable
    static void setSortingToSession(String controlName, HttpServletRequest request) {
        String sortingState = request.getParameter(controlName);
        if (sortingState == null || sortingState.isEmpty()) {
            sortingState = DEFAULT_SORT_STATE;
        }

        request.getSession().setAttribute("sortingState", sortingState);
    }
}
