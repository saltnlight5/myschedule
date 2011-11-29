<%@ include file="/WEB-INF/jsp/main/page-a.inc" %>
<script>
$(document).ready(function() {
	$('#tabs').tabs({
	    select: function(event, ui) {
	        var url = $.data(ui.tab, 'load.tabs');
	        if( url ) {
	            location.href = url;
	            return false;
	        }
	        return true;
	    }
	});

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

<div id="tabs">
	<ul>
	<li><a href="#">Jobs with trigger</a></li>
	<li><a href="${ mainPath }/job/ajax/list-no-trigger-jobs">Jobs without trigger</a></li>
	<li><a href="${ mainPath }/job/ajax/list-executing-jobs">Currently Executing Jobs</a></li>
	</ul>
	
	<div id="tabs-1">
		<h1>Scheduler Calendars List (usually used for job exclusion)</h1>
		<table id="calendars-table" class="display">
			<thead>
			<tr>
				<th> INDEX </th>
				<th> NAME </th>
				<th> CLASS </th>
				<th> DESC </th>
				<th> INFO </th>
			</tr>
			</thead>
			
			<tbody>
			<c:forEach items="${ data.calendars }" var="item" varStatus="loop">
			<c:set var="item" value="${ item }" scope="request" />
			<tr>
				<td> ${ loop.index + 1 }</td>
				<td> ${ data.calendarNames[loop.index] } </td>
				<td> <%= request.getAttribute("item").getClass().getName() %> </td>
				<td> ${ item.description } </td>
				<td> ${ item } </td>
			</tr>
			</c:forEach>
			</tbody>
		</table> <!-- jobs-with-triggers -->
	</div>
</div>
<%@ include file="/WEB-INF/jsp/main/page-b.inc" %>