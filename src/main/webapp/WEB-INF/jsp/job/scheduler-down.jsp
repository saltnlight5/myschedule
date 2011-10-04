<%@ include file="/WEB-INF/jsp/page-a.inc" %>
<%@ include file="/WEB-INF/jsp/menu.inc" %>

<div id="page-container">
<h1>Scheduler Is Down</h1>

<div class="error">
The scheduler service ${ sessionData.currentSchedulerName } has already been shutdown. Please go
to the settings menu and start it first.</div>

</div> <!-- page-container -->
<%@ include file="/WEB-INF/jsp/page-b.inc" %>