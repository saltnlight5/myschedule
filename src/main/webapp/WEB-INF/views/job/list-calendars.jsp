<%@ include file="/WEB-INF/views/page-a.inc" %>
<%@ include file="/WEB-INF/views/menu.inc" %>
<%@ include file="/WEB-INF/views/job/submenu.inc" %>
<script>
$(document).ready(function() {
	// Use dataTables plugin
	$("#calendars-table").dataTable({		
		"bPaginate": false,
		"bLengthChange": false,
		"bFilter": false,
		"bSort": false,
		"bInfo": false,
		"bAutoWidth": false
	});
});
</script>

<h1>Scheduler Calendars List (usually used for job exclusion)</h1>
<table id="calendars-table" class="display">
	<thead>
	<tr>
		<th> COUNT </th>
		<th> NAME </th>
		<th> CLASS </th>
		<th> DESC </th>
		<th> INFO </th>
	</tr>
	</thead>
	
	<tbody>
	<c:forEach items="${ data.calendars }" var="item" varStatus="loop">
	<tr>
		<td> ${ loop.index + 1 }</td>
		<td> ${ data.calendarNames[loop.index] } </td>
		<td> ${ item.class.name } </td>
		<td> ${ item.description } </td>
		<td> ${ item } </td>
	</tr>
	</c:forEach>
	</tbody>
</table> <!-- jobs-with-triggers -->
<%@ include file="/WEB-INF/views/page-b.inc" %>