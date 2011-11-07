<%@ include file="/WEB-INF/jsp/main/page-a.inc" %>
<%@ include file="/WEB-INF/jsp/main/menu.inc" %>
<%@ include file="/WEB-INF/jsp/main/job/submenu.inc" %>

<div id="page-container">
<h1>Load Job Scheduling Data</h1>
<div class="success">Job loaded successfully.</div>

<table class="outlined datalist">
	<tr>
		<td> LOADED JOBS </td>
	</tr>
	<c:forEach items="${ data.xmlLoadedJobList.loadedJobs }" var="item">
	<tr>
		<td> ${ item } </td>
	</tr>
	</c:forEach>
</table>

<table class="outlined datalist">
	<tr>
		<td> LOADED TRIGGERS </td>
	</tr>
	<c:forEach items="${ data.xmlLoadedJobList.loadedTriggers }" var="item">
	<tr>
		<td> ${ item } </td>
	</tr>
	</c:forEach>
</table>

</div> <!-- page-container -->
<%@ include file="/WEB-INF/jsp/main/page-b.inc" %>