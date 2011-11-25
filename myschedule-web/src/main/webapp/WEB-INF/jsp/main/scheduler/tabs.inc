<div id="tabs">	
	<ul>
	<li><a href="${ mainPath }/scheduler/ajax/summary">Scheduler Summary</a></li>
	<li><a href="${ mainPath }/scheduler/ajax/detail">Detail</a></li>
	<li><a href="${ mainPath }/scheduler/ajax/listeners">Listeners</a></li>
	<li><a href="${ mainPath }/job/ajax/list-calendars">Calendars</a></li>
	<li><a href="${ mainPath }/scheduler/ajax/modify">Config Properties</a></li>
	
	<%--
	<c:choose><c:when test="${ data.isStandby }">
		<li><a href="${ mainPath }/scheduler/start">Start Scheduler</a></li>
	</c:when><c:otherwise>
		<li><a href="${ mainPath }/scheduler/standby">Standby Scheduler</a></li>
		<li><a href="${ mainPath }/scheduler/pause-all-triggers">Pause All Triggers</a></li>
		<li><a href="${ mainPath }/scheduler/resume-all-triggers">Resume All Triggers</a></li>
	</c:otherwise></c:choose>
	--%>
		
	</ul>
	<div id="tabs-1"></div>
</div>
<script>
$(document).ready(function() {
	$("#tabs").tabs({
		ajaxOptions: {
			error: function( xhr, status, index, anchor ) {
				$( anchor.hash ).html("Failed to load content." );
			}
		}
	});
});
</script>