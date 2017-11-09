<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html>
<head>
    <meta charset="utf-8">
    <title>Simple RSS Reader</title>

    <link rel="icon" href="resources/images/rss-reader-32x32.png" sizes="32x32">
</head>
<body>
<h1>Channels list</h1>
<%--@elvariable id="userName" type="java.lang.String"--%>
<%--@elvariable id="userLogin" type="java.lang.String"--%>
<c:choose>
    <c:when test="${empty userLogin}">
        <p style="color:red; font-weight:bold">There is no logged in users</p>
    </c:when>

    <c:when test="${not empty userName && not empty userLogin}">
        <form action="/logout" method="GET">
            <p style="color:blue; font-weight:bold">Current user:
                <jsp:text>${userName} @${userLogin}</jsp:text>
                <input type="submit" value="Logout" />
            </p>
        </form>
    </c:when>
</c:choose>
<table>
    <tr>
        <!-- Control elements under RSS channels -->
        <td>
            <form action="add-channel" method="POST">
                <table frame="box">
                    <tr>
                        <td><b>RSS channel link</b></td>
                        <td>
                            <input title="Channel link" type="text" name="channel" value='${requestScope["username"]}' />
                        </td>

                        <td><input type="submit" value="Add" /></td>
                    </tr>
                </table>
            </form>
        </td>

        <td>&nbsp;&nbsp;&nbsp;&nbsp;</td>

        <td>
            <table frame="box">
                <tr>
                    <td><b>RSS Channels</b></td>
                </tr>
            </table>
        </td>
    </tr>
</table>

</body>
</html>
