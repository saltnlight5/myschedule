<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
	<style>
#menu li {
	display: inline;
	list-style-type: none;
}
	</style>
	<title>Debug</title>
</head>
<body>
<div>
	<ul id="menu">
		<li><a href="${ debugServletPath }">Debug Home</a></li>
		<li><a href="${ debugServletPath }/css">CSS</a></li>
	</ul>
	<h1>A Test for CSS</h1>
	<p>Hello World</p>
</div>
</body>
</html>