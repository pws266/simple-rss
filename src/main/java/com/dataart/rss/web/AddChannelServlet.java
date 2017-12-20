package main.java.com.dataart.rss.web;

import com.sun.istack.internal.Nullable;
import main.java.com.dataart.rss.data.FeedChannel;
import main.java.com.dataart.rss.data.User;
import main.java.com.dataart.rss.db.ChannelDAO;
import main.java.com.dataart.rss.db.FeedItemDAO;
import main.java.com.dataart.rss.db.UserDAO;
import main.java.com.dataart.rss.service.FeedParser;
import main.java.com.dataart.rss.webform.ChannelFormData;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;

import static main.java.com.dataart.rss.data.Reference.UNASSIGNED_ID;

/**
 * Created by newbie on 06.11.17.
 */
@WebServlet("/add-channel")
public class AddChannelServlet extends HttpServlet {
    private UserDAO userDAO;
    private ChannelDAO channelDAO;
    private FeedItemDAO itemDAO;

    @Override
    public void init() {
        userDAO = UserDAO.getInstance();
        channelDAO = ChannelDAO.getInstance();
        itemDAO = FeedItemDAO.getInstance();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ChannelFormData formData = new ChannelFormData();
        formData.rssLink = request.getParameter("rssLink");

        // validating RSS link content
        String errorMessage = formData.checkForm();
        if (!errorMessage.isEmpty()) {
            sendErrorMessage(errorMessage, formData, request, response);
            return;
        }

        // getting user ID
        User currentUser = getCurrentUser(request, response, formData);
        if (currentUser == null) {
            return;
        }
//        HttpSession currentSession = request.getSession(false);
//        String userLogin = (String) currentSession.getAttribute("userLogin");
//
//        User currentUser;
//
//        try {
//            currentUser = userDAO.findUser(userLogin);
//        } catch (SQLException exc) {
//            throw new ServletException(exc);
//        }
//
//        if (currentUser == null) {
//            errorMessage = "User @" + userLogin + " isn't registered.";
//
//            sendErrorMessage(errorMessage, formData, request, response);
//            return;
//        }

        // looking for specified RSS-channel in USER_CHANNEL table
        if (isUserChannelFound(request, response, currentUser, formData)) {
            return;
        }

//        try {
//            channel = channelDAO.findChannel(formData.rssLink, currentUser.getId());
//        } catch (SQLException exc) {
//            throw new ServletException(exc);
//        }
//
//        if (channel != null) {
//            errorMessage = "This channel is already in @" + currentUser.getLogin() + " channels list";
//
//            sendErrorMessage(errorMessage, formData, request, response);
//            return;
//        }
//
        // looking for specified channel in general CHANNEL table
        FeedChannel channel;

        try {
            channel = channelDAO.findChannel(formData.rssLink);
        } catch (SQLException exc) {
            throw new ServletException(exc);
        }


        if (channel == null) {
            try {
                FeedParser rssParser = new FeedParser();
                rssParser.openRssLink(formData.rssLink);

                rssParser.readNextItem(true);
                FeedChannel newChannel = new FeedChannel(formData.rssLink, rssParser.getCurrentItem().getTitle(),
                                                         rssParser.getCurrentItem().getLink(),
                                                         rssParser.getCurrentItem().getDescription());

                int channelId = channelDAO.addChannel(newChannel);
                int userChannelId = channelDAO.assignChannelToUser(channelId, currentUser.getId());

                // здесь копирование Items в ITEM и USER_ITEM

                boolean isRead;

                while (rssParser.hasNextItem()) {
                    isRead = rssParser.readNextItem(false);

                    if (isRead) {
                        itemDAO.addItem(rssParser.getCurrentItem(), channelId);
                        itemDAO.assignItemToUser(rssParser.getCurrentItem().getGuid(), userChannelId);
                    }
                }

                rssParser.closeRssLink();
            } catch (XMLStreamException | SQLException | ParseException exc) {
                throw new ServletException(exc);
            }

            // парсим канал из RSS XML, добавляем его в CHANNEL, USER_CHANNEL, а также заполняем элементы ITEM и USER_ITEM
            // все делает RSS парсер
        } else {
            // хорошо бы сделать Update канала
            try {
                //
                int userChannelId = channelDAO.assignChannelToUser(channel.getId(), currentUser.getId());
                // копируем записи в USER_ITEM, все делаем непрочитанными
                itemDAO.copyItemsToUser(userChannelId);
            } catch (SQLException exc) {
                throw new ServletException(exc);
            }
        }


        // сливаем каналы пользователя в список и отдаем в channel.jsp, там разбираем
        response.sendRedirect("show");
//        try {
//            List<FeedChannel> userChannels = channelDAO.getUserChannels(currentUser.getId());
//
//            request.setAttribute("listChannel", userChannels);
//            request.getRequestDispatcher("/channel.jsp").forward(request, response);
//        } catch (SQLException exc) {
//            throw new ServletException(exc);
//        }
    }

    private void sendErrorMessage(String message, ChannelFormData formData, HttpServletRequest request,
                                  HttpServletResponse response) throws ServletException, IOException {
        request.setAttribute("rssLink", formData.rssLink);

        request.setAttribute("errorMessage", message);
        request.setAttribute("messageColor", "indianred");

        request.getRequestDispatcher("/channel.jsp").forward(request, response);
    }

    // "getCurrentUserId" returns current user ID based on "userLogin" value transferred via session
    private User getCurrentUser(HttpServletRequest request, HttpServletResponse response,
                                 ChannelFormData formData) throws ServletException, IOException {
        HttpSession currentSession = request.getSession(false);
        String userLogin = (String) currentSession.getAttribute("userLogin");

        User currentUser;

        try {
            currentUser = userDAO.findUser(userLogin);
        } catch (SQLException exc) {
            throw new ServletException(exc);
        }

        if (currentUser == null) {
            String errorMessage = "User @" + userLogin + " isn't registered.";
            sendErrorMessage(errorMessage, formData, request, response);
        }

        return currentUser;
    }

    private boolean isUserChannelFound(HttpServletRequest request, HttpServletResponse response, User currentUser,
                                            ChannelFormData formData) throws ServletException, IOException {
        FeedChannel channel;

        try {
            channel = channelDAO.findChannel(formData.rssLink, currentUser.getId());
        } catch (SQLException exc) {
            throw new ServletException(exc);
        }

        if (channel != null) {
            String errorMessage = "This channel is already in @" + currentUser.getLogin() + " channels list";

            sendErrorMessage(errorMessage, formData, request, response);
            return true;
        }

        return false;
    }
}
