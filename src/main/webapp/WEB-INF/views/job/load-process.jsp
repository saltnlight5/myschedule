<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ include file="/WEB-INF/views/header.inc" %>

<h1>Load Job Scheduling Data</h1>
<p id="info">Job loaded successfully.</p>

<table class="simple">
	<tr>
		<td> LOADED JOBS </td>
		<td class="plaintext">
			<c:forEach items="${ data.loadedJobs }" var="item" varStatus="status">
			${ item }
			</c:forEach>
		</td>
	</tr>
	<tr>
		<td> LOADED TRIGGERS </td>
		<td class="plaintext">
			<c:forEach items="${ data.loadedTriggers }" var="item" varStatus="status">
			${ item }
			</c:forEach>
		</td>
	</tr>
</table>

<%@ include file="/WEB-INF/views/footer.inc" %>