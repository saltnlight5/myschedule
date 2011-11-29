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
	<li><a href="#">Load Xml Jobs</a></li>
	</ul>
	
	<div id="tabs-1">
		<div id="page-container">
		<h1>Load Job Scheduling Data</h1>
		<div class="success">Job loaded successfully.</div>
		
		<table class="outlined datalist">
			<tr>
				<td> LOADED JOBS </td>
			</tr>
			<c:forEach items="${ data.xmlLoadedJobList.loadedJobs }" var="item">
			<tr>
				<td> ${ item } </td>
			</tr>
			</c:forEach>
		</table>
		
		<table class="outlined datalist">
			<tr>
				<td> LOADED TRIGGERS </td>
			</tr>
			<c:forEach items="${ data.xmlLoadedJobList.loadedTriggers }" var="item">
			<tr>
				<td> ${ item } </td>
			</tr>
			</c:forEach>
		</table>		
		</div> <!-- page-container -->
	</div>
</div>
<%@ include file="/WEB-INF/jsp/main/page-b.inc" %>