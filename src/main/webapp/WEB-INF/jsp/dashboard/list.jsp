<%@ include file="/WEB-INF/jsp/page-a.inc" %>
<%@ include file="/WEB-INF/jsp/dashboard/menu.inc" %>
<%@ include file="/WEB-INF/jsp/dashboard/submenu.inc" %>
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
			<th>PERSISTENCE</th>
			<th>RUNNING SINCE</th>
			<th># JOBS</th>
			<th>ACTION</th>
		</tr>
	</thead>
	<tbody>
	<c:forEach items="${ data.schedulerStatusList }" var="schedulerStatus" varStatus="loop">
	<tr>
		<c:choose><c:when test="${ schedulerStatus.initialized }">
			<td><a href="${ mainPath }/dashboard/switch-scheduler?configId=${ schedulerStatus.configId }">${ schedulerStatus.name }</a></td>
		</c:when><c:otherwise>
			<td>${ schedulerStatus.name }</td>
		</c:otherwise></c:choose>
		<td>${ schedulerStatus.initialized }</td>
		<c:choose><c:when test="${ schedulerStatus.initialized }">
			<td>${ schedulerStatus.started }</td>
			<td>${ schedulerStatus.standby }</td>
			<td>${ schedulerStatus.schedulerMetaData.jobStoreSupportsPersistence }</td>
			<td><fmt:formatDate value="${ schedulerStatus.schedulerMetaData.runningSince }" pattern="MM/dd/yyyy HH:mm"/></td>
			<td>${ schedulerStatus.jobCount }</td>
			<td><a class="shutdown-link" href="${ mainPath }/dashboard/shutdown?configId=${ schedulerStatus.configId }">Shutdown</a></td>
		</c:when><c:otherwise>
			<td>N/A</td>
			<td>N/A</td>
			<td>N/A</td>
			<td>N/A</td>
			<td>N/A</td>
			<td><a href="${ mainPath }/dashboard/init?configId=${ schedulerStatus.configId }">Initialize</a></td>
		</c:otherwise></c:choose>
	</tr>
	</c:forEach>
	</tbody>
</table>
<%@ include file="/WEB-INF/jsp/page-b.inc" %>