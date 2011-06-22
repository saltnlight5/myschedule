<%@ include file="/WEB-INF/views/page-a.inc" %>
<%@ include file="/WEB-INF/views/menu.inc" %>
<%@ include file="/WEB-INF/views/scheduler/submenu.inc" %>

<h1>Scheduler Summary</h1>
<div id="scheduler-summary">
<pre>
${ data.schedulerSummary }
</pre>
</div>

<%@ include file="/WEB-INF/views/page-b.inc" %>