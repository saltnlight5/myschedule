<%@ include file="/WEB-INF/views/page-a.inc" %>
<%@ include file="/WEB-INF/views/dashboard/menu.inc" %>
<%@ include file="/WEB-INF/views/dashboard/submenu.inc" %>
<h1>Create New Scheduler Service</h1>
<c:choose>
<c:when test="${ not empty data.initFailedExceptionString }">
	<div class="error">
	A new scheduler service ${ data.schedulerService.scheduler.schedulerName } configuration has been 
	created, but failed to initialized.
	
	<pre>
	${ data.initFailedExceptionString }
	</pre>
	
	</div>
</c:when>
<c:otherwise>
	<div class="success">
	A new scheduler service 
	<a href="${ mainPath }/dashboard/switch-scheduler?configId=${ data.schedulerService.schedulerConfig.configId }">
	${ data.schedulerService.scheduler.schedulerName }</a> has been successfully created.
	</div>
</c:otherwise>
</c:choose>
<%@ include file="/WEB-INF/views/page-b.inc" %>