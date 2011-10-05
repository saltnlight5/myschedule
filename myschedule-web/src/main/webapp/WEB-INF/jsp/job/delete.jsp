<%@ include file="/WEB-INF/jsp/page-a.inc" %>
<%@ include file="/WEB-INF/jsp/menu.inc" %>
<%@ include file="/WEB-INF/jsp/job/submenu.inc" %>

<div id="page-container">
<h1>Job Deleted</h1>

<div class="success">Job ${ data.jobDetail.fullName } has been deleted.</div>

<c:if test="${ not empty data.triggers }">
<div class="info">And these triggers are also removed.</div>
<ul>
<c:forEach items="${ data.triggers }" var="item">
	<li>Trigger: ${ item.fullName }</li> 
</c:forEach>
</ul>
</c:if>

</div> <!-- page-container -->
<%@ include file="/WEB-INF/jsp/page-b.inc" %>