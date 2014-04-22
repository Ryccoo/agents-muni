<%@page contentType="text/html;charset=utf-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<body>

<table border="1">
    <thead>
    <tr>
        <th>ID</th>
        <th>Name</th>
        <th>Rank</th>
        <th>Secret</th>
    </tr>
    </thead>
    <c:forEach items="${agents}" var="agent">
        <tr>
            <td><c:out value="${agent.id}"/></td>
            <td><c:out value="${agent.name}"/></td>
            <td><c:out value="${agent.rank}"/></td>
            <td><c:out value="${agent.secret}"/></td>
            <td><form method="post" action="${pageContext.request.contextPath}/agents/delete?id=${agent.id}"
                      style="margin-bottom: 0;"><input type="submit" value="Smazat"></form></td>
        </tr>
    </c:forEach>
</table>

<h2>Add Agent</h2>
<c:if test="${not empty chyba}">
    <div style="border: solid 1px red; background-color: yellow; padding: 10px">
        <c:out value="${chyba}"/>
    </div>
</c:if>
<form action="${pageContext.request.contextPath}/agents/add" method="post">
    <table>
        <tr>
            <th>Name:</th>
            <td><input type="text" name="name" value="<c:out value='${param.name}'/>"/></td>
        </tr>
        <tr>
            <th>Rank:</th>
            <td><input type="text" name="rank" value="<c:out value='${param.rank}'/>"/></td>
        </tr>
        <tr>
            <th>Secret:</th>
            <td><input type="checkbox" name="secret" value="1" <% if(request.getParameter("secret") != null) { %> checked <% } %>/></td>
        </tr>
    </table>
    <input type="Submit" value="Add" />
</form>

</body>
</html>