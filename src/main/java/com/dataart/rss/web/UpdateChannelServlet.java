package main.java.com.dataart.rss.web;

import main.java.com.dataart.rss.data.FeedChannel;
import main.java.com.dataart.rss.data.FeedItem;
import main.java.com.dataart.rss.data.UserItem;
import main.java.com.dataart.rss.db.ChannelDAO;
import main.java.com.dataart.rss.db.FeedItemDAO;
import main.java.com.dataart.rss.db.UserDAO;
import main.java.com.dataart.rss.service.FeedParser;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;

import static main.java.com.dataart.rss.data.Reference.ALL_CHANNELS_ID;

/**
 * Created by newbie on 30.01.18.
 */
@WebServlet("/update-channel")
public class UpdateChannelServlet extends HttpServlet {
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
        int channelId = Integer.parseInt(request.getParameter("updatedChannelId"));

        String currentChannelRow = request.getParameter("updatedChannelRow");
        int channelRow = currentChannelRow.isEmpty() ? 1 : Integer.parseInt(currentChannelRow);

        String pageToDisplay = request.getParameter("updatedCurrentPage");
        int pageNumber = pageToDisplay.isEmpty() ? 1 : Integer.parseInt(pageToDisplay);

        String currentFeedRow = request.getParameter("updatedFeedRow");
        int feedRow = currentFeedRow.isEmpty() ? 2 : Integer.parseInt(currentFeedRow);

        boolean isDescSorting = "desc".equals(request.getParameter("sortRBForUpdate"));

        if (channelId != ALL_CHANNELS_ID) {
            try {
                // getting user ID
                int userId = Helper.getUser(userDAO, request).getId();

                // getting link to RSS-channel with specified ID
                String rssLink = channelDAO.findChannel(channelId).getRssLink();

                // getting channel ID assigned to specified user
                int userChannelId = channelDAO.findUserChannel(userId, channelId);

                // reading feeds of specified channel
                FeedParser rssParser = new FeedParser();
                rssParser.openRssLink(rssLink);

                // reading RSS channel info
                rssParser.readNextItem(true);

                // reading RSS items
                boolean isRead;
                while (rssParser.hasNextItem()) {
                    isRead = rssParser.readNextItem(false);

                    if (isRead) {
                        // looking for item with read GUID in ITEM table
                        FeedItem dbFeed = feedDAO.findItem(rssParser.getCurrentItem().getGuid());
                        UserItem userFeed = feedDAO.findUserItem(rssParser.getCurrentItem().getGuid(), userChannelId);

                        // adding item to ITEM and USER_ITEM tables if it isn't found in database
                        if (dbFeed == null) {
                            feedDAO.addItem(rssParser.getCurrentItem(), channelId);
                            feedDAO.assignItemToUser(rssParser.getCurrentItem().getGuid(), userChannelId);
                        }
                        // testing feed on update(different title, link or description) if it is found in ITEM table
                        else {
                            if (!rssParser.getCurrentItem().getTitle().equals(dbFeed.getTitle()) ||
                                !rssParser.getCurrentItem().getLink().equals(dbFeed.getLink()) ||
                                !rssParser.getCurrentItem().getDescription().equals(dbFeed.getDescription()) ||
                                !rssParser.getCurrentItem().getPubDate().equals(dbFeed.getPubDate())) {

                                // updating current feed in ITEM table
                                feedDAO.updateItem(rssParser.getCurrentItem());

                                // setting "isRead" flag to unread state in USER_ITEM table
                                if (userFeed != null && !userFeed.isDeleteFlag()) {
                                    feedDAO.invertFeedState(userId, channelId,rssParser.getCurrentItem().getGuid(), false);
                                }
                            }

                            if (userFeed == null) {
                                feedDAO.assignItemToUser(rssParser.getCurrentItem().getGuid(), userChannelId);
                            }
                        }
                    }
                }

                rssParser.closeRssLink();

                Helper.setChannelsToJsp(userId, channelDAO, request);
                Helper.setItemsToJsp(userId, channelId, channelRow, 1, isDescSorting, feedDAO, request);

                request.getRequestDispatcher("/channel.jsp").forward(request, response);
            } catch (SQLException | XMLStreamException | ParseException exc) {
                throw new ServletException(exc);
            }
        }

    }
}
