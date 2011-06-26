<%@ include file="/WEB-INF/views/page-a.inc" %>
<%@ include file="/WEB-INF/views/menu.inc" %>

<h1>List of All Schedulers</h1>
<table id="scheduler-list">
	<tbody>
	<c:forEach items="${ data.names }" var="name" varStatus="status">
	<c:set var="schedulerMetaData" value="${ data.schedulerMetaDataMap[name] }"/>
	<tr>
		<td><pre>${ schedulerMetaData.summary }</pre></td>
	</tr>
	</c:forEach>
	</tbody>
</table>

<%@ include file="/WEB-INF/views/page-b.inc" %>