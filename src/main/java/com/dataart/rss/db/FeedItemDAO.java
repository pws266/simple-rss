package main.java.com.dataart.rss.db;

import main.java.com.dataart.rss.data.FeedItem;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;

import static main.java.com.dataart.rss.data.Reference.UNASSIGNED_ID;

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
}
