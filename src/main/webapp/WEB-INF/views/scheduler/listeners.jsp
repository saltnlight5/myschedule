<%@ include file="/WEB-INF/views/page-a.inc" %>
<%@ include file="/WEB-INF/views/menu.inc" %>
<%@ include file="/WEB-INF/views/scheduler/submenu.inc" %>

<script>
$(document).ready(function() {
	// use dataTables plugin
	$(".listeners-table").dataTable({		
		"bPaginate": false,
		"bLengthChange": false,
		"bFilter": false,
		"bSort": false,
		"bInfo": false,
		"bAutoWidth": false
	});
});
</script>

<div id="page-container">

<h1>Scheduler Listeners</h1>
<table class="listeners-table">
	<thead>
	<tr>
		<th>COUNT</th>
		<th>LISTENER CLASS</th>
	</tr>
	</thead>
	<tbody>
	<c:forEach items="${ data.schedulerListeners }" var="item" varStatus="loop">
	<tr>
		<td> ${ loop.index + 1 }</td>
		<td> ${ item.class.name }</td>
	</tr>
	</c:forEach>
	</tbody>
</table>

<h1>Job Listeners</h1>
<table class="listeners-table">
	<thead>
	<tr>
		<th>COUNT</th>
		<th>NAME</th>
		<th>LISTENER CLASS</th>
	</tr>
	</thead>
	<tbody>
	<c:forEach items="${ data.jobListeners }" var="item" varStatus="loop">
	<tr>
		<td> ${ loop.index + 1 }</td>
		<td> ${ item.name }</td>
		<td> ${ item.class.name }</td>
	</tr>
	</c:forEach>
	</tbody>
</table>

<h1>Trigger Listeners</h1>
<table class="listeners-table">
	<thead>
	<tr>
		<th>COUNT</th>
		<th>NAME</th>
		<th>LISTENER CLASS</th>
	</tr>
	</thead>
	<tbody>
	<c:forEach items="${ data.triggerListeners }" var="item" varStatus="loop">
	<tr>
		<td> ${ loop.index + 1 }</td>
		<td> ${ item.name }</td>
		<td> ${ item.class.name }</td>
	</tr>
	</c:forEach>
	</tbody>
</table>

</div> <!-- page-container -->
<%@ include file="/WEB-INF/views/page-b.inc" %>