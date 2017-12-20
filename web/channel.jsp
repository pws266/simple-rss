<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<html>
<head>
    <meta charset="utf-8">
    <title>Simple RSS Reader</title>

    <link rel="icon" href="resources/images/rss-reader-32x32.png" sizes="32x32">
    <link href="resources/css/channel-style.css" rel="stylesheet" type="text/css">

    <script type="text/javascript" src="resources/js/jquery-1.8.3.js"></script>

    <script type='text/javascript'>//<![CDATA[
    $(window).load(function(){
        $("#rssList tr").click(function(){
            $(this).addClass('selected').siblings().removeClass('selected');
            var value=$(this).find('td:first').html();
            //alert(value);
            $("#rssList tr.selected td:first").html()
        });
/*
        $('.ok').on('click', function(e){
            alert($("#rssList tr.selected td:first").html());
        });
*/
    });//]]>

    </script>
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
        <form action="show-channel" method="POST" id="showChannelForm"></form>
        <form action="delete-channel" method="POST" id="deleteChannelForm"></form>

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
                <td align="right"><input type="submit" style="width: 60px" value="Show" form="showChannelForm" /></td>
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
                        </tr>
                    </c:forEach>


            </table>
        </div>
        <div id="content-wrap">
            <div id="info-wrap">
                <div class="info">small info </div>
                <div class="info">small info</div>
            </div>
            Content
        </div>
    </div>
    <div id="footer">Footer</div>
</body>
</html>