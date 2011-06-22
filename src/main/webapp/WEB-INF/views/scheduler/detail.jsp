<%@ include file="/WEB-INF/views/page-a.inc" %>
<%@ include file="/WEB-INF/views/menu.inc" %>
<%@ include file="/WEB-INF/views/scheduler/submenu.inc" %>

<h1>Scheduler : ${ data.schedulerName }</h1>
<c:choose>
<c:when test="${ data.schedulerInStandbyMode }">
<div class="warning">Scheduler has not yet started!</div>
</c:when>
<c:otherwise>
<table class="outlined datalist">
	<c:forEach items="${ data.schedulerDetail }" var="item" varStatus="status">
	<tr>
		<td> ${ item.key }</td>
		<td> ${ item.value }</td>
	</tr>
	</c:forEach>
</table>
</c:otherwise>
</c:choose>

<%@ include file="/WEB-INF/views/page-b.inc" %>
