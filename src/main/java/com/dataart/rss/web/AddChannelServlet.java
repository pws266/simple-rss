package main.java.com.dataart.rss.web;

import main.java.com.dataart.rss.db.ChannelDAO;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;

/**
 * Created by newbie on 06.11.17.
 */
@WebServlet("/add-channel")
public class AddChannelServlet extends HttpServlet {
   private ChannelDAO channelDAO;

    @Override
    public void init() {
        channelDAO = ChannelDAO.getInstance();
    }
}
