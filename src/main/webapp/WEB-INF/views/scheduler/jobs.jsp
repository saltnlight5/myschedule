<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ include file="/WEB-INF/views/header.inc" %>

<h1>Scheduled Jobs</h1>

<table class="simple">
	<tr>
		<td> JOB_NAME </td>
		<td> TRIGGER_NAME </td>
		<td> NEXT FIRE TIME </td>
		<td> More ...</td>
	</tr>
	<c:forEach items="${ data.jobs }" var="job">
	<tr>
		<c:choose>
		<c:when test="${ not empty job.trigger }">
		<td> ${ job.jobDetail.fullName } </td>
		<td> ${ job.trigger.fullName } </td>
		<td> ${ job.trigger.nextFireTime } </td>
		<td><a href="job-firetimes?triggerName=${ job.trigger.name }&triggerGroup=${ job.trigger.group }&nextFireTimesRequested=20">More Next FireTime</a></td>
		</c:when>
		<c:otherwise>
		<td> ${ job.jobDetail.fullName } </td>
		<td> No Trigger Assigned.</td>
		<td> N/A </td>
		<td> N/A </td>
		</c:otherwise>
		</c:choose>
	</tr>
	</c:forEach>
</table>

<%@ include file="/WEB-INF/views/footer.inc" %>