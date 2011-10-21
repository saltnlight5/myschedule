<%@ page import="org.apache.commons.lang.exception.*" %>
<%@ include file="/WEB-INF/jsp/main/page-a.inc" %>
<%@ include file="/WEB-INF/jsp/main/menu.inc" %>
<script>
$(document).ready(function() {
	// Hide the help page on startup.
	$("#details").hide();
	
	// Reponse to show page link
	$('#show-details').click(function() {
		$("#details").toggle('slow');
	});
});
</script>
<div id="page-container">
<h1>Application Error</h1>

<div class="error">
<p>An application error has occurred during your request. Please report this to the administrator.</p>

<p>Status Code: ${ requestScope['javax.servlet.error.status_code'] }</p>

<p>Message: ${ requestScope['javax.servlet.error.message'] }</p>

<p>Error Type: ${ requestScope['javax.servlet.error.exception_type'] }</p>

<p><a id="show-details">Show more details</a></p>
</div>

<div id="details">
<pre>
<%= ExceptionUtils.getFullStackTrace((Throwable)request.getAttribute("javax.servlet.error.exception")) %>
</pre>
</div>

</div> <!-- page-container -->
<%@ include file="/WEB-INF/jsp/main/page-b.inc" %>