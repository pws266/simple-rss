package main.java.com.dataart.rss.db;

import main.java.com.dataart.rss.data.FeedChannel;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

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

    // searches for specified RSS-channel in database
    public FeedChannel findChannel(String link) throws SQLException {
        FeedChannel rssChannel = null;

        String sqlQuery = "SELECT * FROM channel WHERE link = ?";

        db.connect();
        PreparedStatement statement = db.getConnection().prepareStatement(sqlQuery);

        statement.setString(1, link);

        ResultSet resultSet = statement.executeQuery();

        if (resultSet.next()) {
            rssChannel = new FeedChannel(resultSet.getInt("id"), resultSet.getString("title"), resultSet.getString("link"),
                                         resultSet.getString("description"));}

        resultSet.close();
        statement.close();
        db.disconnect();

        return rssChannel;
    }

    // searches for specified RSS-channel saved for given user in database
    public FeedChannel findChannel(String link, int userId) throws SQLException {
        FeedChannel rssChannel = null;

        String sqlQuery = "SELECT * FROM channel ch INNER JOIN user_channel uch ON ch.id = uch.fk_channel_id " +
                          "WHERE ch.link = ? AND uch.fk_user_id = ?";

        db.connect();
        PreparedStatement statement = db.getConnection().prepareStatement(sqlQuery);

        statement.setString(1, link);
        statement.setString(2, link);

        ResultSet resultSet = statement.executeQuery();

        if (resultSet.next()) {
            rssChannel = new FeedChannel(resultSet.getInt("id"), resultSet.getString("title"), resultSet.getString("link"),
                    resultSet.getString("description"));}

        resultSet.close();
        statement.close();
        db.disconnect();

        return rssChannel;
    }

    // adds new RSS channel to database
    public boolean addChannel(FeedChannel channel, int userId, boolean isSaved) throws SQLException {
        String sqlQuery;
        PreparedStatement statement;

        db.connect();

        // RSS-channel isn't saved in CHANNEL table
        if (!isSaved) {
            sqlQuery = "INSERT INTO channel (title, link, description) VALUES (?, ?, ?)";

            statement = db.getConnection().prepareStatement(sqlQuery, Statement.RETURN_GENERATED_KEYS);

            statement.setString(1, channel.getTitle());
            statement.setString(2, channel.getLink());
            statement.setString(3, channel.getDescription());

            statement.executeUpdate();

            ResultSet generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                channel.setId(generatedKeys.getInt(1));
            } else {
                generatedKeys.close();
                statement.close();
                db.disconnect();

                throw new SQLException("ChannelDAO->addChannel: unable to get ID of new inserted RSS-channel");
            }

            generatedKeys.close();
            statement.close();
        }

        // adding RSS-channel to "USER_CHANNEL" table
        sqlQuery = "INSERT INTO user_channel (fk_user_id, fk_channel_id) VALUES (?, ?)";

        statement = db.getConnection().prepareStatement(sqlQuery);

        statement.setInt(1, userId);
        statement.setInt(2, channel.getId());

        boolean isChannelAdded = statement.executeUpdate() > 0;

        statement.close();
        db.disconnect();

        return isChannelAdded;
    }

    public boolean deleteChannel(int channelId, int userId) throws SQLException {
        // searching for given RSS-channel in "USER_CHANNEL" table
        String sqlQuery = "SELECT COUNT(*) FROM user_channel WHERE fk_user_id = ? AND fk_channel_id = ?";

        db.connect();

        PreparedStatement statement = db.getConnection().prepareStatement(sqlQuery);

        statement.setInt(1, userId);
        statement.setInt(2, channelId);

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
                                                "user_channel WHERE fk_user_id = ? AND fk_user_id = ?";

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
 }
