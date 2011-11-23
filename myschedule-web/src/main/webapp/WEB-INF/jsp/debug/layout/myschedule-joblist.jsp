<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
	<script src="${ contextPath }/js/jquery-1.7.1.min.js"></script>
	<script src="${ contextPath }/js/jquery-ui-1.8.13.custom.js"></script>
	<link rel="stylesheet" type="text/css" href="${ contextPath }/themes/${ themeName }/jquery-ui.custom.css" />
	
	<script>
$(document).ready(function() {
	// Styling menu link as button
	$("#topmenu").buttonset();
	$("#submenu").tabs({
		ajaxOptions: {
			error: function( xhr, status, index, anchor ) {
				$( anchor.hash ).html("Failed to load content." );
			}
		}
	});
});
	</script>
	<style>
#myschedule-logo {
	font-size: 40px; font-family: Impact; border-bottom: 5px solid black
}
.menu {
	margin: 0px;
	padding: 0px;
}
.menu li {
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
<body class=".ui-helper-reset">
<div id="myschedule-logo">
	<span style="color: red;">MY</span><span style="color: black;">SCHEDULE</span>
</div>
<div id="topmenu" class="menu">
	<a href="${ debugServletPath }/layout/myschedule-joblist?m=1">Jobs</a>
	<a href="${ debugServletPath }/layout/myschedule-joblist?m=2">Settings</a>
	<a href="${ debugServletPath }/layout/myschedule-joblist?m=3">Scripting</a>
	<a href="${ debugServletPath }/layout/myschedule-joblist?m=4">Dashboard</a>
</div>

<div id="submenu" class="menu">
	<ul>
	<li><a href="${ debugActionPath }/tab-sample?p=1">Jobs with trigger</a></li>
	<li><a href="${ debugActionPath }/tab-sample?p=2">Jobs without trigger</a></li>
	<li><a href="${ debugActionPath }/tab-sample?p=3">Calendars</a></li>
	<li><a href="${ debugActionPath }/tab-sample?p=4">Currently Executing Jobs</a></li>
	<li><a href="${ debugActionPath }/tab-sample?p=5">Load Jobs (Xml)</a></li>
	</ul>
	
	<div id="tabs-1"></div>
</div>

<div id="footer">
	<a href="http://code.google.com/p/myschedule">myschedule-2.4.0-SNAPSHOT</a> with 
	<a href="http://quartz-scheduler.org">quartz-2.1.0</a>
</div>

</body>
</html>