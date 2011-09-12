<%@ include file="/WEB-INF/views/page-a.inc" %>
<%@ include file="/WEB-INF/views/menu.inc" %>
<%@ include file="/WEB-INF/views/scheduler/submenu.inc" %>
<h1>Resumed All Triggers</h1>
<div class="success">
${ fn:length(data.triggers) } triggers were resumed in this scheduler. 
You may view these in the job list with trigger detail page.
</div>
<%@ include file="/WEB-INF/views/page-b.inc" %>