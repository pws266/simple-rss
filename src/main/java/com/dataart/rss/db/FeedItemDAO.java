package main.java.com.dataart.rss.db;

import main.java.com.dataart.rss.data.FeedItem;
import main.java.com.dataart.rss.data.UserFeedItem;
import main.java.com.dataart.rss.data.UserItem;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import static main.java.com.dataart.rss.data.Reference.ALL_CHANNELS_ID;
import static main.java.com.dataart.rss.data.Reference.FEEDS_PER_PAGE;

/**
 * Database service operations for single RSS-item
 *
 * @author Sergey "AIM" Sokhnyshev
 * Created on 13.11.17.
 */

public class FeedItemDAO {
    private static FeedItemDAO feedItemInstance;
    private DataProvider db;

    private FeedItemDAO() {
        db = DataProvider.getInstance();
    }

    public static synchronized FeedItemDAO getInstance() {
        if (feedItemInstance == null) {
            feedItemInstance = new FeedItemDAO();
        }

        return feedItemInstance;
    }

    // searches for RSS item with specified GUID in ITEM table of database
    public FeedItem findItem(String guid) throws SQLException {
        FeedItem rssItem = null;

        String sqlQuery = "SELECT * FROM item WHERE guid = ?";

        db.connect();
        PreparedStatement statement = db.getConnection().prepareStatement(sqlQuery);

        statement.setString(1, guid);

        ResultSet resultSet = statement.executeQuery();

        if (resultSet.next()) {
            rssItem = new FeedItem(resultSet.getString("guid"),
                                   resultSet.getString("title"), resultSet.getString("link"),
                                   resultSet.getString("description"), resultSet.getTimestamp("pubDate"));}

        resultSet.close();
        statement.close();

        db.disconnect();

        return rssItem;
    }

    // searches for RSS-item inside USER_ITEM table of specified user
    public UserItem findUserItem(String guid, int userChannelId) throws SQLException {
        UserItem userItem = null;

        String sqlQuery = "SELECT * FROM user_item WHERE fk_item_guid = ? AND fk_user_channel_id = ?";

        db.connect();
        PreparedStatement statement = db.getConnection().prepareStatement(sqlQuery);

        statement.setString(1, guid);
        statement.setInt(2, userChannelId);

        ResultSet resultSet = statement.executeQuery();

        if (resultSet.next()) {
            userItem = new UserItem(resultSet.getString("fk_item_guid"), resultSet.getInt("fk_user_channel_id"),
                                    resultSet.getBoolean("isRead"), resultSet.getBoolean("isDelete"));}

        resultSet.close();
        statement.close();

        db.disconnect();

        return userItem;
    }

    // updates fields of RSS-item in ITEM table
    public boolean updateItem(FeedItem item) throws SQLException, ParseException {
        String sqlQuery = "UPDATE item SET title = ?, link = ?, description = ?, pubDate = ? WHERE guid = ?";

        db.connect();
        PreparedStatement statement = db.getConnection().prepareStatement(sqlQuery);

        statement.setString(1, item.getTitle());
        statement.setString(2, item.getLink());
        statement.setString(3, item.getDescription());
        statement.setTimestamp(4, item.getTimestamp());
        statement.setString(5, item.getGuid());

        boolean isOk = statement.executeUpdate() > 0;

        statement.close();
        db.disconnect();

        return isOk;
    }

    // adds unknown RSS-item of specified RSS-channel to ITEM table
    public boolean addItem(FeedItem item, int channelId) throws SQLException, ParseException {
        String sqlQuery = "INSERT INTO item (guid, title, link, description, pubDate, fk_channel_id) " +
                          "VALUES (?, ?, ?, ?, ?, ?)";

        db.connect();

        PreparedStatement statement = db.getConnection().prepareStatement(sqlQuery);

        statement.setString(1, item.getGuid());
        statement.setString(2, item.getTitle());
        statement.setString(3, item.getLink());
        statement.setString(4, item.getDescription());
        // statement.setString(4, item.parseDescription());
        statement.setTimestamp(5, item.getTimestamp());
        statement.setInt(6, channelId);

        boolean isItemAdded = statement.executeUpdate() > 0;

        statement.close();
        db.disconnect();

        return isItemAdded;
    }

