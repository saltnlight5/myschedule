<%@ include file="/WEB-INF/views/page-a.inc" %>
<%@ include file="/WEB-INF/views/menu.inc" %>
<%@ include file="/WEB-INF/views/job/submenu.inc" %>
<script>
$(document).ready(function() {
	// use dataTables plugin
	$("#triggers-datatable").dataTable({
		"aaSorting": [[ 3, "desc" ], [0, "asc"], [1, "asc"]],
		"iDisplayLength": 50,
		"bJQueryUI": true,
		"sPaginationType": "full_numbers"
	});
});
</script>

<div id="page-container">
<h1>Jobs with assigned trigger</h1>
<table id="triggers-datatable" cellpadding="0" cellspacing="0" border="0" class="display">
	<thead>
	<tr>
		<td> JOB </td>
		<td> TRIGGER </td>
		<td> SCHEDULE </td>
		<td> START TIME </td>
		<td> END TIME </td>
		<td> NEXT FIRE TIME </td>
		<td> ACTIONS </td>
	</tr>
	</thead>
	
	<tbody>
	<c:forEach items="${ data.triggers }" var="trigger" varStatus="status">
	<tr>
		<td><a href="${ mainPath }/job/job-detail?jobName=${ trigger.jobName }&jobGroup=${ trigger.jobGroup }">${ trigger.jobName }</a></td>
		<td><a href="${ mainPath }/job/trigger-detail?triggerName=${ trigger.name }&triggerGroup=${ trigger.group }&fireTimesCount=${ data.showMaxFireTimesCount }">${ trigger.name }</a></td>
		<td>${ data.triggerSchedules[status.index] }</td>
		<td><fmt:formatDate value="${ trigger.startTime }" pattern="${ data.datePattern }"/></td>
		<td><fmt:formatDate value="${ trigger.endTime }" pattern="${ data.datePattern }"/></td>
		<td><fmt:formatDate value="${ trigger.nextFireTime }" pattern="${ data.datePattern }"/></td>
		<td><a href="${ mainPath }/job/unschedule?triggerName=${ trigger.name }&triggerGroup=${ trigger.group }">Unschedule</a></td>
	</tr>
	</c:forEach>
	</tbody>
</table> <!-- triggersTable-datatable -->

</div> <!-- page-container -->
<%@ include file="/WEB-INF/views/page-b.inc" %>