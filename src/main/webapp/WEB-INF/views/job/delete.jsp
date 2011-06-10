<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ include file="/WEB-INF/views/header.inc" %>
<%@ include file="/WEB-INF/views/job/submenu.inc" %>

<h1>Job Deleted</h1>
<div class="success">Job ${ data.jobDetail.fullName } has been deleted.</div>

<ul>
<c:forEach items="${ data.triggers }" var="item">
<li>Trigger: ${ item.fullName }</li> 
</c:forEach>
</ul>

<%@ include file="/WEB-INF/views/footer.inc" %>