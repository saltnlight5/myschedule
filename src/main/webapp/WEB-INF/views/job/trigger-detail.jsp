<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ include file="/WEB-INF/views/header.inc" %>
<%@ include file="/WEB-INF/views/job/submenu.inc" %>

<h1>Trigger Detail </h1>

<c:set var="trigger" value="${ data.firstTrigger }" scope="request"/>	
<%@ include file="/WEB-INF/views/job/trigger-detail.inc" %>

<h2>Trigger's Next ${ data.fireTimesCount } FireTimes</h2>
<table class="simple datalist">
	<c:forEach items="${ data.nextFireTimes }" var="time" varStatus="status">
	<tr>
		<td>${ status.index + 1 }</td>
		<td>${ time }</td>
	</tr>
	</c:forEach>
</table>

<%@ include file="/WEB-INF/views/footer.inc" %>
