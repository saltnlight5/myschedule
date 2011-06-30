<%@ include file="/WEB-INF/views/page-a.inc" %>
<%@ include file="/WEB-INF/views/dashboard/menu.inc" %>
<%@ include file="/WEB-INF/views/dashboard/submenu.inc" %>
<style>
#create-scheduler-service {
	width: 100%;
	margin-right: 10px;
}
#create-scheduler .label {
	margin-top: 2px;
	margin-bottom: 2px;
}
#configProperties, #fileLocation {
	font-size: 1.5em;
	height: 2.0em;
	font-height: 1.2em;
	margin-top: 2px;
	margin-bottom: 2px;
}
#configProperties {
	height: 20.0em;
	width: 100%;
}
#fileLocation, #configProperties{
	width: 480px;
	width: 100%;
}
#autoStart, #waitForJobsToComplete {
	font-size: 1.5em;
	margin-top: 2px;
	margin-bottom: 2px;
}
#submit {
	font-size: 1.5em;
}

.two-cols div { 
	float: left;
	width: 50%; 
}
</style>
<script>
$(document).ready(function() {
	var fileLocationPrefix = "${ data.fileLocationPrefix }";
	$("#config-in-memory").click(function() {
		$("#configProperties").load("${ mainPath }/dashboard/get-config-eg?name=quartz.properties.in-memory");
		$("#fileLocation").attr('value', fileLocationPrefix + "/quartz-in-memory.properties");
	});
	$("#config-rmi-server").click(function() {
		$("#configProperties").load("${ mainPath }/dashboard/get-config-eg?name=quartz.properties.rmi-server");
		$("#fileLocation").attr('value', fileLocationPrefix + "/quartz-rmi-server.properties");
	});
	$("#config-rmi-client").click(function() {
		$("#configProperties").load("${ mainPath }/dashboard/get-config-eg?name=quartz.properties.rmi-client");
		$("#fileLocation").attr('value', fileLocationPrefix + "/quartz-rmi-client.properties");
	});
	$("#config-database").click(function() {
		$("#configProperties").load("${ mainPath }/dashboard/get-config-eg?name=quartz.properties.database");
		$("#fileLocation").attr('value', fileLocationPrefix + "/quartz-database.properties");
	});
	$("#config-database-clustered").click(function() {
		$("#configProperties").load("${ mainPath }/dashboard/get-config-eg?name=quartz.properties.database-clustered");
		$("#fileLocation").attr('value', fileLocationPrefix + "/quartz-database-clustered.properties");
	});
});
</script>
<h1>Create New Scheduler Service</h1>
<div id="create-scheduler-service">
<form method="post" action="create-action">

<div>
<label class="label">
Quartz Scheduler Config Properties
<span style="label-notes">(Eg: 
	<a name="config-in-memory" id="config-in-memory">In-Memory</a>, 
	<a name="config-rmi-server" id="config-rmi-server">RMI Server</a>, 
	<a name="config-rmi-client" id="config-rmi-client">RMI Client</a>, 
	<a name="config-database" id="config-database">Database</a>, 
	<a name="config-database-clustered" id="config-database-clustered">Database Cluster Node</a>
)</span>
</label>
<textarea id="configProperties" name="configProperties"></textarea>
</div>

<div>
<label class="label">File Saving Location <span style="label-notes">(Eg: /data/myschedule/myscheduler.properties)</span></label>
<input id="fileLocation" type="text" name="fileLocation" value=""/>
</div>

<div class="two-cols">
<div>
<label class="label">AutoStart</label>
<select id="autoStart" name="autoStart"><option selected="selected" value="true">True</option><option value="false">False</option></select>
</div>

<div>
<label class="label">Wait For Jobs To Complete <span style="label-notes">(when scheduler is shutdown)</span></label>
<select id="waitForJobsToComplete" name="waitForJobsToComplete"><option selected="selected" value="true">True</option><option value="false">False</option></select>
</div>
</div>

<div>
	<span id="empty-label" class="label"></span></div>
	<input id="submit" type="submit" value="Create Scheduler"/>
</div>

</form>
</div>
<%@ include file="/WEB-INF/views/page-b.inc" %>