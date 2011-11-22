<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
	<script src="${ contextPath }/js/jquery-1.7.1.min.js"></script>
	<script src="${ contextPath }/js/jquery-ui-1.8.13.custom.js"></script>
	<link rel="stylesheet" type="text/css" href="${ contextPath }/themes/${ themeName }/jquery-ui.custom.css" />
	
	<script>
$(document).ready(function() {
	// UI styling
	$("body").css(".ui-helper-reset");
	$("#click-me").button();
});
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
		<li><a href="${ debugServletPath }">Debug Home</a></li>
		<li><a href="${ debugServletPath }/jquery-ui/index">JQueryUI</a></li>
	</ul>
</div>
<div id="content">
	<h1>A Test for JQueryUI</h1>
	<a id="click-me" href="#">Click Me</a>
</div>
<div id="footer">
	<a href="http://jqueryui.com/docs/Theming/API">JQueryUI doc</a>
</div>
</body>
</html>