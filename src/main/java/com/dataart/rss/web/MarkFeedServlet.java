package main.java.com.dataart.rss.web;

import main.java.com.dataart.rss.data.User;
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
 * Created by newbie on 24.01.18.
 */
@WebServlet("/mark-feed")
public class MarkFeedServlet extends HttpServlet {
    private UserDAO userDAO;
    private FeedItemDAO feedDAO;

    @Override
    public void init() {
        userDAO = UserDAO.getInstance();
        feedDAO = FeedItemDAO.getInstance();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int channelId = Integer.parseInt(request.getParameter("channelId"));
        String feedGuid = request.getParameter("feedGuid");
        boolean feedState = Boolean.parseBoolean(request.getParameter("feedState"));

        try {
            User currentUser = Helper.getUser(userDAO, request);

            boolean isFeedRead = feedDAO.invertFeedState(currentUser.getId(), channelId, feedGuid, feedState);
        } catch (SQLException exc) {
        throw new ServletException(exc);
    }


//        String pageToDisplay = request.getParameter("pageNumber");
//        int pageNumber = pageToDisplay.isEmpty() ? 1 : Integer.parseInt(pageToDisplay);
//
//        String currentChannelRow = request.getParameter("channelRow");
//        int channelRow = currentChannelRow.isEmpty() ? 1 : Integer.parseInt(currentChannelRow);
//
//        boolean isDescSorting = "desc".equals(request.getParameter("sorting"));
//
//        try {
//            User currentUser = Helper.getUser(userDAO, request);
//
//            // getting current user channels list and setting it to *.jsp
//            Helper.setChannelsToJsp(currentUser.getId(), channelDAO, request);
//
//            // getting whole RSS-feeds number
//            // TODO: check "feedsNumber" on zero
///*
//            int feedsNumber = feedDAO.getFeedsNumberForUser(currentUser.getId(), channelId);
//
//            // getting current RSS-channel feeds
//            List<FeedItem> feeds = feedDAO.getChannelFeedsForUser(currentUser.getId(), channelId, pageNumber, isDescSorting);
//
//            request.setAttribute("listFeed", feeds);
//
//            request.setAttribute("feedsNumber", feedsNumber);
//            request.setAttribute("feedsPerPage", FEEDS_PER_PAGE);
//            request.setAttribute("currentPageNumber", pageNumber);
//            request.setAttribute("currentChannelRow", channelRow);
//*/
//            Helper.setItemsToJsp(currentUser.getId(), channelId, channelRow, pageNumber, isDescSorting, feedDAO, request);
//
//            request.getRequestDispatcher("/channel.jsp").forward(request, response);
//        } catch (SQLException exc) {
//            throw new ServletException(exc);
//        }
    }
}
