<%@page contentType="text/html" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html>
<head>
	<script src="${ contextPath }/js/jquery-1.7.1.min.js"></script>
	<script src="${ contextPath }/js/jquery-ui-1.8.13.custom.js"></script>
	<script src="${ contextPath }/js/jquery-breadcrumbs-r5.js"></script>
	<script src="${ contextPath }/js/jquery.dataTables-1.8.0.js"></script>
	
	<link rel="stylesheet" type="text/css" href="${ contextPath }/themes/${ themeName }/jquery-ui.custom.css" />
	<link rel="stylesheet" type="text/css" href="${ contextPath }/themes/${ themeName }/jquery-breadcrumbs-r5.css" />
	<link rel="stylesheet" type="text/css" href="${ contextPath }/themes/${ themeName }/datatables-css/table_jui.css" />
	<link rel="stylesheet" type="text/css" href="${ contextPath }/themes/${ themeName }/main.css" />
	
	<script>
	$(document).ready(function() {
		$("#breadcrumbs").breadcrumbs();
	});
	</script>
	
	<title>MySchedule</title>
</head>
<body class="ui-helper-reset">

<div id="topline"></div>

<div id="pagewrap">

<div id="header">
	<div id="myschedule-logo"><span>MY</span><span>SCHEDULE</span></div>
	<div id="breadcrums-container">
	<ul id="breadcrumbs">
	<li><a class="breadcrumb_e breadcrumb_icon_home" href="${ mainPath }"></a></li>
	<li><a href="${ mainPath }/dashboard/list">Dashboard</a></li>
	<c:if test="${ breadCrumbsScheduler }">
	<li><a href="${ mainPath }/job/list">${ sessionData.currentSchedulerName }</a></li>
	</c:if>
	</ul>
	</div>
</div>