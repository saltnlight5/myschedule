<%@ include file="/WEB-INF/views/page-a.inc" %>
<%@ include file="/WEB-INF/views/menu.inc" %>
<%@ include file="/WEB-INF/views/job/submenu.inc" %>
<script>
$(document).ready(function() {
	// use dataTables plugin
	$("#noTriggerJobDetailsTable-datatable").dataTable({
		"aaSorting": [[0, "asc"], [1, "asc"]],
		"iDisplayLength": 50,
		"bJQueryUI": true,
		"sPaginationType": "full_numbers"
	});
});
</script>

<div id="page-container">
<h1>Jobs without assigned trigger</h1>
<table id="noTriggerJobDetailsTable-datatable" cellpadding="0" cellspacing="0" border="0" class="display">
	<thead>
	<tr>
		<td> JOB </td>
		<td> JOB CLASS </td>
		<td> ACTIONS </td>
	</tr>
	</thead>
	
	<tbody>
	<c:forEach items="${ data.noTriggerJobDetails }" var="jobDetail">
	<tr>
		<td><a href="${ mainPath }/job/job-detail?jobName=${ jobDetail.name }&jobGroup=${ jobDetail.group }">${ jobDetail.fullName }</a></td>
		<td>${ jobDetail.jobClass.name }</td>
		<td>
			<a href="${ mainPath }/job/delete?jobName=${ jobDetail.name }&jobGroup=${ jobDetail.group }">Delete</a>
		</td>
	</tr>
	</c:forEach>
	</tbody>
</table> <!-- noTriggerJobDetailsTable-datatable -->

</div> <!-- page-container -->
<%@ include file="/WEB-INF/views/page-b.inc" %>