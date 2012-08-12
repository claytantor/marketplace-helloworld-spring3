<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
  <head><title>Hello</title></head>
  <body>
    <h1>Hello <c:out value="${user.username}"/></h1>
    
    <a href="<c:url value="/user/calendar"/>">calendar</a>

   <%--
   		<h2>Current or Next Calendar Event:</h2>
    <c:choose>
      <c:when test="${nextEvent != null}">
        <div>
            Title: <c:out value="${nextEvent.title.plainText}"/><br/>
            When: <c:out value="${nextEvent.times[0].startTime}"/>" - <c:out value="${nextEvent.times[0].endTime}"/><br/>
            Where: <c:out value="${nextEvent.locations[0].valueString}"/><br/>
            Description: <c:out value="${nextEvent.plainTextContent}"/><br/>
      </c:when>
      <c:otherwise>
          You have no upcoming calendar events.
      </c:otherwise>
    </c:choose>
   
    --%> 

  </body>
</html>