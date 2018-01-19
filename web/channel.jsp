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

    <script type="text/javascript">
        var feedsNumber = <%=request.getAttribute("feedsNumber")%>,
            feedsPerPage = <%=request.getAttribute("feedsPerPage")%>,
            currentPage = <%=request.getAttribute("currentPageNumber")%>,
            currentChannelRow=<%=request.getAttribute("currentChannelRow")%>;
    </script>

    <script type="text/javascript" src="resources/js/select-channel.js"></script>


</head>
<body>
    <h1>Channels list</h1>

    <div id="top-block">
        <table id="topInfo" align="center">
            <form action="logout" method="GET" id="logoutForm"></form>
            <tr>
                <%--@elvariable id="userName" type="java.lang.String"--%>
                <%--@elvariable id="userLogin" type="java.lang.String"--%>
                <c:choose>
                    <c:when test="${empty userLogin}">
                        <td align="left" style="width: 45%; color:indianred; font-weight:bold">
                            There is no logged in users
                        </td>
                    </c:when>

                    <c:when test="${not empty userName && not empty userLogin}">
                        <td align="left" style="width: 45%; color:royalblue; font-weight:bold">
                            Current user: <jsp:text>${userName} @${userLogin}</jsp:text>
                            <input type="submit" value="Logout" form="logoutForm" />
                        </td>
                    </c:when>
                </c:choose>

                <td align="left" style="width: 55%; color:${messageColor}; font-weight: bold">
                    <%--@elvariable id="errorMessage" type="java.lang.String"--%>
                    <%--@elvariable id="messageColor" type="java.lang.String"--%>
                    <c:choose>
                        <c:when test="${not empty errorMessage}">
                            <jsp:text>${errorMessage}</jsp:text>
                        </c:when>
                    </c:choose>
                </td>
            </tr>
        </table>
    </div>

    <div id="header">
        <div id="channel-operation">
            <form action="add-channel" method="POST" id="addChannelForm">
                <input type="hidden" id="SortRBForAdd" name="sortRBForAdd" value='${requestScope["sortRBForAdd"]}'>
            </form>
            <form action="show-feeds" method="POST" id="showFeedsForm">
                <input type="hidden" id="ShowChannel" name="displayChannelId" value="">
                <input type="hidden" id="ChannelRow" name="channelRow" value="">
                <input type="hidden" id="PageForShow" name="pageNumber" value='${requestScope["pageNumber"]}'>
            </form>
            <form action="delete-channel" method="POST" id="deleteChannelForm">
                <input type="hidden" id="DelChannel" name="deletedChannelId" value="">
                <input type="hidden" id="SortRBForDelete" name="sortRBForDelete" value='${requestScope["sortRBForDelete"]}'>
            </form>

            <table id="rssOperation" align="center">
                <tr>
                    <!--td colspan="3" align="center" style="color:white; background-color: cornflowerblue"-->
                    <th id="caption-th" colspan="2" align="center">
                        Channels control
                    </th>
                </tr>

                <tr>
                    <td class="oper-left-td">RSS link:</td>
                    <td class="oper-right-td">
                        <input style="width: 97%" title="Channel link" type="text" name="rssLink"
                               value='${requestScope["rssLink"]}' form="addChannelForm" />
                    </td>
                </tr>

                <tr>
                    <td class="oper-left-td">Action:</td>
                    <td class="oper-right-td">
                        <input type="submit" style="width: 60px; padding: 0" value="Add" form="addChannelForm" />
                        <input type="submit" style="width: 60px; padding: 0" value="Show" form="showFeedsForm" />
                        <input type="submit" style="width: 60px; padding: 0" value="Remove" form="deleteChannelForm" />
                    </td>
                </tr>
            </table>
        </div>

        <div id="feed-operation">
            <!-- Feeds control table-->
            <table class="controlTable" align="center">
                <tr>
                    <th class="ct-caption-th" colspan="2" align="center">
                        Feeds control
                    </th>
                </tr>

                <tr>
                    <th class="ct-group-th" colspan="2" align="left">Sorting</th>
                </tr>
                <tr>
                    <td class="ct-left-td" align="right">
                        <input name="sorting" type="radio" value="asc" ${requestScope["sorting"]=='asc'?'checked':''} form="showFeedsForm">
                    </td>
                    <td class="ct-right-td">
                        Ascending
                    </td>
                </tr>

                <tr>
                    <td class="ct-left-td" align="right">
                        <input name="sorting" type="radio" value="desc" ${requestScope["sorting"]=='desc'?'checked':''} form="showFeedsForm">
                    </td>
                    <td class="ct-right-td">
                        Descending
                    </td>
                </tr>

                <tr>
                    <td class="ct-left-td">Action:</td>
                    <td class="ct-right-td">
                        <!-- Создавать свои формы под реакции -->
                        <input type="submit" style="width: 60px; padding: 0" value="As Read" form="addChannelForm" />
                        <input type="submit" style="width: 70px; padding: 0" value="As Unread" form="showFeedsForm" />
                        <input type="submit" style="width: 60px; padding: 0" value="Delete" form="deleteChannelForm" />
                    </td>
                </tr>
            </table>
        </div>
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