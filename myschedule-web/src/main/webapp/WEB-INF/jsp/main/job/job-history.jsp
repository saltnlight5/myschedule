<%@ include file="/WEB-INF/jsp/main/page-a.inc" %>
<%@ include file="/WEB-INF/jsp/main/menu.inc" %>
<%@ include file="/WEB-INF/jsp/main/job/submenu.inc" %>
<script>
$(document).ready(function() {
	// Use dataTables plugin
	$("#jobHitory").dataTable({
		"aaSorting": [[ 5, "desc" ]],
		"iDisplayLength": 50,
		"bJQueryUI": true,
		"sPaginationType": "full_numbers"
	});	
});
</script>

<c:choose><c:when test="${ data.jobHistoryPluginNotFound }">
	<div class="warning">No plugin found to support job history report. Please configure 
	<code>myschedule.quartz.extra.JdbcSchedulerHistoryPlugin</code> plugin in scheduler properties to enable 
	this feature. See <a href="http://code.google.com/p/myschedule/wiki/QuartzExtraUserGuide">User Guide</a> for 
	more.</div>
</c:when><c:otherwise>

<h1>Job History Report</h1>
<table id="jobHitory" class="display" style="font-size: 12px;">
	<thead>
	<tr>
		<th> INDEX </th>
		<th> HOST IP/NAME </th>
		<th> SCHEDULE NAME </th>
		<th> EVENT TYPE </th>
		<th> EVENT NAME </th>
		<th> EVENT TIME </th>
		<th> INFO1 </th>
		<th> INFO2 </th>
		<th> INFO3 </th>
		<th> INFO4 </th>
		<th> INFO5 </th>
	</tr>
	</thead>
	<tbody>
	<c:forEach items="${ data.jobHistoryTable }" var="jobHistory" varStatus="loop">
	<tr>
		<td>${ loop.index }</td>
		<td>${ jobHistory[0] } / ${ jobHistory[1] }</td>
		<td>${ jobHistory[2] }</td>
		<td>${ jobHistory[3] }</td>
		<td>${ jobHistory[4] }</td>
		<td><fmt:formatDate value="${ jobHistory[5] }" pattern="MM/dd/yy HH:mm:ss"/></td>
		<td>${ jobHistory[6] }</td>
		<td>${ jobHistory[7] }</td>
		<td>${ jobHistory[8] }</td>
		<td>${ jobHistory[9] }</td>
		<td>${ jobHistory[10] }</td>
	</tr>
	</c:forEach>
	</tbody>
</table>

</c:otherwise></c:choose>
<%@ include file="/WEB-INF/jsp/main/page-b.inc" %>