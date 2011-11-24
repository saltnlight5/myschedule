<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
	<script src="${ contextPath }/js/jquery-1.7.1.min.js"></script>
	<script src="${ contextPath }/js/jquery-ui-1.8.13.custom.js"></script>
	<script src="${ contextPath }/js/jquery-breadcrumbs-r5.js"></script>
	<link rel="stylesheet" type="text/css" href="${ contextPath }/themes/${ themeName }/jquery-ui.custom.css" />
	<link rel="stylesheet" type="text/css" href="${ contextPath }/themes/${ themeName }/jquery-breadcrumbs-r5.css" />
	<script>
$(document).ready(function() {
	$("#breadcrumbs").breadcrumbs();
	$("#menu").buttonset();
	$("#tabs").tabs({
		ajaxOptions: {
			error: function( xhr, status, index, anchor ) {
				$( anchor.hash ).html("Failed to load content." );
			}
		}
	});
});
	</script>
	<style>	
#topline { background-color: black; width: 100%; height: 5px; }
#pagewrap { padding: 10px; }
#header { height: 50px; position: relative; margin-bottom: 30px; }
#myschedule-logo { font-size: 40px; font-family: Impact; position:absolute; bottom: 0px; }
#myschedule-logo span { color: red; }
#myschedule-logo span + span { color: black; }
#breadcrums-container { position:absolute; left: 220px; bottom: 5px; }
#breadcrums-container a, #breadcrums-container a:link, #breadcrums-container a:visited { color: #555555; text-decoration: none; }
#menu {	margin: 10px 0px 10px 0px; }
#menu li { display: inline; list-style-type: none; }
#footer { text-align: center; color: #555555; }
#footer a, #footer a:link, #footer a:visited { color: #555555; text-decoration: none; }
	</style>
	<title>Debug</title>
</head>
<body class=".ui-helper-reset" style="margin: 0px; padding: 0px;">
<div id="topline"></div>
<div id="pagewrap">
<div id="header">
	<div id="myschedule-logo"><span>MY</span><span>SCHEDULE</span></div>
	<div id="breadcrums-container">
	<ul id="breadcrumbs">
	<li><a class="breadcrumb_e breadcrumb_icon_home" href="${ debugServletPath }/layout/myschedule-joblist?m=0"></a></li>
	<li><a href="${ debugServletPath }/layout/myschedule-joblist?m=1">Dashboard</a></li>
	<li><a href="${ debugServletPath }/layout/myschedule-joblist?m=2">InMemoryQuartScheduler_$_NON_CLUSTER</a></li>
	</ul>
	</div>
</div>
<div id="menu">
	<a class="ui-state-highlight" href="${ debugServletPath }/layout/myschedule-joblist?m=1">Jobs</a>
	<a href="${ debugServletPath }/layout/myschedule-joblist?m=2">Settings</a>
	<a href="${ debugServletPath }/layout/myschedule-joblist?m=3">Scripting</a>
</div>
<div id="tabs">
	<ul>
	<li><a href="${ debugActionPath }/tab-sample?p=1">Jobs with trigger</a></li>
	<li><a href="${ debugActionPath }/tab-sample?p=2">Jobs without trigger</a></li>
	<li><a href="${ debugActionPath }/tab-sample?p=3">Calendars</a></li>
	<li><a href="${ debugActionPath }/tab-sample?p=4">Currently Executing Jobs</a></li>
	<li><a href="${ debugActionPath }/tab-sample?p=5">Load Jobs (Xml)</a></li>
	</ul>
	<div id="tabs-1"></div>
</div>
</div> <!--  #pagewrap -->
<div id="footer">
	You are running <code><a href="http://code.google.com/p/myschedule">myschedule-2.4.0-SNAPSHOT</a></code> with  
	<code><a href="http://quartz-scheduler.org">quartz-2.1.0</a></code> | by 
	<a href="http://code.google.com/p/zemiandeng">Zemian Deng</a>
</div>

</body>
</html>
