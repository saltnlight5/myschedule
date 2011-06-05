<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ include file="/WEB-INF/views/header.inc" %>

<h1>Create New Job</h1>
<p id="info">
Job ${ data.jobDetail.fullName } with Trigger ${ data.trigger.fullName } has been added successfully. 
Fire time: ${ data.fireTime }</p>

<%@ include file="/WEB-INF/views/footer.inc" %>