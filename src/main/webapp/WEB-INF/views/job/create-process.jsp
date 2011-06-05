<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ include file="/WEB-INF/views/header.inc" %>
<%@ include file="/WEB-INF/views/job/submenu.inc" %>

<h1>Create New Job</h1>
<p id="info">
Job ${ data.jobDetail.fullName } has been scheduled with
<c:forEach items="${ data.triggers }" var="item">
Trigger ${ item.fullName } 
</c:forEach>

</p>

<%@ include file="/WEB-INF/views/footer.inc" %>