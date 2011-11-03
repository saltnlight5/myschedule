<%@ include file="/WEB-INF/jsp/main/page-a.inc" %>
<%@ include file="/WEB-INF/jsp/main/menu.inc" %>
<%@ include file="/WEB-INF/jsp/main/scheduler/submenu.inc" %>
<h1>Resumed All Triggers</h1>
<div class="success">
${ fn:length(data.triggers) } triggers were resumed in this scheduler. 
You may view these in the job list with trigger detail page.
</div>
<%@ include file="/WEB-INF/jsp/main/page-b.inc" %>