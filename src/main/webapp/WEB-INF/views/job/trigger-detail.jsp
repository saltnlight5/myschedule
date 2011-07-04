<%@ include file="/WEB-INF/views/page-a.inc" %>
<%@ include file="/WEB-INF/views/menu.inc" %>
<%@ include file="/WEB-INF/views/job/submenu.inc" %>
<script>
$(document).ready(function() {
	// Use dataTables plugin
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
	
	// Confirm unschedule job
	$("#unschedule-confirm").hide()
	$("#unschedule").click(function() {
		var linkUrl = $(this).attr("href");
		$("#unschedule-confirm").dialog({
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

<div id="unschedule-confirm" title="Unschedule Trigger?">
	<p><span class="ui-icon ui-icon-alert" style="float:left; margin:0 7px 20px 0;"></span>
	Are you sure you want to remove this trigger and it's associated job from the scheduler?
	</p>
</div>

<div id="page-container">
<h1>Trigger Detail </h1>

<div>
<a id="unschedule" href="${ mainPath }/job/unschedule?triggerName=${ data.firstTrigger.name }&triggerGroup=${ data.firstTrigger.group }">
UNSCHEDULE THIS TRIGGER JOB</a>
</div>

<div>
The job class for this trigger is : ${ data.jobDetail.jobClass.name }. 
You may view full <a href="${ mainPath }/job/job-detail?jobName=${ data.jobDetail.name }&jobGroup=${ data.jobDetail.group }">JOB DETAIL</a> here.
</div>

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
