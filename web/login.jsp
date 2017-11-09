<%--
  Login server page

  User: Sergey "Manual Brakes" Sokhnyshev
  Date: 01.11.17
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html>
  <head>
    <meta charset="utf-8">
    <title>Simple RSS Reader</title>

    <link rel="icon" href="resources/images/rss-reader-32x32.png" sizes="32x32">
  </head>
  <body>
  <h1>Login</h1>

  <form action="login" method="POST">
    <table frame="box">
      <tr>
        <td><b>Login</b></td>
        <td>
          <input type="text" name="login" value='${requestScope["login"]}' />
        </td>
      </tr>

      <tr>
        <td><b>Password</b></td>
        <td>
          <input type="password" name="password" value='${requestScope["password"]}' />
        </td>
      </tr>
    </table>

    <br>
    <table>
      <tr>
        <td><input type="submit" value="Login" /></td>
        <td><a href="sign-up.jsp">SignUp</a><td>
      </tr>
    </table>

  </form>

  <%--@elvariable id="errorMessage" type="java.lang.String"--%>
  <%--@elvariable id="messageColor" type="java.lang.String"--%>
  <c:if test="${not empty errorMessage}">
    <p style="color:${messageColor}; font-weight:bold"><jsp:text>
      ${errorMessage}</jsp:text></p>
  </c:if>

  </body>
</html>
