<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page session="false" %>
<html>
<head>
<style type="text/css">
table.sample {
	border-width: 1px;
	border-spacing: 2px;
	border-style: outset;
	border-color: gray;
	border-collapse: collapse;
	background-color: white;
}
table.sample th {
	border-width: 1px;
	padding: 1px;
	border-style: inset;
	border-color: gray;
	background-color: white;
	-moz-border-radius: ;
}
table.sample td {
	vertical-align: top;
	border-width: 1px;
	padding: 1px;
	border-style: inset;
	border-color: gray;
	background-color: white;
	-moz-border-radius: ;
}
.plaintext {
	white-space:pre;
	font-family:courier new;
}
</style>
<title>Scheduler</title>
</head>
<body>
<div>
<a href="list-scheduled-jobs">List of scheduled jobs</a>
<a href="create-trigger-form">Create Jobs</a>
</div>
<h1>
	Scheduler Information
</h1>
<table class="sample">
	<c:forEach items="${ schedulerMap }" var="item" varStatus="status">
	<tr>
		<td> ${ item.key }</td>
		<td class="plaintext"> ${ item.value }</td>
	</tr>
	</c:forEach>
</table>
</body>
</html>