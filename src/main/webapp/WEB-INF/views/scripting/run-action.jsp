<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ include file="/WEB-INF/views/header.inc" %>
<%@ include file="/WEB-INF/views/job/submenu.inc" %>

<h1>Create New Job</h1>
<p id="info">Job ${ data.jobDetail.fullName } has been scheduled.</p>

<ul>
<c:forEach items="${ data.triggers }" var="item">
<li>Trigger: ${ item.fullName }</li> 
</c:forEach>
</ul>

<%@ include file="/WEB-INF/views/footer.inc" %>