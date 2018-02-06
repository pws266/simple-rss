package main.java.com.dataart.rss.web;

import main.java.com.dataart.rss.data.User;
import main.java.com.dataart.rss.db.ChannelDAO;
import main.java.com.dataart.rss.db.UserDAO;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

import static main.java.com.dataart.rss.data.Reference.ALL_CHANNELS_ID;

/**
 * Created by newbie on 22.12.17.
 */
@WebServlet("/delete-channel")
public class RemoveChannelServlet extends HttpServlet {
    private UserDAO userDAO;
    private ChannelDAO channelDAO;

    @Override
    public void init() {
        userDAO = UserDAO.getInstance();
        channelDAO = ChannelDAO.getInstance();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int channelId = Integer.parseInt(request.getParameter("deletedChannelId"));
        String mySort = request.getParameter("sorting");

        Helper.setSortingToSession("sortRBForDelete", request);

        if (channelId != ALL_CHANNELS_ID) {
            try {
                User currentUser = Helper.getUser(userDAO, request);

                channelDAO.deleteChannel(channelId, currentUser.getId());
            } catch (SQLException exc) {
                throw new ServletException(exc);
            }
        }

        response.sendRedirect("show");
    }
}
