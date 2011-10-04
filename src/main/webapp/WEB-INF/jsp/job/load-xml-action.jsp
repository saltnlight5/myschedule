<%@ include file="/WEB-INF/jsp/page-a.inc" %>
<%@ include file="/WEB-INF/jsp/menu.inc" %>
<%@ include file="/WEB-INF/jsp/job/submenu.inc" %>

<div id="page-container">
<h1>Load Job Scheduling Data</h1>
<div class="success">Job loaded successfully.</div>

<table class="outlined datalist">
	<tr>
		<td> LOADED JOBS </td>
		<td><c:forEach items="${ data.loadedJobs }" var="item" varStatus="status">${ item }
</c:forEach>
		</td>
	</tr>
	<tr>
		<td> LOADED TRIGGERS </td>
		<td><c:forEach items="${ data.loadedTriggers }" var="item" varStatus="status">${ item }
</c:forEach>
		</td>
	</tr>
</table>

</div> <!-- page-container -->
<%@ include file="/WEB-INF/jsp/page-b.inc" %>