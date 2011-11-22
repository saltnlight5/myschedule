<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
	<script src="${contextPath}/js/jquery-1.6.1.js"></script>
	<link rel="stylesheet" type="text/css" href="${ debugActionPath }/debug-css" />
	<title>MySchedule - Debug</title>
</head>
<body>
<div>
	<ul id="menu">
		<li><a href="${ debugActionPath }">Debug Home</a></li>
	</ul>
</div>
<div>
	<code>${ actionDir }index</code>
	<ul>
	<c:forEach items="${ dirNames }" var="name">
		<li class="dir"><a href="${ debugActionPath }/${actionDir}/${ name }/index">${ name }</a></li>
	</c:forEach>
	<c:forEach items="${ fileNames }" var="name">
		<li class="file"><a href="${ debugActionPath }/${actionDir}/${ name }">${ name }</a></li>
	</c:forEach>
	</ul>
</div>
</body>
</html>