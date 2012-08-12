<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
  <head><title>Calendars</title></head>
  <body>
   	<h2>Your Calendars</h2>
    <c:forEach var="entry" items="${entries}" varStatus="status">
		<div>${entry.description}</div>
	</c:forEach>
  </body>
</html>