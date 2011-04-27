<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page session="false" %>
<html>
<head>
	<title>Scheduler</title>
</head>
<body>
<h1>
	List of Next ${ maxCount } FireTimes for Trigger ${ triggerGroup }.${ triggerName }
</h1>
<table>
	<c:forEach items="${ fireTimes }" var="time" varStatus="status">
	<tr>
		<td> ${ status.index + 1 }</td>
		<td> ${ time }</td>
	</tr>
	</c:forEach>
</table>
</body>
</html>