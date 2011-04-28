<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page session="false" %>
<html>
<head>
	<title>Scheduler</title>
</head>
<body>
<div>
<a href="show">Scheduler Info</a>
</div>
<h1>
	Create New Jobs/Triggers
</h1>
<p>Enter Spring xml beans that contains Trigger bean definitions.</p>
<form name="form" method="post" action="create-trigger">
	<textarea rows="20" cols="120" name="text">${ sampleTriggerBeans }</textarea>
	<br/>
	<input type="submit" value="Create"/>
</form>

</body>
</html>