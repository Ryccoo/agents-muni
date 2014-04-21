<%--
  Created by IntelliJ IDEA.
  User: richard
  Date: 22.4.2014
  Time: 0:44
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>

<%! //kód mimo metodu service()
    public String prictiJedna(String a) {
        return Integer.toString(Integer.parseInt(a)+1);
    }
%>
<html>
    <body>
        <%  //kód uvnitř metody service()
            if(request.getMethod().equals("GET")) {
        %>
        <form method="post">
            Zadejte číslo: <input name="cislo" value="">
            <input type="submit" >
        </form>
        <%
        } else if (request.getMethod().equals("POST")) {
            String cislo = request.getParameter("cislo");
            String plusjedna = prictiJedna(cislo);
        %>
        Výsledek <%out.println(cislo);%> + 1 je <%=plusjedna%>
        <%
            }
        %>
    </body>
</html>