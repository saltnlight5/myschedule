<%@ include file="/WEB-INF/jsp/main/page-a.inc" %>
<%@ include file="/WEB-INF/jsp/main/menu.inc" %>
<%@ include file="/WEB-INF/jsp/main/job/submenu.inc" %>

<div id="page-container">
<h1>Unscheduled Job</h1>

<div class="success">Trigger ${ data.trigger.fullName } has been removed.</div>
<c:if test="${ empty data.jobDetail }">
<div class="info">The JobDetails ${ data.trigger.jobName }.${ data.trigger.jobGroup } 
has no more trigger associated with it, so it was also removed by scheduler!</div>
</c:if>

</div> <!-- page-container -->
<%@ include file="/WEB-INF/jsp/main/page-b.inc" %>