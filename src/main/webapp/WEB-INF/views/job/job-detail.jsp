<%@ include file="/WEB-INF/views/page-a.inc" %>
<%@ include file="/WEB-INF/views/menu.inc" %>
<%@ include file="/WEB-INF/views/job/submenu.inc" %>

<script>
$(document).ready(function() {
	// use dataTables plugin
	$("#job-table").dataTable({		
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
<h1>Job Detail and Its Associated Triggers</h1>

<div>
<a href="${ mainPath }/job/delete?jobName=${ data.jobDetail.name }&jobGroup=${ data.jobDetail.group }">
DELETE THIS JOB AND ALL OF ITS TRIGGERS</a>
</div>

<h2>Job : ${ data.jobDetail.fullName }</h2>
<table id="job-table">
	<thead>
		<td> NAME </td>
		<td> VALUE </td>
	</thead>
	<tbody>
	<tr><td>Name</td><td>${ data.jobDetail.name }</td></tr>
	<tr><td>Group</td><td>${ data.jobDetail.group }</td></tr>
	<tr><td>Job Class</td><td>${ data.jobDetail.jobClass }</td></tr>
	<tr><td>Key</td><td>${ data.jobDetail.key }</td></tr>
	<tr><td>Description</td><td>${ data.jobDetail.description }</td></tr>
	<tr><td>Durability</td><td>${ data.jobDetail.durable }</td></tr>
	<tr><td>Stateful</td><td>${ data.jobDetail.stateful }</td></tr>
	<tr><td>Volatile</td><td>${ data.jobDetail.volatile }</td></tr>
	<tr><td>Should Recover</td><td>${ data.jobDetailShouldRecover }</td></tr>
	
	<c:forEach items="${ data.jobDetail.jobDataMap }" var="item">
	<tr><td>Job Data Map: ${ item.key }</td><td>${ item.value }</td></tr>
	</c:forEach>
	</tbody>
</table>

<c:forEach items="${ data.triggers }" var="trigger" varStatus="status">
	<c:set var="loopIndex" value="${ status.index }" scope="request"/>	
	<script>
	$(document).ready(function() {
		// use dataTables plugin
		$("#trigger-table-${ loopIndex }").dataTable({		
			"bPaginate": false,
			"bLengthChange": false,
			"bFilter": false,
			"bSort": false,
			"bInfo": false,
			"bAutoWidth": false
		});
	});
	</script>
	<%@ include file="/WEB-INF/views/job/trigger-detail.inc" %>
</c:forEach>

</div> <!-- page-container -->
<%@ include file="/WEB-INF/views/page-b.inc" %>