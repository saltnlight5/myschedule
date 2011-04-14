<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:useBean id="threadDump" class="deng.hellowebapp.ThreadDumpBean" scope="request"/>

<html>
<body>
<h2>Thread Summary:</h2>
<table cellpadding="5" cellspacing="5">
  <tr>
    <th>Thread</th>
    <th>State</th>
    <th>Priority</th>
    <th>Daemon</th>
  </tr>
  <c:forEach items="${threadDump.threads}" var="thr">
    <tr>
      <td><a href="#${thr.id}">${thr.name}</a></td>
      <td>${thr.state}</td>
      <td>${thr.priority}</td>
      <td>${thr.daemon}</td>
    </tr>
  </c:forEach>
</table>

<h2>Stack Trace of JVM:</h2>
<c:forEach items="${threadDump.traces}" var="trace">
  <h4><a name="${trace.key.id}">${trace.key}</a></h4>
  <pre>
  <c:forEach items="${trace.value}" var="traceline">
      at ${traceline}</c:forEach>
  </pre>
</c:forEach>

</body>
</html>
