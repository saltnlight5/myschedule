<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ include file="/WEB-INF/views/common.inc" %>

<div>
<a href="${ contextPath }/scheduler/jobs">Jobs</a>
</div>

<h1>Quartz Scheduler Dashboard</h1>
<table class="simple">
	<c:forEach items="${ data.schedulerInfo }" var="item" varStatus="status">
	<tr>
		<td> ${ item.key }</td>
		<td class="plaintext"> ${ item.value }</td>
	</tr>
	</c:forEach>
</table>

<%@ include file="/WEB-INF/views/footer.inc" %>