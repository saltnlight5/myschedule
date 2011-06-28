<%@ include file="/WEB-INF/views/page-a.inc" %>
<link rel="stylesheet" type="text/css" href="${contextPath}/themes/${themeName}/datatables-css/table_jui.css" />
<script src="${contextPath}/js/jquery.dataTables-1.8.0.js"></script>
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
<%@ include file="/WEB-INF/views/dashboard/menu.inc" %>
<%@ include file="/WEB-INF/views/dashboard/submenu.inc" %>
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