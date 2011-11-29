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
	<li><a href="#">Scripting</a></li>
	</ul>
	
	<div id="tabs-1">
		<div class="page-container">
		<h1>Scheduler Scripting</h1>
		<div class="success">Your script has been run successfully.</div>
		
		<c:if test="${ not empty data.webOutResult }">
		<div class="info">
		<pre>${ data.webOutResult }</pre>
		</div>
		</c:if>
		</div><!-- page-container -->
	</div>
	
</div>

<%@ include file="/WEB-INF/jsp/main/page-b.inc" %>