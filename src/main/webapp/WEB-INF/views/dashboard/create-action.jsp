<%@ include file="/WEB-INF/views/page-a.inc" %>
<%@ include file="/WEB-INF/views/dashboard/menu.inc" %>
<%@ include file="/WEB-INF/views/dashboard/submenu.inc" %>
<h1>Create New Scheduler Service</h1>
<div class="success">
A new scheduler service 
<a href="${ mainPath }/dashboard/switch-scheduler?name=${ data.schedulerService.name }">${ data.schedulerService.name }</a> 
has been successfully created.
</div>
<%@ include file="/WEB-INF/views/page-b.inc" %>