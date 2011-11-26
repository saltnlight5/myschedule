<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
	<script src="${ contextPath }/js/jquery-1.7.1.min.js"></script>
	<script src="${ contextPath }/js/jquery-ui-1.8.13.custom.js"></script>
	<link rel="stylesheet" type="text/css" href="${ contextPath }/themes/${ themeName }/jquery-ui.custom.css" />
	
	<script>
$(document).ready(function() {
});
	</script>
	<style>
#menu { padding: 10px, 10px, 10px, 10px; }
#menu li { display: inline; list-style-type: none; }
#footer { margin-top: 30px;	border-top: 1px solid black; }

#tabs-nav ul { margin-left: 0; list-style: none; padding-left: 0; padding-top: 6px; padding-bottom: 5px; border-bottom: 1px dashed #000; }
#tabs-nav ul li { display: inline; }
#tabs-nav ul a { border 1px dashed #000; border-bottom: none; padding: 5px 15px 5px 15px; margin-right: 5px; background-color: #EAEAEA; text-decoration: none; color: #333; }

#tabs-nav2 ul { margin-left: 0; list-style: none; padding-left: 0; padding-top: 6px; padding-bottom: 5px; border-bottom: 1px dashed #000; overflow: hidden; zoom: 1; }
#tabs-nav2 ul li { float: left; }
#tabs-nav2 ul a { display: block; width: 12em; text-align: center; border 1px dashed #000; border-bottom: none; padding: 5px 15px 5px 15px; margin-right: 5px; background-color: #EAEAEA; text-decoration: none; color: #333; }

#tabs-nav3 {
   float:left;
   width:100%;
   background:#fff;
   overflow:hidden;
   position:relative;
   border-bottom: 5px solid black;
}
#tabs-nav3 ul {
   clear:left;
   float:left;
   list-style:none;
   margin:0;
   padding:0;
   position:relative;
   left:50%;
   text-align:center;
}
#tabs-nav3 ul li {
   display:block;
   float:left;
   list-style:none;
   margin:0 5px 0 0;
   padding:0;
   position:relative;
   right:50%;
}
#tabs-nav3 ul li a {
   display:block;
   margin:0 0 0 1px;
   padding:3px 10px;
   background:#fff;
   color:#000;
   text-decoration:none;
   line-height:1.3em;
}
#tabs-nav3 ul li a:hover {
   background:#369;
   color:#fff;
}
#tabs-nav3 ul li a.active,
#tabs-nav3 ul li a.active:hover {
   color:#fff;
   background:#000;
   font-weight:bold;
}
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
	
	<div id="tabs-nav">
	<ul>
		<li><a href="list?p=a">Home</a></li>
		<li><a href="list?p=a">About Us</a></li>
		<li><a href="list?p=a">Corporate Philosophy</a></li>
		<li><a href="list?p=a">Store</a></li>
	</ul>
	</div>
	
	<div id="tabs-nav2">
	<ul>
		<li><a href="list?p=a">Home</a></li>
		<li><a href="list?p=a">About Us</a></li>
		<li><a href="list?p=a">Corporate Philosophy</a></li>
		<li><a href="list?p=a">Store</a></li>
	</ul>
	</div>
	
	<div id="tabs-nav3">
	<ul>
		<li class="ui-widget-header ui-corner-top"><a href="list?p=a">Home</a></li>
		<li class="ui-widget-content ui-corner-top"><a href="list?p=a">About Us</a></li>
		<li class="ui-widget-content ui-corner-top"><a href="list?p=a">Corporate Philosophy</a></li>
		<li class="ui-widget-content ui-corner-top"><a href="list?p=a">Store</a></li>
	</ul>
	</div>
	
</div>
<div id="footer">
	<a href="http://jqueryui.com/docs/Theming/API">JQueryUI doc</a>
</div>
</body>
</html>