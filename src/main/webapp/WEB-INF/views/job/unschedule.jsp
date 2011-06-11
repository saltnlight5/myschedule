<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ include file="/WEB-INF/views/header.inc" %>
<%@ include file="/WEB-INF/views/job/submenu.inc" %>
<div class="content">

<h1>Unscheduled Job</h1>

<div class="fixedwidthpane centerpane">

<div class="success">Trigger ${ data.trigger.fullName } has been removed.</div>
<c:if test="${ empty data.jobDetail }">

<div class="info">The JobDetails ${ data.trigger.jobName }.${ data.trigger.jobGroup } 
has no more trigger associated with it, so it was also removed by scheduler!</div>
</c:if>

</div><!-- <div class="fixedwidthpane centerpane"> -->
</div><!-- div.content -->
<%@ include file="/WEB-INF/views/footer.inc" %>