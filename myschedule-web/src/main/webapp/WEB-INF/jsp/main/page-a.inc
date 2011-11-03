<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<html>
<head>
<link rel="stylesheet" type="text/css" href="${contextPath}/themes/${themeName}/jquery-ui.custom.css" />
<link rel="stylesheet" type="text/css" href="${contextPath}/themes/${themeName}/main.css" />
<link rel="stylesheet" type="text/css" href="${contextPath}/themes/${themeName}/datatables-css/table_jui.css" />
<script src="${contextPath}/js/jquery-1.6.1.js"></script>
<script src="${contextPath}/js/jquery-ui-1.8.13.custom.js"></script>
<script src="${contextPath}/js/jquery.dataTables-1.8.0.js"></script>
<title>MySchedule</title>
</head>

<body>
<div id="body-container">
<%@ include file="/WEB-INF/jsp/main/header.inc" %>