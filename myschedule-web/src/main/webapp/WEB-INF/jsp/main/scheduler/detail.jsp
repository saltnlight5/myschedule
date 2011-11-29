<%@ include file="/WEB-INF/jsp/main/page-a.inc" %>
<script>
$(document).ready(function() {
	$("#tabs").tabs({
		selected: 1,
		ajaxOptions: {	error: function( xhr, status, index, anchor ) { $( anchor.hash ).html("Failed to load content." ); } }
	});
	
	// use dataTables plugin
	$("#name-value-table").dataTable({		
		"bPaginate": false,
		"bLengthChange": false,
		"bFilter": false,
		"bSort": false,
		"bInfo": false,
		"bAutoWidth": false
	});
});
</script>

<div id="tabs">
	<ul>
	<li><a href="${ mainPath }/scheduler/index">Scheduler Summary</a></li>
	<li><a href="#">Detail</a></li>
	<li><a href="${ mainPath }/scheduler/listeners">Listeners</a></li>
	<li><a href="${ mainPath }/job/list-calendars">Calendars</a></li>
	<li><a href="${ mainPath }/scheduler/modify">Config Properties</a></li>
	</ul>
	
	<div id="tabs-1">
		<h1>Scheduler Details for ${ data.schedulerName }</h1>
		<c:choose><c:when test="${ data.isStandby }">
			<div class="warning">Scheduler is in standby mode! You may start scheduler again to get out of standby mode.</div>
		</c:when><c:otherwise>
			<c:if test="${ data.isPaused }">
				<div class="warning">Scheduler is in paused mode! All triggers are paused. You may resume scheduler again to un-pause it.</div>
			</c:if>		
			<table id="name-value-table">
				<thead>
				<tr>
					<th></th>
					<th></th>
				</tr>
				</thead>
				<tbody>
				<tr>
					<td> jobCount </td>
					<td> ${ data.jobCount }</td>
				</tr>
				<c:forEach items="${ data.schedulerDetailMap }" var="item" varStatus="status">
				<tr>
					<td> ${ item.key }</td>
					<td> ${ item.value }</td>
				</tr>
				</c:forEach>
				</tbody>
			</table>
		</c:otherwise></c:choose>
	</div>
</div>

<%@ include file="/WEB-INF/jsp/main/page-b.inc" %>