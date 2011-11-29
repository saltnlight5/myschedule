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
		<h1>Job Deleted</h1>
		
		<div class="success">Job ${ data.jobDetail.fullName } has been deleted.</div>
		
		<c:if test="${ not empty data.triggers }">
		<div class="info">And these triggers are also removed.</div>
		<ul>
		<c:forEach items="${ data.triggers }" var="item">
			<li>Trigger: ${ item.fullName }</li> 
		</c:forEach>
		</ul>
		</c:if>
		
		</div> <!-- page-container -->
	</div>
</div>
<%@ include file="/WEB-INF/jsp/main/page-b.inc" %>