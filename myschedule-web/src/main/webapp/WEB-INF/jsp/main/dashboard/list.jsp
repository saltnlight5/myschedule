<%@ include file="/WEB-INF/jsp/main/page-a.inc" %>
<%@ include file="/WEB-INF/jsp/main/dashboard/menu.inc" %>
<%@ include file="/WEB-INF/jsp/main/dashboard/submenu.inc" %>
<script>
$(document).ready(function() {
	// Use dataTables plugin
	$("#schedulerRow-list").dataTable({		
		"bPaginate": false,
		"bLengthChange": false,
		"bFilter": true,
		"bSort": false,
		"bInfo": true,
		"bJQueryUI": true,
	});
	
	// Confirm shutdown schedulerRow
	$("#shutdown-confirm").hide();
	$("#schedulerRow-list .shutdown-link").click(function() {
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
	$("#schedulerRow-list .delete-config-link").click(function() {
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
	Are you sure you want to shutdown the schedulerRow?
	</p>
</div>

<div id="delete-config-confirm">
	<p><span class="ui-icon ui-icon-alert" style="float:left; margin:0 7px 20px 0;"></span>
	Are you sure you want to delete this schedulerRow configuration?
	</p>
</div>
<c:if test="not empty ${ sessionScope['flashMsg'] }">
<div id="flash-msg" class="${ sessionScope['flashMsg'].msgType }">
	<p><pre>${ sessionScope['flashMsg'].msg }</pre></p>
</div>
</c:if>

<h1>List of All Schedulers</h1>
<table id="schedulerRow-list" class="display">
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
	<c:forEach items="${ data.schedulerRows }" var="schedulerRow" varStatus="loop">
	<tr>
		<c:choose><c:when test="${ schedulerRow.initialized }">
			<td><a href="${ mainPath }/dashboard/switch-scheduler?configId=${ schedulerRow.configId }">${ schedulerRow.name }</a></td>
			<c:choose><c:when test="${ schedulerRow.connExceptionExists }">
				<td>
					<span id="error_${ schedulerRow.configId }" style="color: red;">ERROR</span>
					<!-- <div id="errormsg_${ schedulerRow.configId }">
					<pre>${ schedulerRow.connExceptionString }</pre>
					</div> -->
				</td> <!-- initialized -->
				<td>N/A</td> <!-- started -->
				<td>N/A</td> <!-- runningSince -->
				<td>N/A</td> <!-- numOfJobs -->
			</c:when><c:otherwise>
				<td>true</td> <!-- initialize -->
				<td>${ schedulerRow.started }</td>
				<td><fmt:formatDate value="${ schedulerRow.runningSince }" pattern="MM/dd/yyyy HH:mm"/></td>
				<td>${ schedulerRow.numOfJobs }</td>
			</c:otherwise></c:choose>
		
			<td>
				<a class="shutdown-link" href="${ mainPath }/dashboard/shutdown?configId=${ schedulerRow.configId }">Shutdown</a> |
				<a href="${ mainPath }/dashboard/modify?configId=${ schedulerRow.configId }">Modify</a> |
				<a class="delete-config-link" href="${ mainPath }/dashboard/delete-action?configId=${ schedulerRow.configId }">Delete</a>
			</td>
			
		</c:when><c:otherwise>
			<td>${ schedulerRow.name }</td>
			<td>false</td>  <!-- initialize -->
			<td>N/A</td> <!-- started -->
			<td>N/A</td> <!-- runningSince -->
			<td>N/A</td> <!-- numOfJobs -->
			<td>
				<a href="${ mainPath }/dashboard/init?configId=${ schedulerRow.configId }">Initialize</a> |
				<a href="${ mainPath }/dashboard/modify?configId=${ schedulerRow.configId }">Modify</a> |
				<a class="delete-config-link" href="${ mainPath }/dashboard/delete-action?configId=${ schedulerRow.configId }">Delete</a>
			</td>
		</c:otherwise></c:choose>
	</tr>
	</c:forEach>
	</tbody>
</table>
<%@ include file="/WEB-INF/jsp/main/page-b.inc" %>