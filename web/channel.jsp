<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<html>
<head>
    <meta charset="utf-8">
    <title>Simple RSS Reader</title>

    <link rel="icon" href="resources/images/rss-reader-32x32.png" sizes="32x32" />
    <link href="resources/css/simplePagination.css" rel="stylesheet" type="text/css" />
    <link href="resources/css/channel-style.css" rel="stylesheet" type="text/css" />

    <script type="text/javascript" src="resources/js/jquery-1.8.3.js"></script>
    <script type="text/javascript" src="resources/js/jquery.simplePagination.js"></script>
    <script type="text/javascript" src="resources/js/select-channel.js"></script>


</head>
<body>
    <h1>Channels list</h1>

    <div id="header">
        <%--@elvariable id="userName" type="java.lang.String"--%>
        <%--@elvariable id="userLogin" type="java.lang.String"--%>
        <c:choose>
            <c:when test="${empty userLogin}">
                <p style="color:indianred; font-weight:bold">There is no logged in users</p>
            </c:when>

            <c:when test="${not empty userName && not empty userLogin}">
                <form action="logout" method="GET">
                    <p style="color:royalblue; font-weight:bold">Current user:
                        <jsp:text>${userName} @${userLogin}</jsp:text>
                        <input type="submit" value="Logout" />
                    </p>
                </form>
            </c:when>
        </c:choose>

        <form action="add-channel" method="POST" id="addChannelForm"></form>
        <form action="show-feeds" method="POST" id="showFeedsForm">
            <input type="hidden" id="ShowChannel" name="displayChannelId" value="">
        </form>
        <form action="delete-channel" method="POST" id="deleteChannelForm">
            <input type="hidden" id="DelChannel" name="deletedChannelId" value="">
        </form>

        <!--table frame="box"-->
        <table id="rssOperation">
            <tr>
                <!--td colspan="3" align="center" style="color:white; background-color: cornflowerblue"-->
                <th colspan="3" align="center">
                    <b>RSS channel operations</b>
                </th>
            </tr>

            <tr>
                <td><b>New channel link</b></td>
                <td>
                    <input style="width: 250px" title="Channel link" type="text" name="rssLink"
                           value='${requestScope["rssLink"]}' form="addChannelForm" />
                </td>

                <td><input type="submit" style="width: 60px" value="Add" form="addChannelForm" /></td>
            </tr>

            <tr>
                <td><b>Selected channel</b></td>
                <td align="right"><input type="submit" style="width: 60px" value="Show" form="showFeedsForm" /></td>
                <td><input type="submit" style="width: 60px" value="Remove" form="deleteChannelForm" /></td>

            </tr>
        </table>

        <%--@elvariable id="errorMessage" type="java.lang.String"--%>
        <%--@elvariable id="messageColor" type="java.lang.String"--%>
        <c:choose>
            <c:when test="${not empty errorMessage}">
                <p style="color:${messageColor}; font-weight:bold"><jsp:text>
                    ${errorMessage}</jsp:text></p>
            </c:when>
            <c:otherwise>
                <br>
            </c:otherwise>
        </c:choose>
    </div>

    <div id="main-wrap">
        <!-- Channels list implementation -->
        <div id="sidebar">
<!--            <table id="rssList" frame="box"-->
            <table id="rssList" align="center">
                <tr>
<!--                    <th colspan="1" align="center" style="color:white; background-color: lightsalmon"-->
                    <th colspan="1">
                        <b>Channels list</b>
                    </th>
                </tr>

                    <c:forEach var="curChannel" items="${listChannel}">
                        <tr>
                            <td><c:out value="${curChannel.title}" /></td>
                            <td><c:out value="${curChannel.link}" /></td>
                            <td><c:out value="${curChannel.description}" /></td>
                            <td><c:out value="${curChannel.id}" /></td>
                        </tr>
                    </c:forEach>


            </table>
        </div>
        <div id="content-wrap">
            <table id="rssFeed" align="center">
                <tr>
                    <th id="top-th" colspan="2">
                        <b>Feeds</b>
                    </th>
                </tr>
                <tr>
                    <th id="left-th">Time</th>
                    <th id="right-th">Headline</th>
                </tr>

                <c:forEach var="curFeed" items="${listFeed}">
                    <tr>
                        <td><c:out value="${curFeed.pubDate}" /></td>
                        <td><c:out value="${curFeed.title}" /></td>
                    </tr>
                </c:forEach>
            </table>
<!--
            <div id="info-wrap">
                <div class="info">small info </div>
                <div class="info">small info</div>
            </div>
            Content
-->
            <div id="feed-pagination">
                Fuck!
            </div>
        </div>
    </div>

    <!-- "Windows for selected channel and feed description"-->
    <div id="footer">
        <div id="channel-desc">
            <table id="channelInfo" align="center">
                <tr>
                    <th colspan="1">
                        <b>Selected channel info</b>
                    </th>
                </tr>
                <tr id="channel-link-row">
                    <td>
                        <b>Link: </b>
                    </td>
                </tr>
                <tr id="channel-description-row">
                    <td>
                        <b>Description: </b>
                    </td>
                </tr>
            </table>
        </div>

        <div id="feed-desc">
            Display here feed info!
        </div>
    </div>
</body>
</html>