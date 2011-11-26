<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
	<style>
#menu li {
	display: inline;
	list-style-type: none;
}
#footer {
	margin-top: 30px;
	border-top: 1px solid black;
}
	</style>
	<title>Debug</title>
</head>
<body>
<div>
	<ul id="menu">
		<li><a href="${ debugPath }">Debug Home</a></li>
		<li><a href="index">CSS Demo</a></li>
	</ul>
</div>
<div id="content">
	<h1>A Test for CSS</h1>
	<p>Hello World</p>
</div>
<div id="footer">
	<a href="http://www.w3schools.com/cssref">CSS Reference</a>
</div>
</body>
</html>