<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ include file="/WEB-INF/views/header.inc" %>
<%@ include file="/WEB-INF/views/job/submenu.inc" %>

<h1>Unscheduled Job</h1>
<p id="info">Trigger ${ data.trigger.fullName } has been removed.</p>
<c:if test="${ empty data.jobDetail }">
<p id="info">The JobDetails ${ data.trigger.jobName }.${ data.trigger.jobGroup } 
has no more trigger associated with it, so it was also removed by scheduler!</p>
</c:if>

<%@ include file="/WEB-INF/views/footer.inc" %>