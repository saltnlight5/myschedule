<%@ include file="/WEB-INF/views/page-a.inc" %>
<%@ include file="/WEB-INF/views/dashboard/menu.inc" %>
<%@ include file="/WEB-INF/views/dashboard/submenu.inc" %>
<h1>Delete Scheduler Service</h1>
<div class="success">
The scheduler service 
<a href="${ mainPath }/dashboard/switch-scheduler?name=${ data.schedulerService.name }">${ data.schedulerService.name }</a> 
has been successfully updated 
and re-initialized <c:if test="${ data.origRunning }"> and restarted</c:if>.
</div>
<%@ include file="/WEB-INF/views/page-b.inc" %>