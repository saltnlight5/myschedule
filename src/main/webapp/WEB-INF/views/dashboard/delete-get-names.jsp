<%@ include file="/WEB-INF/views/page-a.inc" %>
<%@ include file="/WEB-INF/views/dashboard/menu.inc" %>
<%@ include file="/WEB-INF/views/dashboard/submenu.inc" %>
<h1>Delete Scheduler Service</h1>
<c:choose>
<c:when test="${ fn:length(data.schedulerNames) == 0 }">
	<p>There is no scheduler service to delete. Please create one first.</p>
</c:when>
<c:otherwise>
	<div class="warning">
		Only configuration properties file will be delete. If the scheduler is configured
		with database persistence, no data from the database will be removed! If you must
		also want remove database data, then try using the Scripting tool before removing
		deleting this configuration. 
	</div>
	<form action="${ mainPath }/dashboard/delete-action" method="post">
	<span>SchedulerService:</span> 
	<select name="name">
	<c:forEach items="${ data.schedulerNames }" var="name">
		<c:set var="selectedAttr" value=""/>
		<c:if test="${ name == sessionData.currentSchedulerName }">
			<c:set var="selectedAttr" value="selected=\"selected\""/>
		</c:if>
		<option value="${ name }" ${ selectedAttr }>${ name }</option>
	</c:forEach>
	</select>
	<input type="submit" value="Delete"/>
	</form>
</c:otherwise>
</c:choose>
<%@ include file="/WEB-INF/views/page-b.inc" %>