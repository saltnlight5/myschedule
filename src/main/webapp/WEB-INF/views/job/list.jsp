<%@ include file="/WEB-INF/views/page-a.inc" %>
<%@ include file="/WEB-INF/views/menu.inc" %>
<%@ include file="/WEB-INF/views/job/submenu.inc" %>
<script>
$(document).ready(function() {
	// use dataTables plugin
	$("#jobs").dataTable({
		"aaSorting": [[ 5, "desc" ], [0, "asc"]],
		"iDisplayLength": 50,
		"bJQueryUI": true,
		"sPaginationType": "full_numbers"
	});
});
</script>
<h1>Jobs with assigned trigger</h1>
<table id="jobs" class="display">
	<thead>
	<tr>
		<th> JOB </th>
		<th> TRIGGER </th>
		<th> SCHEDULE </th>
		<th> START TIME </th>
		<th> END TIME </th>
		<th> NEXT FIRE TIME </th>
		<th> ACTIONS </th>
	</tr>
	</thead>
	
	<tbody>
	<c:forEach items="${ data.triggers }" var="trigger" varStatus="loop">
	<tr>
		<td><a href="${ mainPath }/job/job-detail?jobName=${ trigger.jobName }&jobGroup=${ trigger.jobGroup }">${ trigger.jobName }</a></td>
		<td><a href="${ mainPath }/job/trigger-detail?triggerName=${ trigger.name }&triggerGroup=${ trigger.group }&fireTimesCount=${ data.showMaxFireTimesCount }">${ trigger.name }</a></td>
		<td>${ data.triggerSchedules[loop.index] }</td>
		<td><fmt:formatDate value="${ trigger.startTime }" pattern="${ data.datePattern }"/></td>
		<td><fmt:formatDate value="${ trigger.endTime }" pattern="${ data.datePattern }"/></td>
		<td><fmt:formatDate value="${ trigger.nextFireTime }" pattern="${ data.datePattern }"/></td>
		<td>
			<a href="${ mainPath }/job/unschedule?triggerName=${ trigger.name }&triggerGroup=${ trigger.group }">Unschedule</a>
		</td>
	</tr>
	</c:forEach>
	</tbody>
</table> <!-- jobs-with-triggers -->
<%@ include file="/WEB-INF/views/page-b.inc" %>