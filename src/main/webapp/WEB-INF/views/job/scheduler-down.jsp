<%@ include file="/WEB-INF/views/page-a.inc" %>
<%@ include file="/WEB-INF/views/menu.inc" %>

<div id="page-container">
<h1>Scheduler Is Down</h1>

<div class="error">
The scheduler service ${ sessionData.currentSchedulerName } has already been shutdown. Please go
to the settings menu and start it first.</div>

</div> <!-- page-container -->
<%@ include file="/WEB-INF/views/page-b.inc" %>