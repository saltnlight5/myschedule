<%@ include file="/WEB-INF/jsp/main/page-a.inc" %>
<script>
$(document).ready(function() {
	$("#tabs").tabs({
		selected: 0,
		ajaxOptions: {	error: function( xhr, status, index, anchor ) { $( anchor.hash ).html("Failed to load content." ); } }
	});
});
</script>
<style>
pre { font-size: 15px; }
</style>
<div id="tabs">	
	<ul>
	<li><a href="#">Scheduler Summary</a></li>
	<li><a href="${ mainPath }/scheduler/detail">Detail</a></li>
	<li><a href="${ mainPath }/scheduler/listeners">Listeners</a></li>
	<li><a href="${ mainPath }/job/list-calendars">Calendars</a></li>
	<li><a href="${ mainPath }/scheduler/modify">Config Properties</a></li>
	</ul>
	
	<div id="tabs-1">
		<h1>Scheduler Summary</h1>
		<pre>${ data.schedulerSummary }</pre>
		
		<h1>Scheduler Controls</h1>
		<ul>
		<c:choose><c:when test="${ data.isStandby }">
			<li><a href="${ mainPath }/scheduler/start">Start Scheduler</a></li>
		</c:when><c:otherwise>
			<li><a href="${ mainPath }/scheduler/standby">Standby Scheduler</a></li>
			<li><a href="${ mainPath }/scheduler/pause-all-triggers">Pause All Triggers</a></li>
			<li><a href="${ mainPath }/scheduler/resume-all-triggers">Resume All Triggers</a></li>
		</c:otherwise></c:choose>
		</ul>
		
	</div>
</div>

<%@ include file="/WEB-INF/jsp/main/page-b.inc" %>