    // assigns RSS item with specified GUID to given user
    public boolean assignItemToUser(String itemGuid, int userChannelId) throws SQLException {
        String sqlQuery = "INSERT INTO user_item (fk_user_channel_id, fk_item_guid) VALUES (?, ?)";

        db.connect();
        PreparedStatement statement = db.getConnection().prepareStatement(sqlQuery);

        statement.setInt(1, userChannelId);
        statement.setString(2, itemGuid);

        boolean isAssigned = statement.executeUpdate() > 0;

        statement.close();
        db.disconnect();

        return isAssigned;
    }

    // copies existing RSS items from ITEM table to USER_ITEM table for specified channel
    public boolean copyItemsToUser(int userChannelId) throws SQLException {
        String sqlQuery = "INSERT INTO user_item (fk_user_channel_id, fk_item_guid) " +
                          "SELECT uch.id, guid FROM item it INNER JOIN user_channel uch " +
                          "ON it.fk_channel_id = uch.fk_channel_id WHERE uch.id = ?";

        db.connect();
        PreparedStatement statement = db.getConnection().prepareStatement(sqlQuery);

        statement.setInt(1, userChannelId);

        boolean isOk = statement.executeUpdate() > 0;

        statement.close();
        db.disconnect();

        return isOk;
    }

    // "getFeedsNumberForUser" returns number of RSS-feeds for specified user and channel
    public int getFeedsNumberForUser(int userId, int channelId) throws SQLException {
//        String sqlQuery = "SELECT COUNT(*) FROM user_item uit INNER JOIN user_channel uch " +
//                          "ON uit.fk_user_channel_id = uch.id WHERE uch.fk_user_id = ? AND uch.fk_channel_id = ?";
        String sqlQuery = "SELECT COUNT(*) FROM user_item uit INNER JOIN user_channel uch " +
                          "ON uit.fk_user_channel_id = uch.id WHERE uit.isDelete = false AND uch.fk_user_id = ?";

        if (channelId != ALL_CHANNELS_ID) {
            sqlQuery += " AND uch.fk_channel_id = ?";
        }


        db.connect();

        PreparedStatement statement = db.getConnection().prepareStatement(sqlQuery);

        statement.setInt(1, userId);
//        statement.setInt(2, channelId);
        if (channelId != ALL_CHANNELS_ID) {
            statement.setInt(2, channelId);
        }

        int feedsNumber = 0;
        ResultSet resultSet = statement.executeQuery();

        if (resultSet.next()) {
            feedsNumber = resultSet.getInt(1);
        }

        resultSet.close();

        statement.close();
        db.disconnect();

        return feedsNumber;
    }

    // "getChannelFeeds" returns list of RSS-feeds of user and RSS-channel with specified IDs
    public List<UserFeedItem> getChannelFeedsForUser(int userId, int channelId, int pageNumber, boolean isDesc) throws SQLException {
        List<UserFeedItem> feeds = new ArrayList<>();

//        String sqlQueryPattern = "SELECT it.*, uit.isRead FROM item it INNER JOIN user_item uit ON it.guid = uit.fk_item_guid " +
//                                 "INNER JOIN user_channel uch ON uit.fk_user_channel_id = uch.id " +
//                                 "WHERE uch.fk_user_id = ? AND uch.fk_channel_id = ? ORDER BY it.pubDate ";
        String sqlQueryPattern = "SELECT it.*, uit.isRead, uit.isDelete FROM item it INNER JOIN user_item uit " +
                "ON it.guid = uit.fk_item_guid INNER JOIN user_channel uch ON uit.fk_user_channel_id = uch.id " +
                "WHERE uit.isDelete = false AND uch.fk_user_id = ? "; //"AND uch.fk_channel_id = ? ORDER BY it.pubDate ";

        if (channelId != ALL_CHANNELS_ID) {
            sqlQueryPattern += "AND uch.fk_channel_id = ? ";
        }

        sqlQueryPattern += "ORDER BY it.pubDate ";

        sqlQueryPattern += isDesc ? "DESC " : "ASC ";
        sqlQueryPattern += "LIMIT %d, %d";

        // int offset = (pageNumber - 1)*FEEDS_PER_PAGE + 1;
        int offset = (pageNumber - 1)*FEEDS_PER_PAGE;
        String sqlQuery = String.format(sqlQueryPattern, offset, FEEDS_PER_PAGE);

        db.connect();

        PreparedStatement statement = db.getConnection().prepareStatement(sqlQuery);
        statement.setInt(1, userId);
//        statement.setInt(2, channelId);
        if (channelId != ALL_CHANNELS_ID) {
            statement.setInt(2, channelId);
        }

        ResultSet resultSet = statement.executeQuery();

        while (resultSet.next()) {
            UserFeedItem currentItem = new UserFeedItem();

            // TODO: custom structure for search results
            currentItem.setGuid(resultSet.getString("guid"));
            currentItem.setTitle(resultSet.getString("title"));
            currentItem.setLink(resultSet.getString("link"));


            // TODO: make correct "text" field reading via ajax
            currentItem.setDescription(resultSet.getString("description"));

            currentItem.setPubDate(resultSet.getTimestamp("pubDate"));
            currentItem.setReadFlag(resultSet.getBoolean("isRead"));
            currentItem.setDeleteFlag(resultSet.getBoolean("isDelete"));

            if (!currentItem.isDeleteFlag()) {
                feeds.add(currentItem);
            }
        }

        resultSet.close();
        statement.close();

        db.disconnect();

        return feeds;
    }

