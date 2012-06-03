<%@ include file="/WEB-INF/jsp/main/page-a.inc" %>
<%@ include file="/WEB-INF/jsp/main/dashboard/menu.inc" %>
<%@ include file="/WEB-INF/jsp/main/dashboard/submenu.inc" %>
<h1>Delete Scheduler Service</h1>
<div class="success">
The scheduler service 
<c:if test="${ not empty data.schedulerName }">( ${ data.schedulerName } )</c:if> 
with configuration id ${ data.configId } has been removed.
</div>
<%@ include file="/WEB-INF/jsp/main/page-b.inc" %>