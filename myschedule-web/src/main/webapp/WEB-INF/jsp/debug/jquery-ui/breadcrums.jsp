<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
	<script src="${ contextPath }/js/jquery-1.7.1.min.js"></script>
	<script src="${ contextPath }/js/jquery-ui-1.8.13.custom.js"></script>
	<link rel="stylesheet" type="text/css" href="${ contextPath }/themes/${ themeName }/jquery-ui.custom.css" />
	
	<script>
$(document).ready(function() {
	$("#test-nav4").buttonset();
});
	</script>
	<style>
#menu { padding: 10px, 10px, 10px, 10px; }
#menu li { display: inline; list-style-type: none; }
#footer { margin-top: 30px;	border-top: 1px solid black; }

.breadcrumbs { height: 35px; font-size: 20px; display:table-cell; vertical-align:middle; margin: 0px; padding: 0px 10px 0px 10px; }
.breadcrumbs li { display: inline; list-style-type: none; float:left; padding-right: 5px; }

	</style>
	<title>Debug</title>
</head>
<body class=".ui-helper-reset">
<div id="menu">
	<ul>
		<li><a href="${ debugPath }">Debug Home</a></li>
		<li><a href="index">JQueryUI</a></li>
	</ul>
</div>
<div id="content">

	<div class="ui-state-default ui-corner-all">
	<ul id="breadcrumbs" class="breadcrumbs">
		<li><a href="${ debugServletPath }/jquery-ui/list?p=a">Home</a> &#187</li>
		<li><a href="${ debugServletPath }/jquery-ui/list?p=b">Dashboard</a> &#187</li>
		<li><a href="${ debugServletPath }/jquery-ui/list?p=c">InMemoryQuartzScheduler_$_NON_CLUSTER</a></li>
	</ul>
	</div>
	
</div>
<div id="footer">
	<a href="http://jqueryui.com/docs/Theming/API">JQueryUI doc</a>
</div>
</body>
</html>