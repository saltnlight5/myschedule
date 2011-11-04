<%@ include file="/WEB-INF/jsp/main/page-a.inc" %>
<%@ include file="/WEB-INF/jsp/main/menu.inc" %>
<%@ include file="/WEB-INF/jsp/main/job/submenu.inc" %>
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
	$("#unschedule-trigger-confirm").hide();
	$(".unschedule-trigger").click(function() {
		var linkUrl = $(this).attr("href");
		$("#unschedule-trigger-confirm").dialog({
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
	
	// Confirm pause job
	$("#pause-trigger-confirm").hide();
	$(".pause-trigger").click(function() {
		var linkUrl = $(this).attr("href");
		$("#pause-trigger-confirm").dialog({
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
	
	// Confirm resume job
	$("#resume-trigger-confirm").hide();
	$(".resume-trigger").click(function() {
		var linkUrl = $(this).attr("href");
		$("#resume-trigger-confirm").dialog({
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

<div id="unschedule-trigger-confirm" title="Unschedule Trigger?">
	<p><span class="ui-icon ui-icon-alert" style="float:left; margin:0 7px 20px 0;"></span>
	Are you sure you want to remove this trigger from the scheduler?
	</p>
</div>
<div id="pause-trigger-confirm" title="Pause Trigger?">
	<p><span class="ui-icon ui-icon-alert" style="float:left; margin:0 7px 20px 0;"></span>
	Are you sure you want to pause this trigger?
	</p>
</div>
<div id="resume-trigger-confirm" title="Resume Trigger?">
	<p><span class="ui-icon ui-icon-alert" style="float:left; margin:0 7px 20px 0;"></span>
	Are you sure you want to resume this trigger?
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
		<th> NEXT FIRE TIME </th>
		<th> ACTIONS </th>
	</tr>
	</thead>
	
	<tbody>
	<c:set var="scheduler" value="${ data.schedulerService.scheduler }" scope="request"/>
	<c:forEach items="${ data.triggers }" var="trigger" varStatus="loop">
	<c:set var="trigger" value="${ trigger }" scope="request"/>
	<tr>
		<td><a href="${ mainPath }/job/job-detail?jobName=${ trigger.jobName }&jobGroup=${ trigger.jobGroup }">${ trigger.jobName }.${ trigger.jobGroup }</a></td>
		<td><a href="${ mainPath }/job/trigger-detail?triggerName=${ trigger.name }&triggerGroup=${ trigger.group }&fireTimesCount=${ data.showMaxFireTimesCount }">${ trigger.name }.${ trigger.group }</a></td>
		<td>${ data.triggerSchedules[loop.index] }</td>
		<td><fmt:formatDate value="${ trigger.nextFireTime }" pattern="${ data.datePattern }"/></td>
		<td class="action">
			<a href="${ mainPath }/job/run-job?jobName=${ trigger.jobName }&jobGroup=${ trigger.jobGroup }">Run It Now</a> |
			
			<% if (myschedule.service.QuartzUtils.isTriggerPaused((org.quartz.Trigger)request.getAttribute("trigger"), (myschedule.quartz.extra.SchedulerTemplate)request.getAttribute("scheduler"))) { %>	
				<a class="resume-trigger" style="color: red;" href="${ mainPath }/job/resumeTrigger?triggerName=${ trigger.name }&triggerGroup=${ trigger.group }">Resume</a>
			<% } else { %>	
				<a class="pause-trigger" href="${ mainPath }/job/pauseTrigger?triggerName=${ trigger.name }&triggerGroup=${ trigger.group }">Pause</a>
			<% } %> |
			
			<a class="unschedule-trigger" href="${ mainPath }/job/unschedule?triggerName=${ trigger.name }&triggerGroup=${ trigger.group }">Unschedule</a>
		</td>
	</tr>
	</c:forEach>
	</tbody>
</table> <!-- jobs-with-triggers -->
<%@ include file="/WEB-INF/jsp/main/page-b.inc" %>