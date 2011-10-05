<%@ include file="/WEB-INF/jsp/page-a.inc" %>
<%@ include file="/WEB-INF/jsp/dashboard/menu.inc" %>
<%@ include file="/WEB-INF/jsp/dashboard/submenu.inc" %>
<h1>Delete Scheduler Service</h1>
<c:choose>
<c:when test="${ fn:length(data.schedulerNamesMap) == 0 }">
	<p>There is no scheduler service to modify. Please create one first.</p>
</c:when>
<c:otherwise>
	<form action="${ mainPath }/dashboard/modify" method="get">
	<span>SchedulerService:</span> 
	<select name="configId">
	<c:forEach items="${ data.schedulerNamesMap }" var="entry">
		<c:set var="selectedAttr" value=""/>
		<c:if test="${ entry.key == sessionData.currentSchedulerConfigId }">
			<c:set var="selectedAttr" value="selected=\"selected\""/>
		</c:if>
		<option value="${ entry.key }" ${ selectedAttr }>${ entry.value }</option>
	</c:forEach>
	</select>
	<input type="submit" value="Modify"/>
	</form>
</c:otherwise>
</c:choose>
<%@ include file="/WEB-INF/jsp/page-b.inc" %>