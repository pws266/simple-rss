package main.java.com.dataart.rss.webform;

import main.java.com.dataart.rss.service.FeedParser;

import static main.java.com.dataart.rss.data.Reference.EMPTY_CREDENTIAL_MSG;

/**
 * Created by newbie on 11.11.17.
 */
public class ChannelFormData implements IFormData {
    public String rssLink;

    private String onEmptyChannelUrl() {
        if (rssLink.isEmpty()) {
            return "RSS channel URL " + EMPTY_CREDENTIAL_MSG;
        }

        return "";
    }

    private String onRssUrlCheck() {
        if (!FeedParser.isCorrectRssLink(rssLink)) {
            return "This link isn't valid RSS channel URL. Try to use another one.";
        }

        return "";
    }

    @Override
    public String checkForm() {
        String errorMessage = onEmptyChannelUrl();

        if (!errorMessage.isEmpty())
            return errorMessage;

        return onRssUrlCheck();
    }
}
