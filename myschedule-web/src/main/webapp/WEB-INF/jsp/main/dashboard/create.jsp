<%@ include file="/WEB-INF/jsp/main/page-a.inc" %>
<script>
$(document).ready(function() {
	$('#tabs').tabs({
		selected: 1,
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
#create-form { width: 100%; }
#create-form label { margin-top: 2px; margin-bottom: 2px; }
#create-form textarea { 
	font-size: 15px;
	font-family: monospace;
	height: 400px;
	font-height: 17px;
	margin-top: 2px;
	margin-bottom: 2px;
	width: 99.5%;
}
</style>

<script>
$(document).ready(function() {
	$("#config-in-memory").click(function() {
		$("#configPropsText").load("${ mainPath }/dashboard/create-config-sample?name=quartz.properties.in-memory");
	});
	$("#config-database").click(function() {
		$("#configPropsText").load("${ mainPath }/dashboard/create-config-sample?name=quartz.properties.database");
	});
	$("#config-database-clustered").click(function() {
		$("#configPropsText").load("${ mainPath }/dashboard/create-config-sample?name=quartz.properties.database-clustered");
	});
	$("#config-database-cmt").click(function() {
		$("#configPropsText").load("${ mainPath }/dashboard/create-config-sample?name=quartz.properties.database-cmt");
	});
	$("#config-jmx").click(function() {
		$("#configPropsText").load("${ mainPath }/dashboard/create-config-sample?name=quartz.properties.jmx");
	});
	$("#config-rmi-server").click(function() {
		$("#configPropsText").load("${ mainPath }/dashboard/create-config-sample?name=quartz.properties.rmi-server");
	});
	$("#config-rmi-client").click(function() {
		$("#configPropsText").load("${ mainPath }/dashboard/create-config-sample?name=quartz.properties.rmi-client");
	});
	$("#config-full-default").click(function() {
		$("#configPropsText").load("${ mainPath }/dashboard/create-config-sample?name=quartz.properties.default");
	});
});
</script>

<div id="tabs">
	<ul>
	<li><a href="${ mainPath }/dashboard/index">Scheduler List</a></li>
	<li><a href="#">Add Scheduler</a></li>
	</ul>
	
	<div id="tabs-1">	
		<h1>Create New Scheduler Service</h1>
		<form id="create-form" method="post" action="create-action">
			<label>Quartz Scheduler Config Properties
			<span style="label-notes">(Eg: 
				<a name="config-in-memory" id="config-in-memory">In-Memory</a>, 
				<a name="config-database" id="config-database">Database</a>, 
				<a name="config-database-clustered" id="config-database-clustered">Database Cluster Node</a>,  
				<a name="config-database-cmt" id="config-database-cmt">Database CMT/JTA</a>, 
				<a name="config-jmx" id="config-jmx">Jmx Enabled</a>, 
				<a name="config-rmi-server" id="config-rmi-server">RMI Server</a>, 
				<a name="config-rmi-client" id="config-rmi-client">RMI Client</a>, 
				<a name="config-rmi-client" id="config-full-default">Full Quartz Defaults</a>)
			</span>
			</label>
			<textarea id="configPropsText" name="configPropsText"></textarea>
			<input id="submit" type="submit" value="Create Scheduler"/>	
		</form>	
	</div> <!-- tabs-1 -->	
</div> <!-- tabs -->
	
<%@ include file="/WEB-INF/jsp/main/page-b.inc" %>
