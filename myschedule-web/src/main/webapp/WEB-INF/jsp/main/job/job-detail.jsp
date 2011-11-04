<%@ include file="/WEB-INF/jsp/main/page-a.inc" %>
<%@ include file="/WEB-INF/jsp/main/menu.inc" %>
<%@ include file="/WEB-INF/jsp/main/job/submenu.inc" %>
<script>
$(document).ready(function() {
	// Use dataTables plugin
	$("#jobs").dataTable({		
		"bPaginate": false,
		"bLengthChange": false,
		"bFilter": false,
		"bSort": false,
		"bInfo": false,
		"bAutoWidth": false
	});

	// Confirm unschedule job
	$("#delete-confirm").hide();
	$("#delete").click(function() {
		var linkUrl = $(this).attr("href");
		$("#delete-confirm").dialog({
			resizable: false,
			height:200,
			width:400,
			modal: true,
			buttons: {
				"Yes": function() {
					window.location.href=linkUrl;
				},
				Cancel: function() {
					$(this).dialog("close");
				}
			}
		});
		return false;
	});
});
</script>

<div id="delete-confirm" title="Delete Job?">
	<p><span class="ui-icon ui-icon-alert" style="float:left; margin:0 7px 20px 0;"></span>
	Are you sure you want to remove this job and all of its associated triggers from the scheduler?
	</p>
</div>

<div id="page-container">
<h1>Job Detail and Its Associated Triggers</h1>

<div>
<a id="delete" href="${ mainPath }/job/delete?jobName=${ data.jobDetail.name }&jobGroup=${ data.jobDetail.group }">
DELETE THIS JOB AND ALL OF ITS TRIGGERS</a>
</div>

<h2>Job : ${ data.jobDetail.name }.${ data.jobDetail.group }</h2>
<table id="jobs">
	<thead>
	<tr>
		<th></th>
		<th></th>
	</tr>
	</thead>
	<tbody>
	<tr><td>Name.Group</td><td>${ data.jobDetail.name }.${ data.jobDetail.group }</td></tr>
	<tr><td>Job Class</td><td>${ data.jobDetail.jobClass }</td></tr>
	<tr><td>Description</td><td>${ data.jobDetail.description }</td></tr>
	<tr><td>Durable</td><td>${ data.jobDetail.durable }</td></tr>
	<tr><td>isVolatile</td><td>${ data.jobDetail.volatile }</td></tr>
	<c:set var="jobDetailVar" value="${ data.jobDetail }" scope="request"/>
	<tr><td>Request Recovery</td><td><%= ((org.quartz.JobDetail)request.getAttribute("jobDetailVar")).requestsRecovery() %></td></tr>
	
	<c:forEach items="${ data.jobDetail.jobDataMap }" var="item">
	<tr><td>Job Data Map: ${ item.key }</td><td>${ item.value }</td></tr>
	</c:forEach>
	</tbody>
</table>

<c:forEach items="${ data.triggers }" var="trigger" varStatus="status">
	<c:set var="trigger" value="${ trigger }" scope="request"/>
	<c:set var="triggerStatus" value="${ data.triggerStatusList[status.index] }" scope="request"/>
	<c:set var="loopIndex" value="${ status.index }" scope="request"/>
	<script>
	$(document).ready(function() {
		// use dataTables plugin
		$("#triggers-${ loopIndex }").dataTable({		
			"bPaginate": false,
			"bLengthChange": false,
			"bFilter": false,
			"bSort": false,
			"bInfo": false,
			"bAutoWidth": false
		});
	});
	</script>
	<%@ include file="/WEB-INF/jsp/main/job/trigger-detail.inc" %>
</c:forEach>

</div> <!-- page-container -->
<%@ include file="/WEB-INF/jsp/main/page-b.inc" %>