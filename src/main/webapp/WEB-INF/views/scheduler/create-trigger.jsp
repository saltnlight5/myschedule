<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page session="false" %>
<html>
<head>
	<title>Scheduler</title>
</head>
<body>
<h1>
	Create Jobs/Trigger Processed
</h1>
<div>
<a href="list-scheduled-jobs">List of scheduled jobs</a>
<a href="show">Scheduler Info</a>
</div>

<div>
<p>${ formSubmitStatus }</p>
<table>
	<tr>
		<td> No. </td>
		<td> BeanId </td>
		<td> GROUP.TRIGGER_NAME </td>
	</tr>
	<c:forEach items="${ triggers }" var="item" varStatus="status">
	<tr>
		<td> ${ status.index + 1 }</td>
		<td> ${ item.key }</td>
		<td> ${ item.value.group }.${ item.value.name }</td>
	</tr>
	</c:forEach>
</table>
</div>

</body>
</html>