<%@ include file="/WEB-INF/jsp/main/page-a.inc" %>
<%@ include file="/WEB-INF/jsp/main/menu.inc" %>

<div id="page-container">
<h1>Scheduler Is Down</h1>

<div class="error">
The scheduler service ${ sessionData.currentSchedulerName } has already been shutdown. Please go
to the settings menu and start it first.</div>

</div> <!-- page-container -->
<%@ include file="/WEB-INF/jsp/main/page-b.inc" %>