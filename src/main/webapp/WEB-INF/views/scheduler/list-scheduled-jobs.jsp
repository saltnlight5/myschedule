<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page session="false" %>
<html>
<head>
<style type="text/css">
table.sample {
	border-width: 1px;
	border-spacing: 2px;
	border-style: outset;
	border-color: gray;
	border-collapse: collapse;
	background-color: white;
}
table.sample th {
	border-width: 1px;
	padding: 1px;
	border-style: inset;
	border-color: gray;
	background-color: white;
	-moz-border-radius: ;
}
table.sample td {
	vertical-align: top;
	border-width: 1px;
	padding: 1px;
	border-style: inset;
	border-color: gray;
	background-color: white;
	-moz-border-radius: ;
}
</style>
<title>Scheduler</title>
</head>
<body>
<div>
<a href="show">Scheduler Info</a>
</div>
<h1>
	List of Scheduled Jobs
</h1>
<table class="sample">
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