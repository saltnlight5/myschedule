<%@ include file="/WEB-INF/views/page-a.inc" %>
<%@ include file="/WEB-INF/views/dashboard/menu.inc" %>
<%@ include file="/WEB-INF/views/dashboard/submenu.inc" %>
<h1>Create New Scheduler Service</h1>
<form method="post" action="create-action">
<div><label>Config Url</label><input type="text" name="configUrl"/></div>
<div><label>AutoStart</label><select name="autoStart"><option selected="selected" value="true">True</option><option value="false">False</option></select></div>
<div><label>Wait For Jobs To Complete (when shutdown)</label><select name="waitForJobsToComplete"><option selected="selected" value="true">True</option><option value="false">False</option></select></div>
<input type="submit" value="Create"/>
</form>
<%@ include file="/WEB-INF/views/page-b.inc" %>