<%@ include file="/WEB-INF/jsp/main/page-a.inc" %>
<%@ include file="/WEB-INF/jsp/main/menu.inc" %>
<%@ include file="/WEB-INF/jsp/main/scheduler/submenu.inc" %>
<h1>Modify Scheduler Config</h1>
<div class="success">
The scheduler service 
<c:if test="${ not empty data.schedulerName }">
( 
<a href="${ mainPath }/dashboard/switch-scheduler?configId=${ data.schedulerService.configId }">
${ data.schedulerName }
</a> 
)
</c:if> 
with configuration id ${ data.schedulerService.configId } has been updated.
</div>
<%@ include file="/WEB-INF/jsp/main/page-b.inc" %>