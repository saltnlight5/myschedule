<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
	<link rel="stylesheet" type="text/css" href="${ debugPath }/debug.css"/>
	<title>Debug</title>
</head>
<body>
<div id="menu">
	<ul>
		<li><a href="${ debugPath }">Debug Home</a></li>
	</ul>
</div>
<div class="dir-listing">
	<code>${ actionDir }/index</code>
	<ul>
	<c:forEach items="${ dirNames }" var="name">
		<li class="dir"><a href="${ debugPath }${ actionDir }/${ name }/index">${ name }</a></li>
	</c:forEach>
	<c:forEach items="${ fileNames }" var="name">
		<li class="file"><a href="${ debugPath }${ actionDir }/${ name }">${ name }</a></li>
	</c:forEach>
	</ul>
</div>
</body>
</html>