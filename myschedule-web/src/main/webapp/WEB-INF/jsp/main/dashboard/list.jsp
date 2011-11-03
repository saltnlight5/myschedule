<%@ include file="/WEB-INF/jsp/main/page-a.inc" %>
<%@ include file="/WEB-INF/jsp/main/dashboard/menu.inc" %>
<%@ include file="/WEB-INF/jsp/main/dashboard/submenu.inc" %>
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
	
	// Confirm shutdown schedulerMap
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
	Are you sure you want to shutdown the schedulerMap?
	</p>
</div>

<div id="delete-config-confirm">
	<p><span class="ui-icon ui-icon-alert" style="float:left; margin:0 7px 20px 0;"></span>
	Are you sure you want to delete this schedulerMap configuration?
	</p>
</div>
<c:if test="not empty ${ sessionScope['flashMsg'] }">
<div id="flash-msg" class="${ sessionScope['flashMsg'].msgType }">
	<p><pre>${ sessionScope['flashMsg'].msg }</pre></p>
</div>
</c:if>

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
	<c:forEach items="${ data.schedulerList }" var="schedulerMap" varStatus="loop">
	<tr>
		<c:choose><c:when test="${ schedulerMap.inited }">
			<td><a href="${ mainPath }/dashboard/switch-scheduler?configId=${ schedulerMap.configId }">${ schedulerMap.name }</a></td>
			<td>true</td> <!-- initialize -->
			<td>${ schedulerMap.started }</td>
			<td>${ schedulerMap.runningSince }</td>
			<td>${ schedulerMap.numOfJobs }</td>
			<td>
				<a class="shutdown-link" href="${ mainPath }/dashboard/shutdown?configId=${ schedulerMap.configId }">Shutdown</a> |
				<a href="${ mainPath }/dashboard/modify?configId=${ schedulerMap.configId }">Modify</a> |
				<a class="delete-config-link" href="${ mainPath }/dashboard/delete-action?configId=${ schedulerMap.configId }">Delete</a>
			</td>			
		</c:when><c:otherwise>
			<td>${ schedulerMap.name }</td>
			
			<c:choose><c:when test="${ not empty schedulerMap.initException }">
				<td><span id="error_${ schedulerMap.configId }" style="color: red;">ERROR</span></td> <!-- initialized -->
			</c:when><c:otherwise>
				<td>false</td> <!-- initialize -->
			</c:otherwise></c:choose>
			
			<td>N/A</td> <!-- started -->
			<td>N/A</td> <!-- runningSince -->
			<td>N/A</td> <!-- numOfJobs -->
			<td>
				<a href="${ mainPath }/dashboard/init?configId=${ schedulerMap.configId }">Initialize</a> |
				<a href="${ mainPath }/dashboard/modify?configId=${ schedulerMap.configId }">Modify</a> |
				<a class="delete-config-link" href="${ mainPath }/dashboard/delete-action?configId=${ schedulerMap.configId }">Delete</a>
			</td>
		</c:otherwise></c:choose>
	</tr>
	</c:forEach>
	</tbody>
</table>
<%@ include file="/WEB-INF/jsp/main/page-b.inc" %>