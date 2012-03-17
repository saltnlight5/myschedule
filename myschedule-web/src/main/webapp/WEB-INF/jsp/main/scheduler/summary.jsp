<%@ include file="/WEB-INF/jsp/main/page-a.inc" %>
<%@ include file="/WEB-INF/jsp/main/menu.inc" %>
<%@ include file="/WEB-INF/jsp/main/scheduler/submenu.inc" %>

<h1>Scheduler Summary</h1>
<div id="scheduler-summary">
<pre>
${ data.schedulerSummary }
</pre>
</div>

<%@ include file="/WEB-INF/jsp/main/page-b.inc" %>