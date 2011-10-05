<%@ include file="/WEB-INF/jsp/page-a.inc" %>
<%@ include file="/WEB-INF/jsp/menu.inc" %>
<%@ include file="/WEB-INF/jsp/scheduler/submenu.inc" %>

<h1>Scheduler Summary</h1>
<div id="scheduler-summary">
<pre>
${ data.schedulerSummary }
</pre>
</div>

<%@ include file="/WEB-INF/jsp/page-b.inc" %>