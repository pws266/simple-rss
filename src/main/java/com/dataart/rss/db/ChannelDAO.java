package main.java.com.dataart.rss.db;

import main.java.com.dataart.rss.data.FeedChannel;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import static main.java.com.dataart.rss.data.Reference.UNASSIGNED_ID;

/**
 * Database service operations for RSS-channels
 *
 * @author Sergey 'Manual Brakes' Sokhnyshev
 * Created on 06.11.17.
 */
public class ChannelDAO {
    private static ChannelDAO channelInstance;
    private DataProvider db;

    private ChannelDAO() {
        db = DataProvider.getInstance();
    }

    public static synchronized ChannelDAO getInstance() {
        if (channelInstance == null) {
            channelInstance = new ChannelDAO();
        }

        return channelInstance;
    }

    // searches for specified RSS-channel in CHANNEL table of database
    public FeedChannel findChannel(String rssLink) throws SQLException {
        FeedChannel rssChannel = null;

        String sqlQuery = "SELECT * FROM channel WHERE rssLink = ?";

        db.connect();
        PreparedStatement statement = db.getConnection().prepareStatement(sqlQuery);

        statement.setString(1, rssLink);

        ResultSet resultSet = statement.executeQuery();

        if (resultSet.next()) {
            rssChannel = new FeedChannel(resultSet.getInt("id"), resultSet.getString("rssLink"),
                    resultSet.getString("title"), resultSet.getString("link"),
                    resultSet.getString("description"));}

        resultSet.close();
        statement.close();
        db.disconnect();

        return rssChannel;
    }

    // searches for specified RSS-channel saved for given user in USER_CHANNEL table of database
    public FeedChannel findChannel(String rssLink, int userId) throws SQLException {
        FeedChannel rssChannel = null;

        String sqlQuery = "SELECT * FROM channel ch INNER JOIN user_channel uch ON ch.id = uch.fk_channel_id " +
                          "WHERE ch.rssLink = ? AND uch.fk_user_id = ?";

        db.connect();
        PreparedStatement statement = db.getConnection().prepareStatement(sqlQuery);

        statement.setString(1, rssLink);
        statement.setInt(2, userId);

        ResultSet resultSet = statement.executeQuery();

        if (resultSet.next()) {
            rssChannel = new FeedChannel(resultSet.getInt("id"), resultSet.getString("rssLink"),
                                         resultSet.getString("title"), resultSet.getString("link"),
                                         resultSet.getString("description"));}

        resultSet.close();
        statement.close();
        db.disconnect();

        return rssChannel;
    }

    // adds new RSS channel to CHANNEL table
    // returns added channel ID
    public int addChannel(FeedChannel channel) throws SQLException {
        String sqlQuery = "INSERT INTO channel (rssLink, title, link, description) VALUES (?, ?, ?, ?)";

        db.connect();

        PreparedStatement statement = db.getConnection().prepareStatement(sqlQuery, Statement.RETURN_GENERATED_KEYS);

        statement.setString(1, channel.getRssLink());
        statement.setString(2, channel.getTitle());
        statement.setString(3, channel.getLink());
        statement.setString(4, channel.getDescription());

        statement.executeUpdate();

        int channelId = UNASSIGNED_ID;

        ResultSet generatedKeys = statement.getGeneratedKeys();

        if (generatedKeys.next()) {
            channelId = generatedKeys.getInt(1);
        } else {
            generatedKeys.close();
            statement.close();
            db.disconnect();

            throw new SQLException("ChannelDAO->addChannel: unable to get ID of new inserted RSS-channel");
        }

        generatedKeys.close();
        statement.close();

        db.disconnect();

        return channelId;
    }

    // assigns existing RSS channel to user with specified ID (copies given channel to USER_CHANNEL table)
    public int assignChannelToUser(int channelId, int userId) throws SQLException {
        String sqlQuery = "INSERT INTO user_channel (fk_user_id, fk_channel_id) VALUES (?, ?)";

        db.connect();
        PreparedStatement statement = db.getConnection().prepareStatement(sqlQuery, Statement.RETURN_GENERATED_KEYS);

        statement.setInt(1, userId);
        statement.setInt(2, channelId);

        statement.executeUpdate();

        int userChannelId;

        ResultSet generatedKeys = statement.getGeneratedKeys();

        if (generatedKeys.next()) {
            userChannelId = generatedKeys.getInt(1);
        } else {
            generatedKeys.close();
            statement.close();
            db.disconnect();

            throw new SQLException("ChannelDAO->assignChannelToUser: unable to get ID of user and RSS-channel record");
        }

        generatedKeys.close();


//        boolean isAssigned = statement.executeUpdate() > 0;

        statement.close();
        db.disconnect();

//        return isAssigned;

        return userChannelId;
    }

    //TODO: rewrite method via ONE query to DB (see FeedItemDAO->copyItemsToUser)
    public boolean deleteChannel(int channelId, int userId) throws SQLException {
        // searching for given RSS-channel in "USER_CHANNEL" table
        String sqlQuery = "SELECT COUNT(*) FROM user_channel WHERE fk_channel_id = ?";
        // String sqlQuery = "SELECT COUNT(*) FROM user_channel WHERE fk_user_id = ? AND fk_channel_id = ?";

        db.connect();

        PreparedStatement statement = db.getConnection().prepareStatement(sqlQuery);

//        statement.setInt(1, userId);
//        statement.setInt(2, channelId);
        statement.setInt(1, channelId);

        int channelUsersNumber = UNASSIGNED_ID;
        ResultSet resultSet = statement.executeQuery();

        if (resultSet.next()) {
            channelUsersNumber = resultSet.getInt(1);
        }

        resultSet.close();
        statement.close();

        // wrong user ID or channel ID if it was found nothing
        if (channelUsersNumber < 1) {
            db.disconnect();
            return false;
        }

        // deleting reference on channel from USER_CHANNEL table or channel from CHANNEL table
        sqlQuery = "DELETE FROM ";
        sqlQuery += (channelUsersNumber == 1) ? "channel WHERE id = ?" :
                                                "user_channel WHERE fk_channel_id = ? AND fk_user_id = ?";

        statement = db.getConnection().prepareStatement(sqlQuery);

        statement.setInt(1, channelId);
        if (channelUsersNumber > 1) {
            statement.setInt(2, userId);
        }

        boolean isChannelDeleted = statement.executeUpdate() > 0;

        statement.close();
        db.disconnect();

        return isChannelDeleted;
    }

    // "getAllUserChannels" returns all channels that are belonged to specified user
    public List<FeedChannel> getUserChannels(int userId) throws SQLException {
        List<FeedChannel> userChannels = new ArrayList<>();

        String sqlQuery = "SELECT * FROM channel ch INNER JOIN user_channel uch " +
                           "ON uch.fk_channel_id = ch.id WHERE uch.fk_user_id = ?";

        db.connect();

        PreparedStatement statement = db.getConnection().prepareStatement(sqlQuery);
        statement.setInt(1, userId);

        ResultSet resultSet = statement.executeQuery();

        while (resultSet.next()) {
            FeedChannel currentChannel = new FeedChannel();

            currentChannel.setId(resultSet.getInt("id"));
            currentChannel.setRssLink(resultSet.getString("rssLink"));
            currentChannel.setTitle(resultSet.getString("title"));
            currentChannel.setLink(resultSet.getString("link"));
            currentChannel.setDescription(resultSet.getString("description"));

            userChannels.add(currentChannel);
        }

        resultSet.close();
        statement.close();

        db.disconnect();

        return userChannels;
    }
 }
