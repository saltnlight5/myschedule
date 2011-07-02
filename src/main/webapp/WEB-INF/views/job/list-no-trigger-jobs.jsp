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
<h1>Jobs without assigned trigger</h1>
<table id="jobs" class="display">
	<thead>
	<tr>
		<th> JOB </th>
		<th> JOB CLASS </th>
		<th> ACTIONS </th>
	</tr>
	</thead>
	
	<tbody>
	<c:forEach items="${ data.noTriggerJobDetails }" var="jobDetail">
	<tr>
		<td><a href="${ mainPath }/job/job-detail?jobName=${ jobDetail.name }&jobGroup=${ jobDetail.group }">${ jobDetail.fullName }</a></td>
		<td>${ jobDetail.jobClass.name }</td>
		<td>
			<a href="${ mainPath }/job/delete?jobName=${ jobDetail.name }&jobGroup=${ jobDetail.group }">Delete</a>
		</td>
	</tr>
	</c:forEach>
	</tbody>
</table> <!-- jobs-without-triggers -->
<%@ include file="/WEB-INF/views/page-b.inc" %>