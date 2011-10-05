<%@ include file="/WEB-INF/jsp/page-a.inc" %>
<%@ include file="/WEB-INF/jsp/menu.inc" %>
<%@ include file="/WEB-INF/jsp/scheduler/submenu.inc" %>
<h1>Modify Scheduler Config</h1>
<div class="success">
The scheduler service 
<c:if test="${ not empty data.schedulerName }">
( 
<a href="${ mainPath }/dashboard/switch-scheduler?configId=${ data.schedulerService.schedulerConfig.configId }">
${ data.schedulerName }
</a> 
)
</c:if> 
with configuration id ${ data.schedulerService.schedulerConfig.configId } has been updated.
</div>
<%@ include file="/WEB-INF/jsp/page-b.inc" %>