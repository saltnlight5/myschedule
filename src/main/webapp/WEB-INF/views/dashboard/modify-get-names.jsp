<%@ include file="/WEB-INF/views/page-a.inc" %>
<%@ include file="/WEB-INF/views/dashboard/menu.inc" %>
<%@ include file="/WEB-INF/views/dashboard/submenu.inc" %>
<h1>Delete Scheduler Service</h1>
<c:choose>
<c:when test="${ fn:length(data.schedulerNames) == 0 }">
	<p>There is no scheduler service to modify. Please create one first.</p>
</c:when>
<c:otherwise>
	<form action="${ mainPath }/dashboard/modify" method="get">
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
	<input type="submit" value="Modify"/>
	</form>
</c:otherwise>
</c:choose>
<%@ include file="/WEB-INF/views/page-b.inc" %>