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
			<th>READY</th>
			<th>STARTED</th>
			<th>PAUSED</th>
			<th>PERSISTENCE</th>
			<th>RUNNING SINCE</th>
			<th># JOBS</th>
		</tr>
	</thead>
	<tbody>
	<c:forEach items="${ data.schedulerStatusList }" var="schedulerStatus" varStatus="loop">
	<tr>
		<td><a href="${ mainPath }/dashboard/switch-scheduler?name=${ schedulerStatus.name }">${ schedulerStatus.name }</a></td>
		<td>${ schedulerStatus.initialized }</td>
		<c:choose><c:when test="${ schedulerStatus.initialized }">
			<td>${ schedulerStatus.started }</td>
			<td>${ schedulerStatus.paused }</td>
			<td>${ schedulerStatus.schedulerMetaData.jobStoreSupportsPersistence }</td>
			<td><fmt:formatDate value="${ schedulerStatus.schedulerMetaData.runningSince }" pattern="MM/dd/yyyy HH:mm"/></td>
			<td>${ schedulerStatus.jobCount }</td>
		</c:when><c:otherwise>
			<td>N/A</td>
			<td>N/A</td>
			<td>N/A</td>
			<td>N/A</td>
			<td>N/A</td>
		</c:otherwise>
		</c:choose>
	</tr>
	</c:forEach>
	</tbody>
</table>
<%@ include file="/WEB-INF/views/page-b.inc" %>