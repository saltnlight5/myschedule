<%@ include file="/WEB-INF/jsp/main/page-a.inc" %>
<script>
$(document).ready(function() {
	$('#tabs').tabs({
	    select: function(event, ui) {
	        var url = $.data(ui.tab, 'load.tabs');
	        if( url ) {
	            location.href = url;
	            return false;
	        }
	        return true;
	    }
	});
</script>

<div id="tabs">
	<ul>
	<li><a href="#">Jobs with trigger</a></li>
	<li><a href="${ mainPath }/job/ajax/list-no-trigger-jobs">Jobs without trigger</a></li>
	<li><a href="${ mainPath }/job/ajax/list-executing-jobs">Currently Executing Jobs</a></li>
	</ul>
	
	<div id="tabs-1">

		<div id="page-container">
		<h1>Unscheduled Job</h1>
		
		<div class="success">Trigger ${ data.trigger.fullName } has been removed.</div>
		<c:if test="${ empty data.jobDetail }">
		<div class="info">The JobDetails ${ data.trigger.jobName }.${ data.trigger.jobGroup } 
		has no more trigger associated with it, so it was also removed by scheduler!</div>
		</c:if>
		
		</div> <!-- page-container -->
			</div>
		</div>
<%@ include file="/WEB-INF/jsp/main/page-b.inc" %>