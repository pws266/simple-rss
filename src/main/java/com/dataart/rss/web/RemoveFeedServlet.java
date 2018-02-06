package main.java.com.dataart.rss.web;

import main.java.com.dataart.rss.data.Reference;
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
 * Servlet for RSS - item deleting
 *
 * @author Sergey Sokhnyshev
 * Created on 06.02.18.
 */
@WebServlet("/remove-item")
public class RemoveFeedServlet extends HttpServlet {
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
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException{
        // getting feed row number and page number and changing its values after feed deleting
        String currentItemRow = request.getParameter("removeItemRow");
        int itemRow = currentItemRow.isEmpty() ? 2 : Integer.parseInt(currentItemRow);
        --itemRow;

        String currentPageStr = request.getParameter("removeItemCurrentPage");
        int currentPage = currentPageStr.isEmpty() ? 1 : Integer.parseInt(currentPageStr);

        if (itemRow < 2) {
            --currentPage;
            itemRow = Reference.FEEDS_PER_PAGE + 1;

            if (currentPage < 1) {
                currentPage = 1;
                itemRow = 2;
            }
        }

        boolean isDescSorting = "desc".equals(request.getParameter("sortRBForRemoveItem"));

        int channelId = Integer.parseInt(request.getParameter("removeItemChannelId"));

        String currentChannelRow = request.getParameter("removeItemChannelRow");
        int channelRow = currentChannelRow.isEmpty() ? 1 : Integer.parseInt(currentChannelRow);

        String itemGuid = request.getParameter("removeFeedGuid");

        try {
            // getting user ID
            int userId = Helper.getUser(userDAO, request).getId();

            feedDAO.deleteUserItem(userId, itemGuid);

            Helper.setChannelsToJsp(userId, channelDAO, request);
            Helper.setItemsToJsp(userId, channelId, channelRow, currentPage, isDescSorting, feedDAO, request);

            request.setAttribute("currentFeedRow", itemRow);

            request.getRequestDispatcher("/channel.jsp").forward(request, response);
        } catch (SQLException | IOException exc) {
            throw new ServletException(exc);
        }
    }
}
