<%@ include file="/WEB-INF/jsp/main/page-a.inc" %>
<%@ include file="/WEB-INF/jsp/main/menu.inc" %>
<%@ include file="/WEB-INF/jsp/main/scheduler/submenu.inc" %>

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
		<th>INDEX</th>
		<th>LISTENER CLASS</th>
	</tr>
	</thead>
	<tbody>
	<c:forEach items="${ data.schedulerListeners }" var="item" varStatus="loop">
	<c:set var="item" value="${ item }" scope="request" />
	<tr>
		<td> ${ loop.index + 1 }</td>
		<td> <%= request.getAttribute("item").getClass().getName() %> </td>
	</tr>
	</c:forEach>
	</tbody>
</table>

<h1>Job Listeners</h1>
<table class="listeners-table">
	<thead>
	<tr>
		<th>INDEX</th>
		<th>NAME</th>
		<th>LISTENER CLASS</th>
	</tr>
	</thead>
	<tbody>
	<c:forEach items="${ data.jobListeners }" var="item" varStatus="loop">
	<c:set var="item" value="${ item }" scope="request" />
	<tr>
		<td> ${ loop.index + 1 }</td>
		<td> ${ item.name }</td>
		<td> <%= request.getAttribute("item").getClass().getName() %> </td>
	</tr>
	</c:forEach>
	</tbody>
</table>

<h1>Trigger Listeners</h1>
<table class="listeners-table">
	<thead>
	<tr>
		<th>INDEX</th>
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
<%@ include file="/WEB-INF/jsp/main/page-b.inc" %>