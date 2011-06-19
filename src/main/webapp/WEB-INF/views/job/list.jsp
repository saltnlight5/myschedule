<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ include file="/WEB-INF/views/header.inc" %>
<%@ include file="/WEB-INF/views/job/submenu.inc" %>
<div class="content">

<h1>Jobs List</h1>

<div class="center">Total ${ fn:length(data.triggers) + fn:length(data.noTriggerJobDetails) } jobs found.</div>

<c:if test="${ not empty data.triggers }">
<table class="center">
<tr><td>
<div class="description">Jobs list with associated trigger.</div>
<table class="outlined">
	<tr>
		<td> JOB NAME.GROUP </td>
		<td> TRIGGER NAME.GROUP </td>
		<td> SCHEDULE </td>
		<td> NEXT FIRE TIME </td>
		<td> START TIME </td>
		<td> END TIME </td>
		<td> ACTIONS </td>
	</tr>
	<c:forEach items="${ data.triggers }" var="trigger" varStatus="status">
	<tr>
		<td><a href="${ mainPath }/job/job-detail?jobName=${ trigger.jobName }&jobGroup=${ trigger.jobGroup }">${ trigger.fullJobName }</a></td>
		<td><a href="${ mainPath }/job/trigger-detail?triggerName=${ trigger.name }&triggerGroup=${ trigger.group }&fireTimesCount=${ data.showMaxFireTimesCount }">${ trigger.fullName }</a></td>
		<td> ${ data.triggerSchedules[status.index] } </td>
		<td> ${ trigger.nextFireTime } </td>
		<td> ${ trigger.startTime } </td>
		<td> ${ trigger.endTime } </td>
		<td>
			<a href="${ mainPath }/job/unschedule?triggerName=${ trigger.name }&triggerGroup=${ trigger.group }">Unschedule</a>
		</td>
	</tr>
	</c:forEach>
</table>

</td></tr>
</table><!-- table.center -->
</c:if> <!--  data.triggers -->

<!-- Empty row for separator. -->
<div class="gapseparator"></div>

<c:if test="${ not empty data.noTriggerJobDetails }">

<table class="center">
<tr><td>

<div class="description">Jobs list without triggers.</div>
<table class="outlined">
	<tr>
		<td> JOB NAME.GROUP </td>
		<td> JOB CLASS </td>
		<td> ACTIONS </td>
	</tr>
	<c:forEach items="${ data.noTriggerJobDetails }" var="jobDetail">
	<tr>
		<td><a href="${ mainPath }/job/job-detail?jobName=${ jobDetail.name }&jobGroup=${ jobDetail.group }">${ jobDetail.fullName }</a></td>
		<td>${ jobDetail.jobClass.name }</td>
		<td>
			<a href="${ mainPath }/job/delete?jobName=${ jobDetail.name }&jobGroup=${ jobDetail.group }">Delete</a>
		</td>
	</tr>
	</c:forEach>
</table>

</td></tr>
</table><!-- table.center -->
</c:if> <!--  data.noTriggerJobDetails -->

</div><!-- div.content -->
<%@ include file="/WEB-INF/views/footer.inc" %>