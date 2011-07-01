<%@ include file="/WEB-INF/views/page-a.inc" %>
<%@ include file="/WEB-INF/views/dashboard/menu.inc" %>
<%@ include file="/WEB-INF/views/dashboard/submenu.inc" %>
<h1>Delete Scheduler Service</h1>
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
<%@ include file="/WEB-INF/views/page-b.inc" %>