<%-- 
    Document   : BrowseTable
    Created on : Jan 21, 2019, 8:27:04 PM
    Author     : Owner
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<%@ page import = "JavaClasses.DBBean" %>
<jsp:useBean id = "dBBeanId" scope = "session"
             class = "JavaClasses.DBBean" >
</jsp:useBean>
<%@ page import = "java.sql.*" %>
<html>
    <head>
        <title>BrowseTable</title>
    </head>
    <body>

        <% String tableName = request.getParameter("tablename");

            ResultSet rsColumns = dBBeanId.getConnection().getMetaData().
                    getColumns(null, null, tableName, null);
        %>
        <table border = "1">
            <tr>
                <% // Add column names to the table
     while (rsColumns.next()) {%>
                <td><%= rsColumns.getString("COLUMN_NAME")%></td>
                <%}%>
            </tr>

            <% Statement statement
                        = dBBeanId.getConnection().createStatement();
                ResultSet rs = statement.executeQuery(
                        "select * from " + tableName);

                // Get column count
                int columnCount = rs.getMetaData().getColumnCount();

                // Store rows to rowData
                while (rs.next()) {
                    out.println("<tr>");
         for (int i = 0; i < columnCount; i++) {%>
            <td><%= rs.getObject(i + 1)%></td>
            <% }
         out.println("</tr>");
     }%>
        </table>
    </body>
</html>