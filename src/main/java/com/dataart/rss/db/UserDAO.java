package main.java.com.dataart.rss.db;

import main.java.com.dataart.rss.data.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by newbie on 07.11.17.
 */
public class UserDAO {
    private static UserDAO userInstance;
    private DataProvider db;

    public static synchronized UserDAO getInstance() {
        if (userInstance == null) {
            userInstance = new UserDAO();
        }

        return userInstance;
    }

    private UserDAO() {
        db = DataProvider.getInstance();
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
        db.connect();

        PreparedStatement statement = db.getConnection().prepareStatement(sqlQuery);
        statement.setString(1, userLogin);

        ResultSet resultSet = statement.executeQuery();

        if (resultSet.next()) {
            account = new User(resultSet.getInt("id"), resultSet.getString("name"),
                    resultSet.getString("login"), resultSet.getString("password"));
        }

        resultSet.close();
        statement.close();
        db.disconnect();

        return account;
    }

    public boolean addUser(User account) throws SQLException {
        String sqlQuery = "INSERT INTO user (name, login, password) VALUES (?, ?, ?)";
        db.connect();

        PreparedStatement statement = db.getConnection().prepareStatement(sqlQuery);
        statement.setString(1, account.getName());
        statement.setString(2, account.getLogin());
        statement.setString(3, account.getPassword());

        boolean isUserAdded = statement.executeUpdate() > 0;

        statement.close();
        db.disconnect();

        return isUserAdded;
    }
}
