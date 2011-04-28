<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page session="false" %>
<html>
<head>
	<title>Scheduler</title>
</head>
<body>
<div>
<a href="list-scheduled-jobs">List of scheduled jobs</a>
<a href="create-trigger-form">Create Jobs</a>
</div>
<h1>
	Scheduler Information
</h1>
<table>
	<c:forEach items="${ schedulerMap }" var="item" varStatus="status">
	<tr>
		<td> ${ item.key }</td>
		<td> ${ item.value }</td>
	</tr>
	</c:forEach>
</table>
</body>
</html>