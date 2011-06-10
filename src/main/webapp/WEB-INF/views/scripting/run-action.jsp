<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ include file="/WEB-INF/views/header.inc" %>
<%@ include file="/WEB-INF/views/job/submenu.inc" %>

<h1>Scheduler Scripting</h1>
<p class="info">Your script has been run successfully.</p>

<c:if test="${ not empty data.scriptingOutput }">
<p class="info">The last object it evaluated output is: 
<span class="plaintext">${ data.scriptingOutput }</span>
</p>
</c:if>

<%@ include file="/WEB-INF/views/footer.inc" %>