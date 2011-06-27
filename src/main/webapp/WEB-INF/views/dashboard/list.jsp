<%@ include file="/WEB-INF/views/page-a.inc" %>
<%@ include file="/WEB-INF/views/menu.inc" %>
<script>
$(document).ready(function() {
	// use dataTables plugin
	$("#scheduler-list").dataTable({		
		"bPaginate": false,
		"bLengthChange": false,
		"bFilter": false,
		"bSort": false,
		"bInfo": false,
		"bAutoWidth": false
	});
});
</script>
<h1>List of All Schedulers</h1>
<table id="scheduler-list">
	<thead>
		<tr>
			<td>SCHEDULER NAME</td>
			<td>RUNNING</td>
			<td>JOB STORAGE TYPE</td>
		</tr>
	</thead>
	<tbody>
	<c:forEach items="${ data.schedulerStatusList }" var="schedulerStatus" varStatus="loop">
	<tr>
		<td><a href="${ mainPath }/dashboard/switch-scheduler?name=${ schedulerStatus.name }">${ schedulerStatus.name }</a></td>
		<td>${ schedulerStatus.running }</td>
		<td>${ schedulerStatus.jobStorageType }</td>
	</tr>
	</c:forEach>
	</tbody>
</table>

<%@ include file="/WEB-INF/views/page-b.inc" %>