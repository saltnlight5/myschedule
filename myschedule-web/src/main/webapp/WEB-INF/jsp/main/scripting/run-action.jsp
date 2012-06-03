<%@ include file="/WEB-INF/jsp/main/page-a.inc" %>
<%@ include file="/WEB-INF/jsp/main/menu.inc" %>

<div class="page-container">
<h1>Scheduler Scripting</h1>
<div class="success">Your script has been run successfully.</div>

<c:if test="${ not empty data.webOutResult }">
<div class="info">
<pre>${ data.webOutResult }</pre>
</div>
</c:if>

</div><!-- page-container -->
<%@ include file="/WEB-INF/jsp/main/footer.inc" %>