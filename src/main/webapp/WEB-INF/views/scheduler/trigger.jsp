<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ include file="/WEB-INF/views/common.inc" %>

<div>
<a href="${ contextPath }/scheduler/dashboard">Dashboard</a>
<a href="${ contextPath }/scheduler/jobs">Jobs</a>
</div>

<h1>Job ${ data.jobDetail.fullName } - Trigger ${ data.trigger.fullName }</h1>

<h2>Next ${ data.nextFireTimesRequested } FireTimes </h2>
<table class="simple">
	<c:forEach items="${ data.nextFireTimes }" var="time" varStatus="status">
	<tr>
		<td> ${ status.index + 1 }</td>
		<td> ${ time }</td>
	</tr>
	</c:forEach>
</table>

<%@ include file="/WEB-INF/views/footer.inc" %>