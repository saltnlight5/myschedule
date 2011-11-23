<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
	<script src="${ contextPath }/js/jquery-1.7.1.min.js"></script>
	<script src="${ contextPath }/js/jquery-ui-1.8.13.custom.js"></script>
	<link rel="stylesheet" type="text/css" href="${ contextPath }/themes/${ themeName }/jquery-ui.custom.css" />
	
	<script>
$(document).ready(function() {
	$("#jquery-menu").menu();
});
	</script>
	<style>
#menu { padding: 10px, 10px, 10px, 10px; }
#menu li { display: inline; list-style-type: none; }
#footer { margin-top: 30px;	border-top: 1px solid black; }
	</style>
	<title>Debug</title>
</head>
<body class=".ui-helper-reset">
<div id="menu">
	<ul>
		<li><a href="${ debugServletPath }">Debug Home</a></li>
		<li><a href="${ debugServletPath }/jquery-ui/index">JQueryUI</a></li>
	</ul>
</div>
<div id="content">

	<!--  JQueryUI Menu -->
	<ul id="jquery-menu">
		<li><a href="${ debugServletPath }/jquery-ui/list?p=a">TestA</a></li>
		<li><a href="${ debugServletPath }/jquery-ui/list?p=b">TestB</a></li>
		<li><a href="${ debugServletPath }/jquery-ui/list?p=c">TestC</a></li>
		<li><a href="${ debugServletPath }/jquery-ui/list?p=d">TestD</a></li>
	</ul>
	
</div>
<div id="footer">
	<a href="http://jqueryui.com/docs/Theming/API">JQueryUI doc</a>
</div>
</body>
</html>