<%@ include file="/WEB-INF/views/page-a.inc" %>
<%@ include file="/WEB-INF/views/menu.inc" %>
<%@ include file="/WEB-INF/views/job/submenu.inc" %>
<script>
$(document).ready(function() {
	// use dataTables plugin
	$("#triggers-0").dataTable({		
		"bPaginate": false,
		"bLengthChange": false,
		"bFilter": false,
		"bSort": false,
		"bInfo": false,
		"bAutoWidth": false
	});
	$("#next-fire-times").dataTable({		
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
<h1>Trigger Detail </h1>

<a href="${ mainPath }/job/unschedule?triggerName=${ data.firstTrigger.name }&triggerGroup=${ data.firstTrigger.group }"> UNSCHEDULE THIS TRIGGER JOB</a>

<p>
The job class for this trigger is : ${ data.jobDetail.jobClass.name }. 
View full <a href="${ mainPath }/job/job-detail?jobName=${ data.jobDetail.name }&jobGroup=${ data.jobDetail.group }">JOB DETAIL</a> here.
</p>

<c:set var="loopIndex" value="0" scope="request"/>	
<c:set var="trigger" value="${ data.firstTrigger }" scope="request"/>	
<%@ include file="/WEB-INF/views/job/trigger-detail.inc" %>

<h2>Trigger's Next ${ data.fireTimesCount } FireTimes</h2>

<table id="next-fire-times">
	<thead>
	<tr>
		<th> INDEX </th>
		<th> NEXT FIRE TIME </th>
	</tr>
	</thead>
	<tbody>
	<c:forEach items="${ data.nextFireTimes }" var="time" varStatus="status">
	<tr>
		<td>${ status.index + 1 }</td>
		<td>${ time }</td>
	</tr>
	</c:forEach>
	</tbody>
</table>

</div> <!-- page-container -->
<%@ include file="/WEB-INF/views/page-b.inc" %>
