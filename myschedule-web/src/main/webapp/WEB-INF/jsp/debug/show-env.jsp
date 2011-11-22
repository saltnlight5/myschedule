<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head><title>MySchedule - Debug</title></head>
<body>
<div>
<h1>System Environment Variables</h1>
<table>
	<thead>
		<tr>
			<th>INDEX</th>
			<th>NAME</th>
			<th>VALUE</th>
		</tr>
	</thead>
	<tbody>
	<c:forEach items="${ env }" var="entry" varStatus="loop">
		<tr>
			<td>${ loop.index }</td>
			<td>${ entry.key }</td>
			<td>${ entry.value }</td>
		</tr>
	</c:forEach>
	</tbody>
</table>
</div>
</body>
</html>
