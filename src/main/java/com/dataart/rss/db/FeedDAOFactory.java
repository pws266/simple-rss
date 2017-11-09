package main.java.com.dataart.rss.db;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static main.java.com.dataart.rss.data.Reference.*;

/**
 * Factory for getting DAO instance. Based on singleton pattern
 *
 * @author Sergey "Manual Brakes" Sokhnyshev
 * Created by newbie on 13.10.17.
 */
public final class FeedDAOFactory {
    private static FeedDAO dao = null;

    public static FeedDAO getInstance() throws IOException {
        if (dao == null) {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            InputStream is = classLoader.getResourceAsStream(DB_RESOURCE_FILE);

            Properties traits = new Properties();
            traits.load(is);

            dao = new FeedDAO(traits.getProperty("jdbc-driver-name"), traits.getProperty("db-url"),
                              traits.getProperty("db-login"), traits.getProperty("db-password"));
        }

        return dao;
    }
}
