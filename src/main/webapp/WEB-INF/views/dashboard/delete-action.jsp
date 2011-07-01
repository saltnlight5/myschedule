<%@ include file="/WEB-INF/views/page-a.inc" %>
<%@ include file="/WEB-INF/views/dashboard/menu.inc" %>
<%@ include file="/WEB-INF/views/dashboard/submenu.inc" %>
<h1>Delete Scheduler Service</h1>
<div class="success">
The scheduler service, ${ data.removedSchedulerName }, has been successfully removed.
</div>
<%@ include file="/WEB-INF/views/page-b.inc" %>