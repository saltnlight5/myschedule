<%@ include file="/WEB-INF/jsp/main/page-a.inc" %>
<%@ include file="/WEB-INF/jsp/main/dashboard/menu.inc" %>
<%@ include file="/WEB-INF/jsp/main/dashboard/submenu.inc" %>
<script>
$(document).ready(function() {
	// Use dataTables plugin
	$("#scheduler-list").dataTable({		
		"aaSorting": [[0, "asc"]],
		"bPaginate": false,
		"bLengthChange": false,
		"bFilter": true,
		"bInfo": true,
		"bJQueryUI": true,
	});
	
	// Confirm shutdown schedulerDetail
	$("#shutdown-confirm").hide();
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
	$("#delete-config-confirm").hide();
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
	Are you sure you want to shutdown the schedulerDetail?
	</p>
</div>

<div id="delete-config-confirm">
	<p><span class="ui-icon ui-icon-alert" style="float:left; margin:0 7px 20px 0;"></span>
	Are you sure you want to delete this schedulerDetail configuration?
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
	<c:forEach items="${ data.schedulerList }" var="schedulerDetail" varStatus="loop">
	<tr>
		<c:choose><c:when test="${ not empty schedulerDetail.initExceptionMessage }">
			<td style="color: red;">${ schedulerDetail.name }
				<p>${ schedulerDetail.initExceptionMessage }</p>
			</td>
		</c:when><c:when test="${ schedulerDetail.initialized == 'true' }">
				<td><a href="${ mainPath }/dashboard/switch-scheduler?configId=${ schedulerDetail.configId }">${ schedulerDetail.name }</a></td>
		</c:when><c:otherwise>
			<td>${ schedulerDetail.name }</td>
		</c:otherwise></c:choose>
		
		<td>${ schedulerDetail.initialized }</td>			
		<td>${ schedulerDetail.started }</td>
		<td>${ schedulerDetail.runningSince }</td>
		<td>${ schedulerDetail.numOfJobs }</td>
		
		<td>
			<c:choose><c:when test="${ empty schedulerDetail.initExceptionMessage and schedulerDetail.initialized == 'true' }">
				<a class="shutdown-link" href="${ mainPath }/dashboard/shutdown?configId=${ schedulerDetail.configId }">Shutdown</a> |
			</c:when><c:otherwise>
				<a href="${ mainPath }/dashboard/init?configId=${ schedulerDetail.configId }">Initialize</a> |
			</c:otherwise></c:choose>
			<a href="${ mainPath }/dashboard/modify?configId=${ schedulerDetail.configId }">Modify</a> |
			<a class="delete-config-link" href="${ mainPath }/dashboard/delete-action?configId=${ schedulerDetail.configId }">Delete</a>
		</td>
	</tr>
	</c:forEach>
	</tbody>
</table>
<%@ include file="/WEB-INF/jsp/main/page-b.inc" %>