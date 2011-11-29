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

	// use dataTables plugin
	$("#jobs").dataTable({
		"aaSorting": [[0, "asc"]],
		"iDisplayLength": 50,
		"bJQueryUI": true,
		"sPaginationType": "full_numbers"
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
		<h1>Jobs currently executing by scheduler</h1>
		<table id="jobs" class="display">
			<thead>
			<tr>
				<th> JOB </th>
				<th> TRIGGER </th>
				<th> RECOVERING </th>
				<th> SCHEDULE FIRE TIME </th>
				<th> FIRE TIME </th>
				<th> PREV FIRE TIME </th>
				<th> NEXT FIRE TIME </th>
			</tr>
			</thead>
			
			<tbody>
			<c:forEach items="${ data.jobExecutionContextList }" var="job" varStatus="loop">
			<tr>
				<td>${ job.jobDetail.fullName  }</td>
				<td>${ job.trigger.fullName  }</td>
				<td>${ job.recovering  }</td>
				<td><fmt:formatDate value="${ job.scheduledFireTime  }" pattern="MM/dd/yyyy HH:mm:ss"/></td>
				<td><fmt:formatDate value="${ job.fireTime  }" pattern="MM/dd/yyyy HH:mm:ss"/></td>
				<td><fmt:formatDate value="${ job.previousFireTime  }" pattern="MM/dd/yyyy HH:mm:ss"/></td>
				<td><fmt:formatDate value="${ job.nextFireTime  }" pattern="MM/dd/yyyy HH:mm:ss"/></td>
			</tr>
			</c:forEach>
			</tbody>
		</table> <!-- jobs -->
	</div>
</div>
<%@ include file="/WEB-INF/jsp/main/page-b.inc" %>