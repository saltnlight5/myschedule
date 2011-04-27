<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page session="false" %>
<html>
<head>
	<title>Scheduler</title>
</head>
<body>
<h1>
	List of Scheduled Jobs
</h1>
<table>
	<tr>
		<td> GROUP.TRIGGER_NAME </td>
		<td> NEXT FIRE TIME </td>
		<td> More ...</td>
	</tr>
	<c:forEach items="${ scheduledJobs }" var="job">
	<tr>
		<td> ${ job.triggerGroup }.${ job.triggerName }</td>
		<td> ${ job.nextFireTime }</td>
		<td><a href="list-firetimes/${ job.triggerName }/${ job.triggerGroup }/20">More Next FireTime</a></td>
	</tr>
	</c:forEach>
</table>
</body>
</html>