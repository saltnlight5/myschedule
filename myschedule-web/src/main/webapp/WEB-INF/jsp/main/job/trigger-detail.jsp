<%@ include file="/WEB-INF/jsp/main/page-a.inc" %>
<%@ include file="/WEB-INF/jsp/main/menu.inc" %>
<%@ include file="/WEB-INF/jsp/main/job/submenu.inc" %>
<script>
$(document).ready(function() {
	$(".trigger-table").dataTable({		
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
	$("#unschedule-confirm").hide();
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

	// Highlight excluded calendar date
	$(".excluded-date").css('background-color', 'RED');
});
</script>

<div id="unschedule-confirm" title="Unschedule Trigger?">
	<p><span class="ui-icon ui-icon-alert" style="float:left; margin:0 7px 20px 0;"></span>
	Are you sure you want to remove this trigger and it's associated job from the scheduler?
	</p>
</div>

<div id="page-container">
<h1>Trigger Detail </h1>

<c:set var="triggerWrapper" value="${ data.triggerWrapper }"/>

<div>
<a id="unschedule" href="${ mainPath }/job/unschedule?triggerName=${ triggerWrapper.trigger.key.name }&triggerGroup=${ triggerWrapper.trigger.key.group }">
UNSCHEDULE THIS TRIGGER JOB</a>
</div>

<div>
The job class for this trigger is : ${ triggerWrapper.jobClassName }. 
You may view full <a href="${ mainPath }/job/job-detail?jobName=${ triggerWrapper.trigger.jobKey.name }&jobGroup=${ triggerWrapper.trigger.jobKey.group }">JOB DETAIL</a> here.
</div>

<%-- We need triggerWrapper variable set at request level before include. --%>
<%@ include file="/WEB-INF/jsp/main/job/trigger-detail.inc" %>

<h2>Trigger's Next ${ data.fireTimesCount } FireTimes</h2>

<table id="next-fire-times">
	<thead>
	<tr>
		<th> INDEX </th>
		<th> NEXT FIRE TIME </th>
		<c:if test="${ not empty triggerWrapper.trigger.calendarName }">
			<th> EXCLUDE BY CALENDAR </th>
		</c:if>
	</tr>
	</thead>
	<tbody>
	<c:forEach items="${ triggerWrapper.nextFireTimes }" var="nextFireTimePair" varStatus="loop">
	<tr>
		<td>${ loop.index + 1 }</td>
		<td>${ nextFireTimePair.item1 }</td>
		<c:if test="${ not empty triggerWrapper.trigger.calendarName }">
		<c:choose><c:when test="${ nextFireTimePair.item2 != 'No' }">
			<td class="excluded-date">${ nextFireTimePair.item2 }</td>
		</c:when><c:otherwise>
			<td>${ nextFireTimePair.item2 }</td>
		</c:otherwise></c:choose>
		</c:if>
	</tr>
	</c:forEach>
	</tbody>
</table>

</div> <!-- page-container -->
<%@ include file="/WEB-INF/jsp/main/page-b.inc" %>
