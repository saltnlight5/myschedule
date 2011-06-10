<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ include file="/WEB-INF/views/header.inc" %>
<%@ include file="/WEB-INF/views/job/submenu.inc" %>

<h1>Scheduled Jobs</h1>

<p class="info">${ fn:length(data.jobs) } jobs found.</p>

<table class="simple">
	<tr>
		<td> JOB NAME </td>
		<td> TRIGGER NAME </td>
		<td> TRIGGER NEXT FIRE TIME </td>
		<td> TRIGGER EXTRA INFO </td>
		<td> ACTIONS </td>
	</tr>
	<c:forEach items="${ data.jobs }" var="job">
	<tr>
		<c:choose>
		<c:when test="${ not empty job.trigger }">
		<td> <a href="${ mainPath }/job/job-detail?jobName=${ job.jobDetail.name }&jobGroup=${ job.jobDetail.group }">${ job.jobDetail.fullName }</a> </td>
		<td> <a href="${ mainPath }/job/trigger-detail?triggerName=${ job.trigger.name }&triggerGroup=${ job.trigger.group }&fireTimesCount=20">${ job.trigger.fullName }</a> </td>
		<td> ${ job.trigger.nextFireTime } </td>
		<td> ${ job.triggerInfo } </td>
		<td>
			<a href="${ mainPath }/job/unschedule?triggerName=${ job.trigger.name }&triggerGroup=${ job.trigger.group }">Unschedule</a>
		</td>
		</c:when>
		<c:otherwise>
		<td> <a href="${ mainPath }/job/job-detail?jobName=${ job.jobDetail.name }&jobGroup=${ job.jobDetail.group }">${ job.jobDetail.fullName }</a> </td>
		<td> NO TRIGGER ASSIGNED. </td>
		<td> </td>
		<td> </td>
		<td>
			<a href="${ mainPath }/job/delete?jobName=${ job.jobDetail.name }&jobGroup=${ job.jobDetail.group }">Delete</a>
		</td>
		</c:otherwise>
		</c:choose>
	</tr>
	</c:forEach>
</table>

<%@ include file="/WEB-INF/views/footer.inc" %>