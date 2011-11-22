<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
	<script src="${ contextPath }/js/jquery-1.7.1.min.js"></script>
	<link rel="stylesheet" type="text/css" href="${ debugServletPath }/debug-css"/>
	<title>Debug</title>
</head>
<body>
<div>
	<ul id="menu">
		<li><a href="${ debugServletPath }">Debug Home</a></li>
	</ul>
</div>
<div>
	<code>${ actionDir }/index</code>
	<ul>
	<c:forEach items="${ dirNames }" var="name">
		<li class="dir"><a href="${ debugServletPath }${ actionDir }/${ name }/index">${ name }</a></li>
	</c:forEach>
	<c:forEach items="${ fileNames }" var="name">
		<li class="file"><a href="${ debugServletPath }${ actionDir }/${ name }">${ name }</a></li>
	</c:forEach>
	</ul>
</div>
</body>
</html>