<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ include file="/WEB-INF/views/header.inc" %>
<%@ include file="/WEB-INF/views/job/submenu.inc" %>

<h1>Scheduler Scripting</h1>
<div class="success">Your script has been run successfully.</div>

<c:if test="${ not empty data.webOutResult }">
<div class="info">
<span class="plaintext">${ data.webOutResult }</span>
</div>
</c:if>

<!-- Not to show scripting output by default.
<c:if test="${ not empty data.scriptingOutput }">
<div class="info">The last object it evaluated output is: 
<span class="plaintext">${ data.scriptingOutput }</span>
</div>
</c:if>
-->

<%@ include file="/WEB-INF/views/footer.inc" %>