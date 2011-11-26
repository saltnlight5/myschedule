<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
	<script src="${ contextPath }/js/jquery-1.7.1.min.js"></script>
	<script>
$(document).ready(function() {
	$("#click-me").click(function(){
		$("#content h1").toggle();
	});
});
	</script>
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
		<li><a href="index">JQuery</a></li>
	</ul>
</div>
<div id="content">
	<h1>A Test for JQuery</h1>
	<a id="click-me" href="#">Click Me</a>
</div>
<div id="footer">
	<a href="http://api.jquery.com/category/core">JQuery Reference</a>
</div>
</body>
</html>