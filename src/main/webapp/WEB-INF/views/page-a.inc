<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<html>
<head>

<link rel="stylesheet" type="text/css" href="${contextPath}/themes/${themeName}/page.css" />
<link rel="stylesheet" type="text/css" href="${contextPath}/themes/${themeName}/datatables-css/table_jui.css" />
<link rel="stylesheet" type="text/css" href="${contextPath}/themes/${themeName}/jquery-ui.custom.css" />

<script src="${contextPath}/js/jquery.js"></script>
<script src="${contextPath}/js/jquery-ui-custom.js"></script>
<script src="${contextPath}/js/jquery.dataTables.js"></script>

<title>MySchedule</title>

</head>

<body id="myschedule">
<div id="container">

<%@ include file="/WEB-INF/views/header.inc" %>