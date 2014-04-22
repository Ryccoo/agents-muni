<%@page contentType="text/html;charset=utf-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<body>

<h2>Edit Agent</h2>
<c:if test="${not empty chyba}">
    <div style="border: solid 1px red; background-color: yellow; padding: 10px">
        <c:out value="${chyba}"/>
    </div>
</c:if>
<form action="${pageContext.request.contextPath}/agents/update" method="post">
    <input type="hidden" name="id" value="<c:out value='${agent.id}'/>"/>
    <table>
        <tr>
            <th>Name:</th>
            <td><input type="text" name="name" value="<c:out value='${agent.name}'/>"/></td>
        </tr>
        <tr>
            <th>Rank:</th>
            <td><input type="text" name="rank" value="<c:out value='${agent.rank}'/>"/></td>
        </tr>
        <tr>
            <th>Secret:</th>
            <td><input type="checkbox" name="secret" value="1" <c:out value="${agent.secret ? 'checked' : ''}"/>/></td>
        </tr>
    </table>
    <input type="Submit" value="Update" />
</form>

</body>
</html>