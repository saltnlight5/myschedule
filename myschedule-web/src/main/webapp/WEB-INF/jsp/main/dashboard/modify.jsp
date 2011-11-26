<%@ include file="/WEB-INF/jsp/main/page-a.inc" %>
<script>
$(document).ready(function() {
	$('#tabs').tabs({
		selected: 2,
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

<style>
#modify-form { width: 100%; }
#modify-form label { margin-top: 2px; margin-bottom: 2px; }
#modify-form textarea { 
	font-size: 15px;
	font-family: monospace;
	height: 400px;
	font-height: 17px;
	margin-top: 2px;
	margin-bottom: 2px;
	width: 99.5%;
}
</style>

<div id="tabs">
	<ul>
	<li><a href="${ mainPath }/dashboard/index">Scheduler List</a></li>
	<li><a href="${ mainPath }/dashboard/create">Add Scheduler</a></li>
	<li><a href="#">Modify Config</a></li>
	</ul>
	
	<div id="tabs-1">
	
		<h1>Modify Scheduler Config</h1>
		
		<div class="warning">Modifying configuration will auto restart your scheduler upon save!</div>
		
		<form id="modify-form" method="post" action="modify-action">
			<label class="label">Quartz Scheduler Properties</label>
			<textarea id="configPropsText" name="configPropsText">${ data.configPropsText }</textarea>
		
			<input type="hidden" name="configId" value="${ data.configId }"/>
			<input id="submit" type="submit" value="Modify Scheduler"/>
		</form>
		
	</div>
</div>
	
<%@ include file="/WEB-INF/jsp/main/page-b.inc" %>