<%@ include file="/WEB-INF/views/page-a.inc" %>
<%@ include file="/WEB-INF/views/menu.inc" %>
<%@ include file="/WEB-INF/views/scheduler/submenu.inc" %>

<div id="page-container">
<h1>Scheduler : ${ data.schedulerName }</h1>
<c:choose>
<c:when test="${ data.schedulerInStandbyMode }">
<div class="warning">Scheduler is in standby mode (all jobs are paused)!</div>
</c:when>
<c:otherwise>

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
	<c:forEach items="${ data.schedulerDetail }" var="item" varStatus="status">
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
<%@ include file="/WEB-INF/views/page-b.inc" %>