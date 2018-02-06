package main.java.com.dataart.rss.web;

import main.java.com.dataart.rss.data.FeedChannel;
import main.java.com.dataart.rss.data.UserFeedItem;
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
import static main.java.com.dataart.rss.data.Reference.ALL_CHANNELS_ID;

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

        // adding "All channels" view if user has even though one channel
        if (!userChannels.isEmpty()) {
            FeedChannel allCh = new FeedChannel(ALL_CHANNELS_ID, "", "All channels", "", "All users's channel feeds");
            userChannels.add(allCh);
        }

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
        List<UserFeedItem> feeds = feedDAO.getChannelFeedsForUser(userId, channelId, pageNumber, isDesc);
        request.setAttribute("listFeed", feeds);

        int feedsNumber = feedDAO.getFeedsNumberForUser(userId, channelId);
        request.setAttribute("feedsNumber", feedsNumber);

        request.setAttribute("feedsPerPage", FEEDS_PER_PAGE);
        //request.setAttribute("currentPageNumber", pageNumber);

        // assigning feed page number for displaying in channel.jsp
        request.setAttribute("pageNumber", pageNumber);
        request.setAttribute("updatedCurrentPage", pageNumber);
        request.setAttribute("removeItemCurrentPage", pageNumber);

        request.setAttribute("currentChannelRow", channelRow);

        String sortingState = isDesc ? "desc" : "asc";

        // assigning sorting radio button state for displaying in channel.jsp
        request.setAttribute("sorting", sortingState);
        request.setAttribute("sortRBForAdd", sortingState);
        request.setAttribute("sortRBForDelete", sortingState);
        request.setAttribute("sortRBForUpdate", sortingState);
        request.setAttribute("sortRBForRemoveItem", sortingState);
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
