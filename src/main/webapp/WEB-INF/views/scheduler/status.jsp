<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ include file="/WEB-INF/views/header.inc" %>

<div id="submenu">	
<ul>
<c:choose>
<c:when test="${ data.schedulerInStandbyMode }">
	<li><a href="${ actionPath }/scheduler/start">Start Scheduler (Resume)</a></li>
</c:when>
<c:otherwise>
	<li><a href="${ actionPath }/scheduler/standby">Standby Scheduler (Pause)</a></li>
</c:otherwise>
</c:choose>
</ul>
</div>

<h1>Scheduler Status for ${ data.schedulerName }</h1>

<c:choose>
<c:when test="${ data.schedulerInStandbyMode }">
<p id="warning">
Scheduler has not yet started!
</p>
</c:when>
<c:otherwise>
<table class="simple">
	<c:forEach items="${ data.schedulerInfo }" var="item" varStatus="status">
	<tr>
		<td> ${ item.key }</td>
		<td class="plaintext"> ${ item.value }</td>
	</tr>
	</c:forEach>
</table>
</c:otherwise>
</c:choose>

<%@ include file="/WEB-INF/views/footer.inc" %>