<%@ include file="/WEB-INF/jsp/page-a.inc" %>
<%@ include file="/WEB-INF/jsp/menu.inc" %>
<%@ include file="/WEB-INF/jsp/scheduler/submenu.inc" %>
<h1>Resumed All Triggers</h1>
<div class="success">
${ fn:length(data.triggers) } triggers were resumed in this scheduler. 
You may view these in the job list with trigger detail page.
</div>
<%@ include file="/WEB-INF/jsp/page-b.inc" %>