<%@ include file="/WEB-INF/jsp/main/page-a.inc" %>
<script>
$(document).ready(function() {
	$("#landing").button();
});
</script>
<div>
<a id="landing" href="${mainPath}/dashboard/landing">Dashboard Landing Page</a>
</div>
<%@ include file="/WEB-INF/jsp/main/page-b.inc" %>