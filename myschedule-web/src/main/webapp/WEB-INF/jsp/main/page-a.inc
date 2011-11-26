<%@page contentType="text/html" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html>
<head>
	<script src="${ contextPath }/js/jquery-1.7.1.min.js"></script>
	<script src="${ contextPath }/js/jquery-ui-1.8.13.custom.js"></script>
	<script src="${ contextPath }/js/jquery.dataTables-1.8.0.js"></script>
	
	<link rel="stylesheet" type="text/css" href="${ contextPath }/themes/${ themeName }/jquery-ui.custom.css" />
	<link rel="stylesheet" type="text/css" href="${ contextPath }/themes/${ themeName }/datatables-css/table_jui.css" />
	<link rel="stylesheet" type="text/css" href="${ contextPath }/themes/${ themeName }/main.css" />
	
	<script>
	$(document).ready(function() {	
		$("#menu").buttonset();
	});
	</script>
	
	<title>MySchedule</title>
</head>

<body class="ui-helper-reset" style="margin: 0px; padding: 0px; background-color: #666;">

<div id="topline"></div>

<div id="header">
	<div id="breadcrumbs-container" class="ui-state-default ui-corner-all">
		<ul id="breadcrumbs">
		<li><a href="${ mainPath }/dashboard/index">MySchedule Dashboard</a></li>
		<c:if test="${ not empty breadCrumbsScheduler }">
		<li> &#187 <a href="${ mainPath }/job/index">InMemoryQuartScheduler_$_NON_CLUSTER</a></li>
		</c:if>
		</ul>
	</div>
	
	<c:if test="${ not empty breadCrumbsScheduler }">
	<div id="menu-container">
	<div id="menu">
		<a href="${ mainPath }/job/list">Scheduler Jobs</a>
		<a href="${ mainPath }/job/load-xml">Add Jobs</a>
		<a href="${ mainPath }/scheduler/index">Settings</a>
		<a href="${ mainPath }/scripting/run">Tools</a>
	</div>
	</div>
	</c:if>
</div>

<div id="content">