    // changes feed from "unread" to "read" and vice versa. Returns updated state of USER_ITEM feed flag "isRead"
    public boolean invertFeedState(int userId, int channelId, String feedGuid, boolean feedState) throws SQLException {
        String sqlQuery = "SELECT uch.id, uit.isRead FROM user_item uit INNER JOIN user_channel uch " +
                          "ON uit.fk_user_channel_id = uch.id WHERE uch.fk_user_id = ? AND uit.fk_item_guid = ?";

        if (channelId != ALL_CHANNELS_ID) {
            sqlQuery += " AND uch.fk_channel_id = ?";
        }

        db.connect();

        PreparedStatement statement = db.getConnection().prepareStatement(sqlQuery);

        statement.setInt(1, userId);
        statement.setString(2, feedGuid);

        if (channelId != ALL_CHANNELS_ID) {
            statement.setInt(3, channelId);
        }

        ResultSet resultSet = statement.executeQuery();

        int userAndChannelId;
        boolean isFeedRead;

        if (resultSet.next()) {
            userAndChannelId = resultSet.getInt("id");
            isFeedRead = resultSet.getBoolean("isRead");
        }
        else {
            resultSet.close();
            statement.close();

            db.disconnect();

            throw new SQLException("FeedItemDAO->invertFeedState: Feed is not found");
        }

        resultSet.close();
        statement.close();

        // updating feed state
        isFeedRead = !isFeedRead;

        sqlQuery = "UPDATE user_item SET isRead = ? WHERE fk_user_channel_id = ? AND fk_item_guid = ?";

        statement = db.getConnection().prepareStatement(sqlQuery);

        // statement.setBoolean(1, isFeedRead);
        statement.setBoolean(1, feedState);
        statement.setInt(2, userAndChannelId);
        statement.setString(3, feedGuid);

        statement.executeUpdate();

        statement.close();
        db.disconnect();

        return isFeedRead;
    }

    // marks RSS - item with specified GUID as deleted in USER_ITEM table
    public void deleteUserItem(int userId, String guid) throws SQLException{
        // getting user's channel ID
        String sqlQuery = "SELECT uch.id FROM user_channel uch INNER JOIN item it ON it.fk_channel_id = uch.fk_channel_id WHERE uch.fk_user_id = ? AND it.guid = ?";

        db.connect();

        PreparedStatement statement = db.getConnection().prepareStatement(sqlQuery);

        statement.setInt(1, userId);
        statement.setString(2, guid);

        ResultSet resultSet = statement.executeQuery();

        int userAndChannelId;

        if (resultSet.next()) {
            userAndChannelId = resultSet.getInt("id");
        }
        else {
            resultSet.close();
            statement.close();

            db.disconnect();

            throw new SQLException("FeedItemDAO->deleteUserItem: User's channel is not found");
        }

        resultSet.close();
        statement.close();

        // marking user item as "deleted"
        sqlQuery = "UPDATE user_item SET isDelete = true WHERE fk_user_channel_id = ? AND fk_item_guid = ?";

        statement = db.getConnection().prepareStatement(sqlQuery);

        // statement.setBoolean(1, isFeedRead);
        statement.setInt(1, userAndChannelId);
        statement.setString(2, guid);

        statement.executeUpdate();

        statement.close();
        db.disconnect();
    }
}
