<%@page contentType="text/html;charset=utf-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<body>

<h2>Edit Mission</h2>
<c:if test="${not empty chyba}">
    <div style="border: solid 1px red; background-color: yellow; padding: 10px">
        <c:out value="${chyba}"/>
    </div>
</c:if>
<form action="${pageContext.request.contextPath}/missions/update" method="post">
    <input type="hidden" name="id" value="<c:out value='${mission.id}'/>"/>
    <table>
        <tr>
            <th>Mission Name:</th>
            <td><input type="text" name="name" value="<c:out value='${mission.name}'/>"/></td>
        </tr>
        <tr>
            <th>Description:</th>
            <td><input type="text" name="description" value="<c:out value='${mission.description}'/>"/></td>
        </tr>
        <tr>
            <th>Destination:</th>
            <td><input type="text" name="destination" value="<c:out value='${mission.destination}'/>"/></td>
        </tr>
        <tr>
            <th>Secret:</th>
            <td><input type="checkbox" name="secret" value="1" <c:out value="${mission.secret ? 'checked' : ''}"/>/></td>
        </tr>
    </table>
    <input type="Submit" value="Add" />
</form>

</body>
</html>