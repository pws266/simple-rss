package main.java.com.dataart.rss.db;

import main.java.com.dataart.rss.data.User;

import java.sql.*;

/**
 * Created by newbie on 12.10.17.
 */
public class FeedDAO {
    private Connection dbConnection;    // connection to RSS database

    private String dbDriverName;        // driver name for database
    private String dbUrl;               // database scheme, folder and name

    private String dbLogin;             // user login to database
    private String dbPassword;          // user password

    // creates DAO instance. Only for usage inside package. The DAO instance should be created via appropriate factory
    FeedDAO(String dbDriverName, String dbUrl, String dbLogin, String dbPassword) {
        this.dbDriverName = dbDriverName;
        this.dbUrl = dbUrl;

        this.dbLogin = dbLogin;
        this.dbPassword = dbPassword;
    }

    // establishes connection to RSS database
    private void connect() throws SQLException {
        if (dbConnection == null || dbConnection.isClosed()) {
            try {
                Class.forName(dbDriverName);
            } catch (ClassNotFoundException exc) {
                throw new SQLException(exc);
            }

            dbConnection = DriverManager.getConnection(dbUrl, dbLogin, dbPassword);
        }
    }

    // disconnects from RSS database
    private void disconnect() throws SQLException {
        if (dbConnection != null && !dbConnection.isClosed()) {
            dbConnection.close();
        }
    }

    /**
     * Searches user in DB table "USER" basing on specified login
     *
     * @param userLogin - user login for search
     * @return user ID if search is successful. Otherwise returns ID_NOT_FOUND
     * @throws SQLException if error occurs during some DB operation
     */
    public User findUser(String userLogin) throws SQLException {
        User account = null;

        String sqlQuery = "SELECT * FROM user WHERE login = ?";
        connect();

        PreparedStatement statement = dbConnection.prepareStatement(sqlQuery);
        statement.setString(1, userLogin);

        ResultSet resultSet = statement.executeQuery();

        if (resultSet.next()) {
            account = new User(resultSet.getInt("id"), resultSet.getString("name"),
                                      resultSet.getString("login"), resultSet.getString("password"));
        }

        resultSet.close();
        statement.close();
        disconnect();

        return account;
    }

    public boolean addNewUser(User account) throws SQLException {
        String sqlQuery = "INSERT INTO user (name, login, password) VALUES (?, ?, ?)";
        connect();

        PreparedStatement statement = dbConnection.prepareStatement(sqlQuery);
        statement.setString(1, account.getName());
        statement.setString(2, account.getLogin());
        statement.setString(3, account.getPassword());

        boolean isUserAdded = statement.executeUpdate() > 0;

        statement.close();
        disconnect();

        return isUserAdded;
    }
/*
    public boolean insertItem(int userId, ) {

    }
*/

}
