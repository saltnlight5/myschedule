<%@ include file="/WEB-INF/jsp/main/page-a.inc" %>

<script>
$(document).ready(function() {
	$("#tabs").tabs({
		selected: 2,
		ajaxOptions: {	error: function( xhr, status, index, anchor ) { $( anchor.hash ).html("Failed to load content." ); } }
	});
});
</script>

<div id="tabs">	
	<ul>
	<li><a href="${ mainPath }/scheduler/index">Scheduler Summary</a></li>
	<li><a href="${ mainPath }/scheduler/detail">Detail</a></li>
	<li><a href="${ mainPath }/scheduler/listeners">Listeners</a></li>
	<li><a href="${ mainPath }/job/list-calendars">Calendars</a></li>
	<li><a href="#">Config Properties</a></li>
	</ul>
	
	<div id="tabs-1">
	
	<h1>Modify Scheduler Config</h1>
	<div class="success">
	The scheduler service 
	<c:if test="${ not empty data.schedulerName }">
	( 
	<a href="${ mainPath }/dashboard/switch-scheduler?configId=${ data.schedulerService.configId }">
	${ data.schedulerName }
	</a> 
	)
	</c:if> 
	with configuration id ${ data.schedulerService.configId } has been updated.
	</div>

	</div>
</div>

<%@ include file="/WEB-INF/jsp/main/page-b.inc" %>