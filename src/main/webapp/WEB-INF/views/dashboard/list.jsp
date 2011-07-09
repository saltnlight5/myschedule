<%@ include file="/WEB-INF/views/page-a.inc" %>
<%@ include file="/WEB-INF/views/dashboard/menu.inc" %>
<%@ include file="/WEB-INF/views/dashboard/submenu.inc" %>
<script>
$(document).ready(function() {
	// Use dataTables plugin
	$("#scheduler-list").dataTable({		
		"bPaginate": false,
		"bLengthChange": false,
		"bFilter": true,
		"bSort": false,
		"bInfo": true,
		"bJQueryUI": true,
	});
	
	// Confirm unschedule job
	$("#shutdown-confirm").hide()
	$("#scheduler-list .shutdown-link").click(function() {
		var linkUrl = $(this).attr("href");
		$("#shutdown-confirm").dialog({
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

<div id="shutdown-confirm">
	<p><span class="ui-icon ui-icon-alert" style="float:left; margin:0 7px 20px 0;"></span>
	Are you sure you want to shutdown the scheduler?
	</p>
</div>

<h1>List of All Schedulers</h1>
<table id="scheduler-list" class="display">
	<thead>
		<tr>
			<th>SCHEDULER NAME</th>
			<th>INITIALIZED</th>
			<th>STARTED</th>
			<th>STANDBY</th>
			<th>PAUSED</th>
			<th>PERSISTENCE</th>
			<th>RUNNING SINCE</th>
			<th># JOBS</th>
			<th>ACTION</th>
		</tr>
	</thead>
	<tbody>
	<c:forEach items="${ data.schedulerStatusList }" var="schedulerStatus" varStatus="loop">
	<tr>
		<td><a href="${ mainPath }/dashboard/switch-scheduler?name=${ schedulerStatus.name }">${ schedulerStatus.name }</a></td>
		<td>${ schedulerStatus.initialized }</td>
		<c:choose><c:when test="${ schedulerStatus.initialized }">
			<td>${ schedulerStatus.started }</td>
			<td>${ schedulerStatus.standby }</td>
			<td>${ schedulerStatus.paused }</td>
			<td>${ schedulerStatus.schedulerMetaData.jobStoreSupportsPersistence }</td>
			<td><fmt:formatDate value="${ schedulerStatus.schedulerMetaData.runningSince }" pattern="MM/dd/yyyy HH:mm"/></td>
			<td>${ schedulerStatus.jobCount }</td>
			<td><a class="shutdown-link" href="${ mainPath }/dashboard/shutdown?name=${ schedulerStatus.name }">Shutdown</a></td>
		</c:when><c:otherwise>
			<td>N/A</td>
			<td>N/A</td>
			<td>N/A</td>
			<td>N/A</td>
			<td>N/A</td>
			<td>N/A</td>
			<td><a href="${ mainPath }/dashboard/init?name=${ schedulerStatus.name }">Initialize</a></td>
		</c:otherwise>
		</c:choose>
	</tr>
	</c:forEach>
	</tbody>
</table>
<%@ include file="/WEB-INF/views/page-b.inc" %>