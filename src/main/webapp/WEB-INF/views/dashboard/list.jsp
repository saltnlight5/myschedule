<%@ include file="/WEB-INF/views/page-a.inc" %>
<script>
$(document).ready(function() {
	// use dataTables plugin
	$("#scheduler-list").dataTable({		
		"bPaginate": false,
		"bLengthChange": false,
		"bFilter": true,
		"bSort": false,
		"bInfo": true,
		"bJQueryUI": true,
	});
});
</script>
<%@ include file="/WEB-INF/views/dashboard/menu.inc" %>
<%@ include file="/WEB-INF/views/dashboard/submenu.inc" %>
<h1>List of All Schedulers</h1>
<table id="scheduler-list" class="display">
	<thead>
		<tr>
			<th>SCHEDULER NAME</th>
			<th>INITIALIZED</th>
			<th>JOB STORAGE TYPE</th>
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