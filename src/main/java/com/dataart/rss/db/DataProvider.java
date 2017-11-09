package main.java.com.dataart.rss.db;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import static main.java.com.dataart.rss.data.Reference.DB_RESOURCE_FILE;

/**
 * Created by newbie on 06.11.17.
 */
public class DataProvider {
    private Connection dbConnection;    // connection to RSS database

    private String dbDriverName;        // driver name for database
    private String dbUrl;               // database scheme, folder and name

    private String dbLogin;             // user login to database
    private String dbPassword;          // user password

    private static DataProvider baseInstance;

    private DataProvider() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream is = classLoader.getResourceAsStream(DB_RESOURCE_FILE);

        Properties dbCreds = new Properties();

        try {
            dbCreds.load(is);
        } catch (IOException exc) {
            throw new RuntimeException(exc);
        }

        dbDriverName = dbCreds.getProperty("jdbc-driver-name");
        dbUrl = dbCreds.getProperty("db-url");

        dbLogin = dbCreds.getProperty("db-login");
        dbPassword = dbCreds.getProperty("db-password");
    }

    public static synchronized DataProvider getInstance() {
        if (baseInstance == null) {
            baseInstance = new DataProvider();
        }

        return baseInstance;
    }

    // establishes connection to RSS database
    public void connect() throws SQLException {
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
    public void disconnect() throws SQLException {
        if (dbConnection != null && !dbConnection.isClosed()) {
            dbConnection.close();
        }
    }

    // returns connection to specified database
    public Connection getConnection() {
        return dbConnection;
    }
}
