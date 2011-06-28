<%@ include file="/WEB-INF/views/page-a.inc" %>
<%@ include file="/WEB-INF/views/dashboard/menu.inc" %>
<%@ include file="/WEB-INF/views/dashboard/submenu.inc" %>
<style>
#create-form table {
	width: 100%;
}
#create-form table tr {
	vertical-align: text-top;
}
#create-form table tr td {
	width: 300px;
}

#create-form table tr td td {
	text-align: left;
}
</style>
<h1>Create New Scheduler Service</h1>
<form method="post" action="create-action">
<table width="100%">
<tr>
	<td width="300px">
	<label>Config Url</label>
	</td>
	<td>
	<input type="text" name="configUrl" style="width: 100%;"/>
	<div>Eg: file:///data/myschedule/config/quartz.properties</div>
	</td>
</tr>
<tr>
	<td width="300px">
	<label>AutoStart</label>
	</td>
	<td>
	<select name="autoStart"><option selected="selected" value="true">True</option><option value="false">False</option></select>
	</td>
</tr>
<tr>
	<td width="300px">
	<label>Wait For Jobs To Complete <br/>(when shutdown)</label>
	</td>
	<td>
	<select name="waitForJobsToComplete"><option selected="selected" value="true">True</option><option value="false">False</option></select>
	</td>
</tr>
</table>
<div>
<input type="submit" value="Create"/>
</div>
</form>
<%@ include file="/WEB-INF/views/page-b.inc" %>