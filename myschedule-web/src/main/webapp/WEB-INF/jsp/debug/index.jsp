<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head><title>MySchedule - Debug</title></head>
<body>
<div>
<ul>
	<li><a href="${ debugActionPath }">Back to Debug Home</a></li>
</ul>
</div>
<div>
<code>${ actionDir }index</code>
<ul>
<c:forEach items="${ dirNames }" var="name">
	<li class="dir"><a href="${ debugActionPath }/${ name }/index">${ name }</a></li>
</c:forEach>
<c:forEach items="${ fileNames }" var="name">
	<li class="file"><a href="${ debugActionPath }/${ name }">${ name }</a></li>
</c:forEach>
</ul>
</div>
</body>
</html>