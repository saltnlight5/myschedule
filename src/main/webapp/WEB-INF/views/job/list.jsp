<%@ include file="/WEB-INF/views/page-a.inc" %>
<%@ include file="/WEB-INF/views/menu.inc" %>
<%@ include file="/WEB-INF/views/job/submenu.inc" %>
<script>
$(document).ready(function() {
	// Use dataTables plugin
	$("#jobs").dataTable({
		"aaSorting": [[ 5, "desc" ], [0, "asc"]],
		"iDisplayLength": 50,
		"bJQueryUI": true,
		"sPaginationType": "full_numbers"
	});
	
	// Confirm unschedule job
	$("#unschedule-confirm").hide();
	$("#jobs .action a + a").click(function() {
		var linkUrl = $(this).attr("href");
		$("#unschedule-confirm").dialog({
			resizable: false,
			height:200,
			width:400,
			modal: true,
			buttons: {
				"Yes": function() {
					window.location.href=linkUrl;
				},
				Cancel: function() {
					$(this).dialog("close");
				}
			}
		});
		return false;
	});
});
</script>

<div id="unschedule-confirm" title="Unschedule Trigger?">
	<p><span class="ui-icon ui-icon-alert" style="float:left; margin:0 7px 20px 0;"></span>
	Are you sure you want to remove this trigger from the scheduler?
	</p>
</div>

<h1>Jobs with assigned trigger</h1>
<c:choose><c:when test="${ data.schedulerService.scheduler.inStandbyMode }">
	<div class="warning">The current scheduler is in standby mode! No jobs will be running yet.</div>
</c:when></c:choose>

<table id="jobs" class="display">
	<thead>
	<tr>
		<th> JOB </th>
		<th> TRIGGER </th>
		<th> SCHEDULE </th>
		<th> START TIME </th>
		<th> END TIME </th>
		<th> NEXT FIRE TIME </th>
		<th> CALENDER </th>
		<th> ACTIONS </th>
	</tr>
	</thead>
	
	<tbody>
	<c:forEach items="${ data.triggers }" var="trigger" varStatus="loop">
	<tr>
		<td><a href="${ mainPath }/job/job-detail?jobName=${ trigger.jobKey.name }&jobGroup=${ trigger.jobKey.group }">${ trigger.jobKey }</a></td>
		<td><a href="${ mainPath }/job/trigger-detail?triggerName=${ trigger.key.name }&triggerGroup=${ trigger.key.group }&fireTimesCount=${ data.showMaxFireTimesCount }">${ trigger.key }</a></td>
		<td>${ data.triggerSchedules[loop.index] }</td>
		<td><fmt:formatDate value="${ trigger.startTime }" pattern="${ data.datePattern }"/></td>
		<td><fmt:formatDate value="${ trigger.endTime }" pattern="${ data.datePattern }"/></td>
		<td><fmt:formatDate value="${ trigger.nextFireTime }" pattern="${ data.datePattern }"/></td>
		<td>${ trigger.calendarName }<%--<a href="${ mainPath }/job/calendar?name=${ trigger.calendarName }">${ trigger.calendarName }</a>--%></td>
		<td class="action">
			<a href="${ mainPath }/job/run-job?jobName=${ trigger.jobKey.name }&jobGroup=${ trigger.jobKey.group }">Run It Now</a> |
			<a href="${ mainPath }/job/unschedule?triggerName=${ trigger.key.name }&triggerGroup=${ trigger.key.group }">Unschedule</a>
		</td>
	</tr>
	</c:forEach>
	</tbody>
</table> <!-- jobs-with-triggers -->
<%@ include file="/WEB-INF/views/page-b.inc" %>