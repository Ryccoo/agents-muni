<%@page contentType="text/html;charset=utf-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<body>

<table border="1">
    <thead>
    <tr>
        <th>ID</th>
        <th>Name</th>
        <th>Description</th>
        <th>Destination</th>
        <th>Secret</th>
    </tr>
    </thead>
    <c:forEach items="${missions}" var="mission">
        <tr>
            <td><c:out value="${mission.id}"/></td>
            <td><c:out value="${mission.name}"/></td>
            <td><c:out value="${mission.description}"/></td>
            <td><c:out value="${mission.destination}"/></td>
            <td><c:out value="${mission.secret}"/></td>
            <td><form method="post" action="${pageContext.request.contextPath}/missions/delete?id=${mission.id}"
                      style="margin-bottom: 0;"><input type="submit" value="Smazat"></form></td>
        </tr>
    </c:forEach>
</table>

<h2>Add Mission</h2>
<c:if test="${not empty chyba}">
    <div style="border: solid 1px red; background-color: yellow; padding: 10px">
        <c:out value="${chyba}"/>
    </div>
</c:if>
<form action="${pageContext.request.contextPath}/missions/add" method="post">
    <table>
        <tr>
            <th>Mission Name:</th>
            <td><input type="text" name="name" value="<c:out value='${param.name}'/>"/></td>
        </tr>
        <tr>
            <th>Description:</th>
            <td><input type="text" name="description" value="<c:out value='${param.description}'/>"/></td>
        </tr>
        <tr>
            <th>Destination:</th>
            <td><input type="text" name="destination" value="<c:out value='${param.destination}'/>"/></td>
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