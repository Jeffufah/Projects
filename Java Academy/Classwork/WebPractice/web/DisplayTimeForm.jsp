<%-- 
    Document   : DisplayTimeForm
    Created on : Jan 21, 2019, 6:25:11 PM
    Author     : Owner
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
 <%@ pageimport = "JavaClasses.TimeBean" %>
 <jsp:useBean 
    id = "timeBeanId"
    class = "JavaClasses.TimeBean" scope = "application" >
 </jsp:useBean>

 <html>
     <head>
         <title>
             Display Time Form
         </title>
     </head>
     <body>
         <h3>Choose locale and time zone</h3>
         <form method = "post" action = "DisplayTime.jsp">
             Locale <select size = "1" name = "localeIndex">
                 <% for (int i = 0; i < timeBeanId.getAllLocale().length; i++) {%>
                 <option value = "<%= i%>">
                     <%= timeBeanId.getAllLocale()[i]%>
                 </option>
                 <%}%>
             </select><br />
             Time Zone <select size = "1" name = "timeZoneIndex">
                 <% for (int i = 0; i < timeBeanId.getAllTimeZone().length; i++) {%>
                 <option value = "<%= i%>">
                     <%= timeBeanId.getAllTimeZone()[i]%>
                 </option>
                 <%}%>
             </select><br />
             <input type = "submit" name = "Submit"
                    value = "Get Time" />
             <input type = "reset" value = "Reset" />
         </form>
     </body>
 </html>