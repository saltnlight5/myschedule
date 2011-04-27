<%-- Not used. See list-scheduled-jobs instead. --%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page session="false" %>
<html>
<head>
	<title>Scheduler</title>
</head>
<body>
<h1>
	List of Triggers
</h1>
<table>
	<tr>
		<td> GROUP.TRIGGER_NAME </td>
	</tr>
	<c:forEach items="${ triggerNames }" var="trigger">
	<tr>
		<td> ${ trigger[1] }.${ trigger[0] }</td>
	</tr>
	</c:forEach>
</table>
</body>
</html>