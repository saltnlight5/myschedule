<%@ include file="/WEB-INF/jsp/main/page-a.inc" %>
<%@ include file="/WEB-INF/jsp/main/menu.inc" %>
<%@ include file="/WEB-INF/jsp/main/scheduler/submenu.inc" %>

<div id="page-container">
<h1>Scheduler Details for ${ data.schedulerName }</h1>
<c:choose>

<c:when test="${ data.isStandby }">
<div class="warning">Scheduler is in standby mode! You may start scheduler again to get out of standby mode.</div>
</c:when>

<c:otherwise>

<c:if test="${ data.isPaused }">
<div class="warning">Scheduler is in paused mode! All triggers are paused. You may resume scheduler again to un-pause it.</div>
</c:if>

<script>
$(document).ready(function() {
	// use dataTables plugin
	$("#name-value-table").dataTable({		
		"bPaginate": false,
		"bLengthChange": false,
		"bFilter": false,
		"bSort": false,
		"bInfo": false,
		"bAutoWidth": false
	});
});
</script>
<table id="name-value-table">
	<thead>
	<tr>
		<th></th>
		<th></th>
	</tr>
	</thead>
	<tbody>
	<tr>
		<td> jobCount </td>
		<td> ${ data.jobCount }</td>
	</tr>
	<c:forEach items="${ data.schedulerDetailMap }" var="item" varStatus="status">
	<tr>
		<td> ${ item.key }</td>
		<td> ${ item.value }</td>
	</tr>
	</c:forEach>
	</tbody>
</table>
</c:otherwise>

</c:choose>

</div> <!-- page-container -->
<%@ include file="/WEB-INF/jsp/main/page-b.inc" %>