<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ include file="/WEB-INF/views/header.inc" %>

<h1>Next ${ data.nextFireTimesRequested } FireTimes </h1>
<p>Job ${ data.jobDetail.fullName } - Trigger ${ data.trigger.fullName }</p>

<table class="simple">
	<c:forEach items="${ data.nextFireTimes }" var="time" varStatus="status">
	<tr>
		<td> ${ status.index + 1 }</td>
		<td> ${ time }</td>
	</tr>
	</c:forEach>
</table>

<%@ include file="/WEB-INF/views/footer.inc" %>