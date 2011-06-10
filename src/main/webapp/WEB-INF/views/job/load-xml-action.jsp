<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ include file="/WEB-INF/views/header.inc" %>
<%@ include file="/WEB-INF/views/job/submenu.inc" %>

<h1>Load Job Scheduling Data</h1>
<div class="success">Job loaded successfully.</div>

<table class="simple datalist">
	<tr>
		<td> LOADED JOBS </td>
		<td>
			<c:forEach items="${ data.loadedJobs }" var="item" varStatus="status">
			${ item }
			</c:forEach>
		</td>
	</tr>
	<tr>
		<td> LOADED TRIGGERS </td>
		<td>
			<c:forEach items="${ data.loadedTriggers }" var="item" varStatus="status">
			${ item }
			</c:forEach>
		</td>
	</tr>
</table>

<%@ include file="/WEB-INF/views/footer.inc" %>