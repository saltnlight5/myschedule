<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ include file="/WEB-INF/views/header.inc" %>
<%@ include file="/WEB-INF/views/scheduler/submenu.inc" %>
<div class="content">

<h1>Scheduler Summary</h1>
<table class="center plaintext">
<tr><td>${ data.schedulerSummary }</td></tr>
</table>

</div><!-- div.content -->
<%@ include file="/WEB-INF/views/footer.inc" %>