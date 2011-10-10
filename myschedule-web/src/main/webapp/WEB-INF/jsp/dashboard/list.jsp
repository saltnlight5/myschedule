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
	
	// Confirm shutdown scheduler
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
	
	// Confirm Delete config
	$("#delete-config-confirm").hide()
	$("#scheduler-list .delete-config-link").click(function() {
		var linkUrl = $(this).attr("href");
		$("#delete-config-confirm").dialog({
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

<div id="delete-config-confirm">
	<p><span class="ui-icon ui-icon-alert" style="float:left; margin:0 7px 20px 0;"></span>
	Are you sure you want to delete this scheduler configuration?
	</p>
</div>

<h1>List of All Schedulers</h1>
<table id="scheduler-list" class="display">
	<thead>
		<tr>
			<th>SCHEDULER NAME</th>
			<th>INITIALIZED</th>
			<th>STARTED</th>
			<th>RUNNING SINCE</th>
			<th># JOBS</th>
			<th>ACTION</th>
		</tr>
	</thead>
	<tbody>
	<c:forEach items="${ data.schedulerRows }" var="scheduler" varStatus="loop">
	<tr>
		<c:choose><c:when test="${ scheduler.initialized }">
			<td><a href="${ mainPath }/dashboard/switch-scheduler?configId=${ scheduler.configId }">${ scheduler.name }</a></td>
		</c:when><c:otherwise>
			<td>${ scheduler.name }</td>
		</c:otherwise></c:choose>
		<c:choose><c:when test="${ scheduler.initialized }">

			<c:choose><c:when test="${ scheduler.connExceptionExists }">
			<td>true / Error: + ${ scheduler.connExceptionString })</td>
			</c:when><c:otherwise>
			<td>true</td>
			</c:otherwise></c:choose>
			
			<td>${ scheduler.started }</td>
			<td><fmt:formatDate value="${ scheduler.runningSince }" pattern="MM/dd/yyyy HH:mm"/></td>
			<td>${ scheduler.numOfJobs }</td>
			<td>
				<a class="shutdown-link" href="${ mainPath }/dashboard/shutdown?configId=${ scheduler.configId }">Shutdown</a> |
				<a href="${ mainPath }/dashboard/modify?configId=${ scheduler.configId }">Modify</a> |
				<a class="delete-config-link" href="${ mainPath }/dashboard/delete-action?configId=${ scheduler.configId }">Delete</a>
			</td>
		</c:when><c:otherwise>
			<td>false</td>
			<td>N/A</td>
			<td>N/A</td>
			<td>N/A</td>
			<td>N/A</td>
			<td>N/A</td>
			<td>
				<a href="${ mainPath }/dashboard/init?configId=${ scheduler.configId }">Initialize</a> |
				<a href="${ mainPath }/dashboard/modify?configId=${ scheduler.configId }">Modify</a> |
				<a class="delete-config-link" href="${ mainPath }/dashboard/delete-action?configId=${ scheduler.configId }">Delete</a>
			</td>
		</c:otherwise></c:choose>
	</tr>
	</c:forEach>
	</tbody>
</table>
<%@ include file="/WEB-INF/jsp/page-b.inc" %>