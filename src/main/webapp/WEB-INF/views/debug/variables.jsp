<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html>
<body>
<h1>JSP Implicit Variables:</h1>

<h2>Application</h2>
<table cellpadding="5" cellspacing="5">
  <tr>
    <th>Name</th>
    <th>Value</th>
  </tr>
  <c:forEach items="${applicationScope}" var="item">
    <tr>
      <td>${item.key}</td>
      <td>${item.value}</td>
    </tr>
  </c:forEach>
</table>

<h2>Session</h2>
<table cellpadding="5" cellspacing="5">
  <tr>
    <th>Name</th>
    <th>Value</th>
  </tr>
  <c:forEach items="${sessionScope}" var="item">
    <tr>
      <td>${item.key}</td>
      <td>${item.value}</td>
    </tr>
  </c:forEach>
</table>

<h2>Request</h2>
<table cellpadding="5" cellspacing="5">
  <tr>
    <th>Name</th>
    <th>Value</th>
  </tr>
  <c:forEach items="${requestScope}" var="item">
    <tr>
      <td>${item.key}</td>
      <td>${item.value}</td>
    </tr>
  </c:forEach>
</table>

</body>
</html>
