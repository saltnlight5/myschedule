<%@ include file="/WEB-INF/views/page-a.inc" %>
<%@ include file="/WEB-INF/views/menu.inc" %>
<%@ include file="/WEB-INF/views/job/submenu.inc" %>
<script>
$(document).ready(function() {
	// use dataTables plugin
	$("#jobs").dataTable({
		"aaSorting": [[0, "asc"]],
		"iDisplayLength": 50,
		"bJQueryUI": true,
		"sPaginationType": "full_numbers"
	});
});
</script>
<h1>Jobs currently executing by scheduler</h1>
<table id="jobs" class="display">
	<thead>
	<tr>
		<th> JOB </th>
		<th> TRIGGER </th>
		<th> RECOVERING </th>
		<th> SCHEDULE TIME </th>
		<th> FIRE TIME </th>
		<th> PREV FIRE TIME </th>
		<th> NEXT FIRE TIME </th>
	</tr>
	</thead>
	
	<tbody>
	<c:forEach items="${ data.jobExecutionContextList }" var="job" varStatus="loop">
	<tr>
		<td>${ job.jobDetail.fullName  }</td>
		<td>${ job.trigger.fullName  }</td>
		<td>${ job.recovering  }</td>
		<td>${ job.scheduledTime  }</td>
		<td>${ job.fireTime  }</td>
		<td>${ job.prevFireTime  }</td>
		<td>${ job.nextFireTime  }</td>
	</tr>
	</c:forEach>
	</tbody>
</table> <!-- jobs -->
<%@ include file="/WEB-INF/views/page-b.inc" %>