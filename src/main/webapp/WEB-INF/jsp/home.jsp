<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
  <head>
  	<title>Hello</title>
  	<script type="text/javascript">
  	function spreadsheet(){
  		oForm = document.forms[0];
  	  	window.location = '<c:url value="/user/spreadsheet"/>'+'?key='+oForm.elements["key"].value;
  	} 	
  	</script>
  </head>
  <body>
    <h1>Hello <c:out value="${user.username}"/></h1>
    <form action="/readSpreadsheet.html">
    	<input type="text" name="key" id="key" value="0Avb_MOw4lfVndHBqelNCMlFza1l4WEFHUFB4bzhWM1E" size="60" style="width:600px;"></input>
  	</form> 
  	<div><a href="javascript:spreadsheet(); return false;">view spreadsheet feed</a></div>
   </body>
</html>