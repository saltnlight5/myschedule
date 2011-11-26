<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
	<script src="${ contextPath }/js/jquery-1.7.1.min.js"></script>
	<script src="${ contextPath }/js/jquery-ui-1.8.13.custom.js"></script>
	<link rel="stylesheet" type="text/css" href="${ contextPath }/themes/${ themeName }/jquery-ui.custom.css" />
	<script>
$(document).ready(function() {
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
#footer { text-align: center; color: #555555; }
#footer a, #footer a:link, #footer a:visited { color: #555555; text-decoration: none; }
	</style>
	<title>Debug</title>
</head>
<body class="ui-helper-reset" style="margin: 0px; padding: 0px;">
<div id="topline"></div>
<div id="pagewrap">
<div id="header">
	<div id="myschedule-logo"><span>MY</span><span>SCHEDULE</span></div>
</div>
</div> <!--  #pagewrap -->
<div id="footer">
	You are running <code><a href="http://code.google.com/p/myschedule">myschedule-2.4.0-SNAPSHOT</a></code> with  
	<code><a href="http://quartz-scheduler.org">quartz-2.1.0</a></code> | by 
	<a href="http://code.google.com/p/zemiandeng">Zemian Deng</a>
</div>

</body>
</html>
